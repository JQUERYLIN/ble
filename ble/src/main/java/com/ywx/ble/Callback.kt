package com.ywx.ble

interface Callback {
    fun onSuccess(result: String)
    fun onSendSuccess()
    fun onError()
}