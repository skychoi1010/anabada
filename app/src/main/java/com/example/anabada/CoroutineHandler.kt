package com.example.anabada

sealed class CoroutineHandler<out T> {

    data class Success<out T>(val successData : T) : CoroutineHandler<T>()
    class Error(val exception: java.lang.Exception, val message: String = exception.localizedMessage)
        : CoroutineHandler<Nothing>()

}