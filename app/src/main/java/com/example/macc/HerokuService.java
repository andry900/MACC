package com.example.macc;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface HerokuService {
    @GET("/request")
    Call<ResponseBody> getRequest();
}
