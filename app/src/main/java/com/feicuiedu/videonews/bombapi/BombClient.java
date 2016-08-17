package com.feicuiedu.videonews.bombapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BombClient {

    private static BombClient sInstance;

    public static synchronized BombClient getsInstance() {
        if (sInstance == null) {
            sInstance = new BombClient();
        }
        return sInstance;
    }

    private VideoApi videoApi;

    private BombClient() {

        // 日志拦截器
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 构建OkHttp
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new BombInterceptor()) // 用来统一处理bomb必要头字段的拦截器
                .addInterceptor(httpLoggingInterceptor) // 日志拦截器
                .build();

        // 让Gson能将bomb返回的时间戳自动转为date对象
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                // bomb服务器baseurl
                .baseUrl("https://api.bmob.cn/")
                // Gson转换器
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        videoApi = retrofit.create(VideoApi.class);
    }

    public VideoApi getVideoApi() {
        return videoApi;
    }
}
