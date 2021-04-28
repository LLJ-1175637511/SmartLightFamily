package com.llj.smartlightinfamily.utils

import android.util.Log

object LogUtils {
    const val FLAG = true
    fun d(tag:String,msg:String){
        if (FLAG) Log.d(tag, msg)
    }
}