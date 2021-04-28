package com.llj.smartlightinfamily.net

import com.llj.smartlightinfamily.utils.LogUtils
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

open class JWebSocketClient (serverUri: URI?) :
    WebSocketClient(serverUri, Draft_6455()) {
    override fun onOpen(handshakedata: ServerHandshake) {
        LogUtils.d("JWebSocketClient", "onOpen()")
    }

    override fun onMessage(message: String) {
        LogUtils.d("JWebSocketClient", "onMessage()")
    }

    override fun onClose(
        code: Int,
        reason: String,
        remote: Boolean
    ) {
        LogUtils.d("JWebSocketClient", "onClose()")
    }

    override fun onError(ex: Exception) {
        LogUtils.d("JWebSocketClient", "onError()")
    }
}