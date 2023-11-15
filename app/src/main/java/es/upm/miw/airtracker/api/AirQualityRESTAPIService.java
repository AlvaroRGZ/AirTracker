package es.upm.miw.airtracker.api;

import es.upm.miw.airtracker.model.Weather;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AirQualityRESTAPIService {
    // http://api.weatherapi.com/v1/current.json?key=xxxxxxxxx&q=spain&aqi=no
    @GET("v1/current.json")
    Call<Weather> getZoneLocation(@Query("key") String key, @Query("q") String q, @Query("aqi") String aqi);
}