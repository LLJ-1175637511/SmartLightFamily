package com.llj.smartlightinfamily

import android.app.Application
import com.llj.smartlightinfamily.utils.ToastUtils

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        ToastUtils.init(this.applicationContext)

        //lean
      /*  AVOSCloud.initialize(
            this.applicationContext,
            "fdtOt6omjWTjbwW3Q89KMoqH-gzGzoHsz",
            "cGQYnxrEsJHvf7YU2fyIrBa8",
            "https://fdtot6om.lc-cn-n1-shared.com"
        )
        PushService.startIfRequired(this)*/
    }

    companion object{
        var token = ""
        const val userId = "15356"
        const val appKey = "5f6273efb4"
        const val deviceId = "21759"
        const val clientId = "1084"
        const val clientSecret ="9fa72fcae1"
        const val leanCloudObjId = "606dbba28b555e6e97ed8d6a"
        const val leanCloudTableName = "MainDataBean"
    }
}