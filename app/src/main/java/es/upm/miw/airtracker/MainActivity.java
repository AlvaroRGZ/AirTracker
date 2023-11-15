package es.upm.miw.airtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import es.upm.miw.airtracker.firebase.FirebaseClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static String LOG_TAG = "MiW";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final int RC_SIGN_IN = 2019;

    private FirebaseClient firebaseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.logoutButton).setOnClickListener(this);

        firebaseClient = new FirebaseClient();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // user is signed in
                CharSequence username = user.getEmail();

                firebaseClient.setCurrentUserUID(user.getUid());

                Toast.makeText(MainActivity.this, getString(R.string.firebase_user_fmt, username), Toast.LENGTH_LONG).show();
                Log.i(LOG_TAG, "onAuthStateChanged() " + getString(R.string.firebase_user_fmt, username));
                ((TextView) findViewById(R.id.textView)).setText(getString(R.string.firebase_user_fmt, username));

                // Cuando se verifica el login se va a la pantalla principal de usuario
                startActivity(new Intent(MainActivity.this, FavouritesActivity.class));
                Log.i(LOG_TAG, "[=>] Pantalla de favoritos");
            } else {
                // user is signed out
                startActivityForResult(
                        // Get an instance of AuthUI based on the default app
                        AuthUI.getInstance().
                                createSignInIntentBuilder().
                                setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.EmailBuilder().build()
                                )).
                                setIsSmartLockEnabled(!BuildConfig.DEBUG, true).
                                build(),
                        RC_SIGN_IN);
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Guarda el nuevo usuario en la base de datos
                firebaseClient.registerNewUserFromScratch(FirebaseAuth.getInstance().getCurrentUser());
                Toast.makeText(this, R.string.signed_in, Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "onActivityResult " + getString(R.string.signed_in));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.signed_cancelled, Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "onActivityResult " + getString(R.string.signed_cancelled));
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, DataActivity.class));
        Log.i(LOG_TAG, "[=>] Pantalla de datos");
    }

    public void goToFavourites() {
        startActivity(new Intent(MainActivity.this, FavouritesActivity.class));
        Log.i(LOG_TAG, "[=>] Pantalla de favoritos");
    }
}
