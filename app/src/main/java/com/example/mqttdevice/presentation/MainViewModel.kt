package com.example.mqttdevice.presentation

import androidx.lifecycle.ViewModel
import com.example.mqttdemo.utils.MqttResource
import com.example.mqttdevice.domain.MqttRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mqttRepository: MqttRepository):ViewModel() {

    private val _mqttCallback = MutableStateFlow<MqttResource<MqttMessage>>(MqttResource.NoData())
    val mqttCallback:StateFlow<MqttResource<MqttMessage>> = _mqttCallback.asStateFlow()

    private val _connectToBroker = MutableStateFlow<MqttResource<IMqttToken>>(MqttResource.NoData())
    val connectToBroker: StateFlow<MqttResource<IMqttToken>> = _connectToBroker.asStateFlow()

    private val _listenToSubscribe = MutableStateFlow<MqttResource<IMqttToken>>(MqttResource.NoData())
    val listenToSubscribe:StateFlow<MqttResource<IMqttToken>> = _listenToSubscribe.asStateFlow()

    private val _publishMessage = MutableStateFlow<MqttResource<IMqttToken>>(MqttResource.NoData())
    val publishMessage:StateFlow<MqttResource<IMqttToken>> = _publishMessage.asStateFlow()


    fun connectToBroker(brokerUrl: String) {
        mqttRepository.connect(brokerUrl, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                _connectToBroker.value = MqttResource.Success(asyncActionToken, "Server Connected")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                _connectToBroker.value = MqttResource.Failure(asyncActionToken,exception?.localizedMessage?:"unexpected error")
                exception?.printStackTrace()
            }

        },object :MqttCallback{
            override fun connectionLost(cause: Throwable?) {
                _connectToBroker.value = MqttResource.Failure(message = "Connection Lost : ${cause?.message}")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                _mqttCallback.value = MqttResource.Success(message,topic )
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                token?.let {
//                    _mqttCallback.value = MqttResource.Success(it.message,(it.topics?: arrayOf()).toString())
                }
            }

        })
    }

    fun subscribeToTopic(topics: String) {
        mqttRepository.subscribe(topics,2,object :IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                _listenToSubscribe.value = MqttResource.Success(asyncActionToken)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                _listenToSubscribe.value = MqttResource.Failure(asyncActionToken,exception?.localizedMessage?:"unexpected error")
            }

        })
    }

    fun unSubscribeToTopic(topic: String) {
        mqttRepository.unSubscribe(topic,object :IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {

            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                TODO("Not yet implemented")
            }

        })
    }

    fun disconnectServer() {
        mqttRepository.disconnect(object :IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                _connectToBroker.value = MqttResource.Success(asyncActionToken,"Server disconnected")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                _connectToBroker.value = MqttResource.Failure(asyncActionToken,exception?.message?:"unexpected error")
            }

        })
    }

    /**
     * publish string
     */
    fun publishMessage(topic: String, message: String) {
        mqttRepository.publish(topic,message,2,retained = false, publishCallback = object :IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                _publishMessage.value = MqttResource.Success(asyncActionToken)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                _publishMessage.value = MqttResource.Failure(asyncActionToken,exception?.message?:"unexpected error")
            }

        })
    }

    /**
     * publish bytearray
     */
    fun publishMessage(topic: String, message: ByteArray) {
        mqttRepository.publish(topic,message,2,retained = false, publishCallback = object :IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                _publishMessage.value = MqttResource.Success(asyncActionToken)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                _publishMessage.value = MqttResource.Failure(asyncActionToken,exception?.message?:"unexpected error")
            }

        })
    }


}