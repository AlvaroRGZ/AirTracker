package es.upm.miw.airtracker.api;

import java.util.List;

import es.upm.miw.airtracker.model.Result;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AirQualityRESTAPIService {


    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    // https://restcountries.com/v3.1/name/España
    // http://api.weatherapi.com/v1/current.json?key=0ed93a21f84e47b1a38203517230511&q=spain&aqi=no
    @GET("v1/current.json")
    Call<Result> getZoneLocation(@Query("key") String key, @Query("q") String q, @Query("aqi") String aqi);
}