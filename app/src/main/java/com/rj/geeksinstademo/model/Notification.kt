package com.rj.geeksinstademo.model

data class Notification(val userid: String? = "",val text: String? = "",val postid: String? = "",val isPost: Boolean?=false)
{
    fun getIsPost(): Boolean? {
        // this is needed to trick Kotlin into using this getter instead of generating its own which breaks firebase
        return isPost
    }
}