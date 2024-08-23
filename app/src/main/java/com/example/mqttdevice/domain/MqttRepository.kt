package com.example.mqttdevice.domain

import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.MqttCallback

interface MqttRepository {

    fun connect(url:String,connectionCallback: IMqttActionListener,mqttCallback: MqttCallback)

    fun subscribe(topic:String,qos:Int, subscribeCallback:IMqttActionListener)

    fun unSubscribe(topic: String, unSubscribeCallback:IMqttActionListener)

    fun publish(topic:String, message:String, qos:Int, retained:Boolean = false, publishCallback:IMqttActionListener)

    fun publish(topic:String, message:ByteArray, qos:Int, retained:Boolean = false, publishCallback:IMqttActionListener)

    fun disconnect(disconnectCallback:IMqttActionListener)

}