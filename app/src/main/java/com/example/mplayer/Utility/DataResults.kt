package com.example.mplayer.Utility

sealed class DataResults<T> (
    val data: T?=null,
    val message:String?=null
){

    class Success<T>(data: T): DataResults<T>(data)
    class Error<T>(message: String?,data: T?=null):DataResults<T>(data,message)
}