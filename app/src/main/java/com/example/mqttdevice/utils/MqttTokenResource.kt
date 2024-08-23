package com.example.mqttdemo.utils


sealed class MqttResource<T>(val mqttToke: T? = null, val message: String? = null) {
    class Success<T>(mqttToke: T?,topics:String? = null) : MqttResource<T>(mqttToke = mqttToke, message =topics)
    class Failure<T>(mqttToke: T? = null, message: String?) : MqttResource<T>(mqttToke = mqttToke, message = message)
    class NoData<T>:MqttResource<T>()
}