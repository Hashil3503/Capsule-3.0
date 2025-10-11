package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ChatService {

    @Headers({
            "Content-Type: application/json"
    })
    @POST("v1/chat/completions")
    Call<ChatResponse> getChatCompletion(@Body ChatRequest request);

}
