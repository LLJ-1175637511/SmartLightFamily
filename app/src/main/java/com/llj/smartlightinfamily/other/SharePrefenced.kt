package com.llj.smartlightinfamily.other

import android.content.SharedPreferences

/**
 * 仿kotlin-ktx库 利用高阶函数建立一个save扩展函数 简化sp的使用
 */
fun SharedPreferences.save(block: SharedPreferences.Editor.() -> Unit): Boolean {
    val edit = edit()
//    edit.block()
    block(edit)
    return edit.commit()
}

fun SharedPreferences.search(block: SharedPreferences.Editor.() -> Unit): Boolean {
    val edit = edit()
//    edit.block()
    block(edit)
    return edit.commit()
}
