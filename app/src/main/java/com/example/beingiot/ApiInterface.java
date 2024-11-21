package com.example.beingiot;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    @GET("/received_temphumi")
    Call<String> getData();
    @POST("/send_power")
    Call<Void> sendPowerCommand(@Body CommandPowerData commandPowerData);
    @POST("/send_power")
    Call<Void> sendModeCommand(@Body CommandModeData commandModeData);
}
