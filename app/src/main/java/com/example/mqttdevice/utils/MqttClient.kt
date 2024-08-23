package com.example.mqttdevice.utils

import android.content.Context
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttClient
import java.net.NetworkInterface
import java.net.SocketException

class MqttClient(private val context: Context){

    fun get(url:String):MqttAndroidClient{
        return MqttAndroidClient(context,url/*"tcp://broker.hivemq.com:1883"*/, "client_${getCurrentIPAddress()}")
    }


    private fun getCurrentIPAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val inetAddress = addresses.nextElement()
                    // Look for IPv4 addresses and exclude the loopback address
                    if (!inetAddress.isLoopbackAddress && inetAddress.hostAddress.contains(".")) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return "No IP address found"
    }
}