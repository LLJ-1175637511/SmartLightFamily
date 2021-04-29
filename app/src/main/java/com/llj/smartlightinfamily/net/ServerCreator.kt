package com.llj.smartlightinfamily.net

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServerCreator {

    private const val BASE_URL = "https://www.bigiot.net/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(server:Class<T>): T = retrofit.create(server)

    inline fun <reified T> create():T = create(T::class.java)
}