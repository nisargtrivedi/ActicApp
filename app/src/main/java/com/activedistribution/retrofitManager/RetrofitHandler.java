package com.activedistribution.retrofitManager;

import com.activedistribution.BuildConfig;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHandler {

    private static RetrofitHandler uniqInstance;

    private ApiInterface apiInterface;

    public static synchronized RetrofitHandler getInstance() {
        if (uniqInstance == null) {
            uniqInstance = new RetrofitHandler();
        }
        return uniqInstance;
    }

    public static synchronized RetrofitHandler getNewInstanceOnLogin() {
        uniqInstance = new RetrofitHandler();
        return uniqInstance;
    }


    public void ApiClient() {
        try {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addNetworkInterceptor(header)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();

            apiInterface = retrofit.create(ApiInterface.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ApiInterface getApi() {
        if (apiInterface == null) {
            uniqInstance.ApiClient();
        }
        return apiInterface;
    }

    Interceptor header = chain -> {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("Content-Type","application/x-www-form-urlencoded");
        builder.addHeader("version", "1.0");
        return chain.proceed(builder.build());
    };

}
