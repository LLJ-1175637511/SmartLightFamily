package com.llj.smartlightinfamily.utils

object NetParamsUtils {

    fun buildToken(
        client_id: String,
        client_secret: String,
        username:String,
        password:String
    ):Map<String,String> = mutableMapOf<String,String>().apply {
        put("grant_type","password")
        put("client_id",client_id)
        put("client_secret",client_secret)
        put("username",username)
        put("password",password)
    }


}