package com.llj.smartlightinfamily.bean

data class MainDataBean(
    val temp:Float,
    val humi:Float,
    val bedWetting:Boolean,
    val bedHeader:Boolean,
    val bedEnd: Boolean,
    val tilt: Boolean
)