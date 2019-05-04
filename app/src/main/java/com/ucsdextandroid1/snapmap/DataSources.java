package com.ucsdextandroid1.snapmap;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by rjaylward on 2019-04-27
 */
public class DataSources {

    private static final String TAG = DataSources.class.getSimpleName();

    private static DataSources instance;

    private DataApi dataApi;

    public DataSources() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ActiveLocationsUserResponse.class, new ActiveUserLocationsDeserializer())
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getAnnotation(RemoveFromJson.class) !=null;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
        this.dataApi = new Retrofit.Builder()
                .baseUrl("https://ucsd-ext-android-rja-1.firebaseio.com/apps/snap/")//base where info came from
                .addConverterFactory(GsonConverterFactory.create(gson))//converts info into code
                .build()
                .create(DataApi.class);
    }


    public static DataSources getInstance() {
        if (instance == null)
            instance = new DataSources();

        return instance;
    }

    public void getStaticUserLocations(Callback<List<UserLocationData>> callback) {

        dataApi.getStaticUserLocations().enqueue(new retrofit2.Callback<List<UserLocationData>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserLocationData>> call, @NonNull Response<List<UserLocationData>> response) {
                if (response.isSuccessful())
                    callback.onDataFetched(response.body());
                else
                    callback.onDataFetched(Collections.emptyList());
            }

            @Override
            public void onFailure(@NonNull Call<List<UserLocationData>> call, @NonNull Throwable t) {
                Log.e(TAG, "DataApi error", t);
                callback.onDataFetched(Collections.emptyList());
            }
        });
    }

    public void getAppName(Callback<String> callBack) {
        dataApi.getAppName().enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    callBack.onDataFetched(response.body());

                } else {
                    callBack.onDataFetched("faliure");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callBack.onDataFetched("faliure");
            }
        });
    }

    public void getActiveUserLocations(Callback<List<UserLocationData>> callback) {
        dataApi.getActiveLocationsUserLocations().enqueue(new retrofit2.Callback<ActiveLocationsUserResponse>() {
            @Override
            public void onResponse(Call<ActiveLocationsUserResponse> call, Response<ActiveLocationsUserResponse> response) {
                if (response.isSuccessful()) {
                    callback.onDataFetched(response.body().getUserLocations());
                } else {
                    Log.e("dataSource", "response was not successful");
                    callback.onDataFetched(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<ActiveLocationsUserResponse> call, Throwable t) {
                Log.e("dataSource", "response Failed", t);
                callback.onDataFetched(Collections.emptyList());
            }
        });
    }

    public void updateUser(String userID, UserLocationData locationData, Callback<UserLocationData> callback) {
        dataApi.updateUserLocation(userID, locationData).enqueue(new retrofit2.Callback<UserLocationData>() {
            @Override
            public void onResponse(Call<UserLocationData> call, Response<UserLocationData> response) {
                if (response.isSuccessful()) {
                    callback.onDataFetched(response.body());


                } else {
                    Log.e("dataSource", "response was not successful");
                    callback.onDataFetched(null);

                }
            }

            @Override
            public void onFailure(Call<UserLocationData> call, Throwable t) {
                Log.e("dataSource", "response Failed", t);
                callback.onDataFetched(null);
            }
        });
    }

    public interface Callback<T> {
        void onDataFetched(T data);
    }

    private interface DataApi {
        @GET("static_user_locations.json")
        Call<List<UserLocationData>> getStaticUserLocations();

        @GET("active_user_locations.json")
        Call<ActiveLocationsUserResponse> getActiveLocationsUserLocations();

        @GET("app_name.json")
        Call<String> getAppName();

        @PATCH("active_user_locations/{user_id}.json")
//posts data to the server
        Call<UserLocationData> updateUserLocation(//updating the API
                                                  @Path("user_id") String userId,
                                                  @Body UserLocationData userLocationData);
    }
}
