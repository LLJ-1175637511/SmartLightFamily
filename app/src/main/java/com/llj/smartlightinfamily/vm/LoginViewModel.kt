package com.llj.smartlightinfamily.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.llj.smartlightinfamily.utils.Const
import com.llj.smartlightinfamily.utils.ToastUtils
import com.llj.smartlightinfamily.MyApplication
import com.llj.smartlightinfamily.R
import com.llj.smartlightinfamily.other.trySuspendExceptFunction
import com.llj.smartlightinfamily.net.repository.AccessRepository
import com.llj.smartlightinfamily.other.save
import kotlinx.coroutines.Dispatchers

class LoginViewModel(application: Application, savedStateHandle: SavedStateHandle) :
    BaseViewModel(application, savedStateHandle) {

    private val TAG = this.javaClass.simpleName

    val passWordLiveData = MutableLiveData<String>("")
    val userNameLiveData = MutableLiveData<String>("")
    private val _rememberPwdLiveData = MutableLiveData<Boolean>(false)
    val rememberPwdLiveData: LiveData<Boolean> = _rememberPwdLiveData

    private val _loadTokenSucLiveData = MutableLiveData<String>()
    val loadTokenSucLiveData: LiveData<String> = _loadTokenSucLiveData

    fun login() {
        val input = checkInfo()
        if (!input) return
        trySuspendExceptFunction(Dispatchers.IO) {
            val tokenBean = AccessRepository.requestToken()
            val token = tokenBean.access_token
            if (token.isNotEmpty()) {
                savedSp()
                _loadTokenSucLiveData.postValue(token)
            }
        }
    }

    fun setRememberPwd(boolean: Boolean) {
        _rememberPwdLiveData.postValue(boolean)
    }

    private fun checkInfo(): Boolean {
        getApp().apply {
            if (userNameLiveData.value.isNullOrEmpty()) {
                ToastUtils.toastShort(resources.getString(R.string.user_name_is_null))
                return false
            }
            if (passWordLiveData.value.isNullOrEmpty()) {
                ToastUtils.toastShort(resources.getString(R.string.user_pwd_is_null))
                return false
            }
        }
        return true
    }

    fun loadUserData() {
        getSP(Const.SPUser).let { sp ->
            if (sp.contains(Const.SPUserRememberPwdLogin)) {
                val spIsSaved = sp.getBoolean(Const.SPUserRememberPwdLogin, false)//是否记住密码
                if (spIsSaved) { //如果保存 则赋值给界面
                    if (sp.contains(Const.SPUserNameLogin)) {
                        userNameLiveData.postValue(sp.getString(Const.SPUserNameLogin, ""))
                    }
                    if (sp.contains(Const.SPUserPwdLogin)) {
                        passWordLiveData.postValue(sp.getString(Const.SPUserPwdLogin, ""))
                    }
                }
                _rememberPwdLiveData.postValue(spIsSaved)
            }
        }
    }

    /**
     * 保存用户名 密码
     */
    private fun savedSp() {
        getSP(Const.SPUser).save {
            putString(Const.SPUserPwdLogin, userNameLiveData.value)
            putString(Const.SPUserNameLogin, passWordLiveData.value)
            putBoolean(Const.SPUserRememberPwdLogin, rememberPwdLiveData.value!!)
        }
    }

    private fun getApp() = getApplication<MyApplication>()

}