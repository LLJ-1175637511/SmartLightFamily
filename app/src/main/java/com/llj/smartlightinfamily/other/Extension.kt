package com.llj.smartlightinfamily.other

import androidx.lifecycle.*
import com.llj.smartlightinfamily.utils.LogUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * 便捷添加观察事件
 */
fun <T> LiveData<T>.baseObserver(lifecycleOwner: LifecycleOwner, block: (T) -> Unit) {
    this.observe(lifecycleOwner, Observer { block(it) })
}

fun ViewModel.trySuspendExceptFunction(
    threadType: CoroutineDispatcher = Dispatchers.Default,
    block: suspend () -> Unit
){
    try {
        viewModelScope.launch(threadType) {
            block()
        }
    } catch (e: InterruptedException) {
        e.printStackTrace()
        LogUtils.d("JWebSocketClient", e.message.toString())
    }
}
