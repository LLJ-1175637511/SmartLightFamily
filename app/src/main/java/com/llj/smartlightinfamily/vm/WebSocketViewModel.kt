package com.llj.smartlightinfamily.vm

import android.app.Application
import androidx.lifecycle.*
import com.llj.smartlightinfamily.utils.LogUtils
import com.llj.smartlightinfamily.bean.UserConfigBean
import com.llj.smartlightinfamily.other.WebSocketType
import com.llj.smartlightinfamily.other.trySuspendExceptFunction
import com.llj.smartlightinfamily.net.JWebSocketClient
import com.llj.smartlightinfamily.net.repository.AccessRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URI

abstract class WebSocketViewModel(
    private val accessConfig: UserConfigBean,
    application: Application
) : AndroidViewModel(application) {

    private val TAG = this.javaClass.simpleName

    private val userNameFlag = """"ID":"U${accessConfig.userId}""""
    private val deviceIdFlag = """"ID":"D${accessConfig.deviceId}""""
    private val connectSucFlag = """{"M":"WELCOME TO BIGIOT"}"""
    private val loginFlag = """"M":"login""""
    private val loginSucFlag = """"M":"loginok""""
    private val receiveDataSucFlag = """"M":"update""""
    private val logoutFlag = """"M":"logout""""

    private val _toastLiveData = MutableLiveData<String>()
    val toastLiveData: LiveData<String> = _toastLiveData

    private val _receiveDeviceDataLiveData = MutableLiveData<String>()
    val receiveDeviceDataLiveData: LiveData<String> = _receiveDeviceDataLiveData

    private val _canSendOrder = MutableLiveData<Boolean>(false)
    private val canSendOrder: LiveData<Boolean> = _canSendOrder

    private val _webState = MutableLiveData<WebSocketType>(WebSocketType.CONNECT_INIT)
    val webState: LiveData<WebSocketType> = _webState

    private val webSocket by lazy {
        val webUri = URI.create(BIGIOT)
        object : JWebSocketClient(webUri) {

            override fun onMessage(message: String) {
                //message就是接收到的消息
                if (message.contains(loginFlag) && message.contains(deviceIdFlag)) {
                    _webState.postValue(WebSocketType.DEVICE_ONLINE)
                }else if (message.contains(loginSucFlag)&&message.contains(userNameFlag)) {
                    _webState.postValue(WebSocketType.USER_LOGIN)
                } else if (message.contains(receiveDataSucFlag)) {
                    _webState.postValue(WebSocketType.DEVICE_ONLINE)
                    _receiveDeviceDataLiveData.postValue(message)
//                    notifyAnalysisJson(message)
                } else if (message.contains(logoutFlag)) {
                    if (message.contains(userNameFlag)) _webState.postValue(WebSocketType.USER_LOGOUT)
                    else _webState.postValue(WebSocketType.DEVICE_OFFLINE)
                } else if (message.contains(connectSucFlag)) {
                    _webState.postValue(WebSocketType.CONNECT_BIGIOT)
                } else {
                }
//                setToast(message)
                LogUtils.d("JWebSocketClient", message)
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {
                _webState.postValue(WebSocketType.NOT_CONNECT_BIGIOT)
            }
        }
    }

    fun setToast(str: String) {
        _toastLiveData.postValue(str)
    }

    fun checkUserAndDeviceStatus() {
        var getFirstStatus = false
        var operasTime = System.currentTimeMillis()
        val retryTime = 1000 * 2
        var isConnecting = false
        var isUserLogining = false
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(100)
                val cTime = System.currentTimeMillis()
                when (webState.value) {
                    WebSocketType.CONNECT_INIT -> {
                        _canSendOrder.postValue(false)
                        if (!isConnecting) {
                            connectBigIot()
                            isConnecting = true
                        }
                        if (cTime - operasTime > retryTime) { //每s检测一次
                            isConnecting = false
                            operasTime = cTime
                        }
                    }
                    WebSocketType.NOT_CONNECT_BIGIOT -> {
                        _canSendOrder.postValue(false)
                        if (!isConnecting) {
                            reConnectBigIot()
                            isConnecting = true
                        }
                        if (cTime - operasTime > retryTime) { //每s检测一次
                            isConnecting = false
                            operasTime = cTime
                        }
                    }
                    WebSocketType.CONNECT_BIGIOT -> {
                        _canSendOrder.postValue(false)
                        if (!isUserLogining) {
                            loginBigIot()
                            isUserLogining = true
                        }
                        if (cTime - operasTime > retryTime) { //每s检测一次
                            isUserLogining = false
                            operasTime = cTime
                        }
                    }
                    WebSocketType.USER_LOGOUT -> {
                        _canSendOrder.postValue(false)
                        if (!isUserLogining) {
                            loginBigIot()
                            isUserLogining = true
                        }
                        if (cTime - operasTime > retryTime) { //每s检测一次
                            isUserLogining = false
                            operasTime = cTime
                        }
                    }
                    WebSocketType.USER_LOGIN -> {
                        _canSendOrder.postValue(false)
                        if (!getFirstStatus){
                            getFirstStatus = true
                            getDeviceFirstStatus()
                        }
                    }
                    WebSocketType.DEVICE_OFFLINE -> {
                        _canSendOrder.postValue(false)

                    }
                    WebSocketType.DEVICE_ONLINE -> {
                        if (canSendOrder.value == false) {
                            _canSendOrder.postValue(true)
                            setToast("设备已连接")
                        }
                    }
                }
            }
        }
    }


    private fun getDeviceFirstStatus() = trySuspendExceptFunction(Dispatchers.IO) {
        LogUtils.d("JWebSocketClient", "getDeviceFirstStatus")
        val deviceOL = AccessRepository.requestDeviceOL()
        LogUtils.d("JWebSocketClient", deviceOL.toString())
        LogUtils.d(TAG,deviceOL.toString())
        if (deviceOL.online=="1") { //在线
            _webState.postValue(WebSocketType.DEVICE_ONLINE)
        }
    }

    @Synchronized
    private fun reConnectBigIot() = trySuspendExceptFunction(Dispatchers.IO) {
        if (webSocket.isClosed) {
            webSocket.reconnect()
            LogUtils.d("JWebSocketClient", "reconnec")
        }
    }

    @Synchronized
    private fun connectBigIot() = trySuspendExceptFunction(Dispatchers.IO) {
        LogUtils.d("JWebSocketClient", "connectBigIot()")
        webSocket.connect()
    }

    @Synchronized
    private fun loginBigIot() {
        LogUtils.d("JWebSocketClient", "loginBigIot()")
        sendMessage("""{"M":"login","ID":"${accessConfig.userId}","K":"${accessConfig.appKey}"}""")
    }

    fun sendOrderToDevice(content: String) {
        sendMessage("""{"M":"say","ID":"D${accessConfig.deviceId}","C":"$content","SIGN":"llj"}""")
    }

    private fun sendMessage(str: String) {
        if (webSocket.isOpen) {
            LogUtils.d("JWebSocketClient", "send:${str}")
            webSocket.send(str)
        } else {
            setToast("当前webSocket已关闭")
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (webSocket.isOpen) webSocket.close()
    }

    companion object {
        private const val BIGIOT = "wss://www.bigiot.net:8484"
    }
}