package com.example.mqttdevice.data

import com.example.mqttdevice.domain.MqttRepository
import com.example.mqttdevice.utils.MqttClient
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttRepositoryImpl(private val mqttClientInstance: MqttClient) : MqttRepository {

    private var mqttClient: MqttAndroidClient? = null

    override fun connect(
        url: String,
        connectionCallback: IMqttActionListener,
        mqttCallback: MqttCallback
    ) {
        mqttClient = mqttClientInstance.get(url)
        mqttClient?.setCallback(mqttCallback)
        mqttClient?.connect(MqttConnectOptions().apply {
            isAutomaticReconnect = true
        }, connectionCallback)
    }

    override fun subscribe(topic: String, qos: Int, subscribeCallback: IMqttActionListener) {
        if (mqttClient?.isConnected == true)
            mqttClient?.subscribe(topic, qos, null, subscribeCallback)
    }

    override fun unSubscribe(topic: String, unSubscribeCallback: IMqttActionListener) {
        if (mqttClient?.isConnected == true)
            mqttClient?.unsubscribe(topic, null, unSubscribeCallback)
    }

    override fun publish(
        topic: String,
        message: String,
        qos: Int,
        retained: Boolean,
        publishCallback: IMqttActionListener
    ) {
        if (mqttClient?.isConnected == true) {
            val mqttMessage = MqttMessage().apply {
                payload = message.toByteArray()
                this.qos = qos
                isRetained = retained
            }
            mqttClient?.publish(topic, mqttMessage, null, publishCallback)
        }
    }

    override fun publish(
        topic: String,
        message: ByteArray,
        qos: Int,
        retained: Boolean,
        publishCallback: IMqttActionListener
    ) {
        if (mqttClient?.isConnected == true) {
            val mqttMessage = MqttMessage().apply {
                payload = message
                this.qos = qos
                isRetained = retained


            }
            mqttClient?.publish(topic, mqttMessage, null, publishCallback)
        }
    }

    override fun disconnect(disconnectCallback: IMqttActionListener) {
        mqttClient?.disconnect(null, disconnectCallback)
    }

}