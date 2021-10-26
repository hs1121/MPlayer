package com.example.mplayer.Utility

class Event<T>(val data:T) {
    private var isHandled=false

    fun isHandled()=isHandled

    fun handle(){isHandled=true}

    fun peekContent()=data

}