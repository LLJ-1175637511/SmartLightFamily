package com.llj.smartlightinfamily.net.server

import com.llj.smartlightinfamily.MyApplication
import com.llj.smartlightinfamily.bean.DeviceOLStatus
import com.llj.smartlightinfamily.bean.TokenBean
import retrofit2.Call
import retrofit2.http.*

interface AccessToken {

    @FormUrlEncoded
    @POST("oauth/token")
    fun getShellAccessInfo(
        @FieldMap map: Map<String, String>,
        @Header("Content-Type") Content_Type: String = "application/x-www-form-urlencoded"
    ): Call<TokenBean>

}

interface DeviceOL {

    @GET("oauth/dev")
    fun getDeviceOL(
        @Query("access_token") accessToken:String = MyApplication.token,
        @Query("id") id:String = MyApplication.deviceId
    ): Call<DeviceOLStatus>

}