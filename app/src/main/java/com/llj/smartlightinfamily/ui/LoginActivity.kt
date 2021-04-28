package com.llj.smartlightinfamily.ui

import androidx.activity.viewModels
import com.llj.smartlightinfamily.other.baseObserver
import com.llj.smartlightinfamily.vm.LoginViewModel
import com.llj.smartlightinfamily.utils.ToastUtils
import com.llj.smartlightinfamily.MyApplication
import com.llj.smartlightinfamily.R
import com.llj.smartlightinfamily.databinding.SmartlightLoginBinding

class LoginActivity : BaseActivity<SmartlightLoginBinding>() {

    override fun getLayoutId(): Int = R.layout.smartlight_login
    override fun buildToolbar()= ToolbarConfig(title = "登录界面", isShowBack = false, isShowMenu = false)
    override fun showToolbar() = false

    private val viewModel by viewModels<LoginViewModel>()

    override fun init() {
        initVM()
        initListener()
    }

    private fun initVM() {
        getDataBinding().loginVm = viewModel

        viewModel.rememberPwdLiveData.baseObserver(this) {
            getDataBinding().cbRememPwdLogin.isChecked = it
        }

        viewModel.toastLiveData.baseObserver(this) {
            ToastUtils.toastShort(it)
        }

        viewModel.loadTokenSucLiveData.baseObserver(this){
            val token = MyApplication.token
            if (token.isEmpty()) MyApplication.token = it
            startCommonActivity<MainActivity>()
            finish()
        }

        viewModel.loadUserData()
    }

    private fun initListener() {
        getDataBinding().cbRememPwdLogin.setOnClickListener {
            viewModel.setRememberPwd(getDataBinding().cbRememPwdLogin.isChecked)
        }

        getDataBinding().btLogin.setOnClickListener {
            viewModel.login()
        }

    }

}