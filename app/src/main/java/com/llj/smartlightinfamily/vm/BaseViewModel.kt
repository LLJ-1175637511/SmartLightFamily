package com.llj.smartlightinfamily.vm

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.llj.smartlightinfamily.utils.Const
import com.llj.smartlightinfamily.net.repository.AccessRepository
import com.llj.smartlightinfamily.other.save
import com.llj.smartlightinfamily.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val TAG = this.javaClass.simpleName

    private val _toastLiveData = MutableLiveData<String>()
    val toastLiveData:LiveData<String> = _toastLiveData

    fun setToast(str:String){
        _toastLiveData.postValue(str)
    }

    private suspend fun getToken() =
        withContext<String>(Dispatchers.IO) {
            val sp = getSP(Const.SPBaiduToken)
            if (sp.contains(Const.SPBaiduTokenPeriod)) {
                val periodTime = sp.getLong(Const.SPBaiduTokenPeriod, 0L)
                val currentTime = System.currentTimeMillis() / 1000
                LogUtils.d(TAG, "periodTime:${periodTime} currentTime:${currentTime}")
                if (periodTime - 60 * 60 * 24 * 2 > currentTime) {
                    return@withContext getSP(Const.SPBaiduToken).getString(
                        Const.SPBaiduTokenString,
                        ""
                    ).toString()//如果未过期 则不需要请求token
                }
            }

            val tokenBean = AccessRepository.requestToken()
            LogUtils.d(TAG, "suc:${tokenBean}")
            if (tokenBean.access_token!="") { //请求成功则保存到sp中
                val savedSp = getSP(Const.SPBaiduToken).save {
                    putString(Const.SPBaiduTokenString, tokenBean.access_token)
                    val periodTime = System.currentTimeMillis() / 1000 + tokenBean.expires_in
                    putLong(Const.SPBaiduTokenPeriod, periodTime)
                }
                return@withContext if (savedSp) {
                    return@withContext getSP(Const.SPBaiduToken).getString(
                        Const.SPBaiduTokenString,
                        ""
                    ).toString()
                } else {
                    false.toString()
                }
            } else {
                LogUtils.d(TAG, "err:${tokenBean}")
                return@withContext false.toString()
            }
        }

    fun getSP(key: String): SharedPreferences =
        getApplication<Application>().getSharedPreferences(key, Context.MODE_PRIVATE)

    fun vmRequest(block:() -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                block()
            }catch (e:Exception){
                e.printStackTrace()
                _toastLiveData.postValue(e.message.toString())
            }
        }
    }
}