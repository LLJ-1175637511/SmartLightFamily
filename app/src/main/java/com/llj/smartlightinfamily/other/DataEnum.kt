package com.llj.smartlightinfamily.other

enum class FamilyModeEnum{ //正常（手动模式） 离开 休息 智能（自动模式）
    COMMON,LEAVE,REST,SMART
}

enum class WebSocketType {
    CONNECT_INIT, CONNECT_BIGIOT, NOT_CONNECT_BIGIOT, USER_LOGIN, USER_LOGOUT, DEVICE_ONLINE, DEVICE_OFFLINE
}