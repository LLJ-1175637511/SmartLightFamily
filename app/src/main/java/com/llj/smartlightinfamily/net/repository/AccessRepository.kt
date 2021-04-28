package com.llj.smartlightinfamily.net.repository

import com.llj.smartlightinfamily.MyApplication
import com.llj.smartlightinfamily.bean.DeviceOLStatus
import com.llj.smartlightinfamily.bean.TokenBean
import com.llj.smartlightinfamily.net.ServerCreator
import com.llj.smartlightinfamily.net.server.AccessToken
import com.llj.smartlightinfamily.net.server.DeviceOL
import com.llj.smartlightinfamily.utils.NetParamsUtils

object AccessRepository {

    private val tokenSever = ServerCreator.create<AccessToken>()
    private val deviceOLSever = ServerCreator.create<DeviceOL>()

    fun requestToken(): TokenBean {
        val tokenParams = NetParamsUtils.buildToken(
            client_id = MyApplication.clientId,
            client_secret = MyApplication.clientSecret,
            username = MyApplication.userId,
            password = MyApplication.appKey
        )
        val tokenRequest = tokenSever.getShellAccessInfo(tokenParams).execute()
        if (!tokenRequest.isSuccessful) {
            throw Exception(tokenRequest.message())
        }
        val bean = tokenRequest.body()
        return bean ?: throw Exception("data is null")
    }

    fun requestDeviceOL(): DeviceOLStatus {
        val deviceOLRequest = deviceOLSever.getDeviceOL().execute()
        if (!deviceOLRequest.isSuccessful) {
            throw Exception(deviceOLRequest.message())
        }
        val bean = deviceOLRequest.body()
        return bean ?: throw Exception("data is null")
    }
}