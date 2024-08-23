package com.example.mqttdevice.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mqttdemo.utils.MqttResource
import com.example.mqttdevice.R
import com.example.mqttdevice.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel:MainViewModel by viewModels()
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConnectToServer.setOnClickListener {
            val brokerUrl = binding.etBrokerUrl.text.toString().trim()
            if (brokerUrl.isNotEmpty()){
                viewModel.connectToBroker("tcp://$brokerUrl")
                binding.btnConnectToServer.isEnabled = false
            }
        }

        binding.btnSendMessage.setOnClickListener {
            viewModel.publishMessage(binding.etTopics.text.toString().trim(), binding.etSendMessage.text.toString().trim())
        }

        binding.btnSubscribe.setOnClickListener {
            viewModel.subscribeToTopic(binding.etTopicsToSubscribe.text.toString().trim())
        }

        binding.btnUnsubscribe.setOnClickListener {
            viewModel.unSubscribeToTopic(binding.etTopicsToUnsubscribe.text.toString().trim())
        }

        binding.btnDisconnectServer.setOnClickListener {
            viewModel.disconnectServer()
            binding.btnConnectToServer.isEnabled = true
        }

        binding.btnStart.setOnClickListener {
            viewModel.subscribeToTopic("NUM")
        }

        binding.btnPlus.setOnClickListener {
            viewModel.publishMessage("NUM", (++count).toString())
            binding.tvCount.text = "$count"
        }

        binding.btnMinus.setOnClickListener {
            viewModel.publishMessage("NUM", (--count).toString())
            binding.tvCount.text = "$count"
        }

        lifecycleScope.launch {
            viewModel.connectToBroker.collectLatest {
                when(it){
                    is MqttResource.Success -> {
                        Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                        binding.tvStatus.text = "Connected"
                    }
                    is MqttResource.Failure -> {

                        Toast.makeText(this@MainActivity, "Connection Failed : ${it.message} - ${it.mqttToke?.exception}", Toast.LENGTH_SHORT).show()
                        binding.tvStatus.text = "Disconnected"
                        binding.btnConnectToServer.isEnabled = true
                    }
                    is MqttResource.NoData -> {}
                }
            }
        }

        viewModel.listenToSubscribe.onEach {
            when(it){
                is MqttResource.Success -> {
                    Toast.makeText(this@MainActivity, it.mqttToke?.topics.toString(), Toast.LENGTH_SHORT).show()
                }
                is MqttResource.Failure -> {
                    Toast.makeText(this@MainActivity, "${it.message}", Toast.LENGTH_SHORT).show()
                }
                is MqttResource.NoData -> {}
            }
        }.launchIn(lifecycleScope)

        viewModel.mqttCallback.onEach {
            when(it){
                is MqttResource.Success -> {

                    when(it.message){
                        "NUM" -> {
                            count = it.mqttToke.toString().toInt()
                            binding.tvCount.text = "$count"
                        }
                        else -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Topics: ${it.message} => ${it.mqttToke.toString()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                is MqttResource.Failure -> {
                    Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                    binding.tvStatus.text = "Disconnected"
                    binding.btnConnectToServer.isEnabled = true
                }
                is MqttResource.NoData -> {}
            }
        }.launchIn(lifecycleScope)

        viewModel.publishMessage.onEach {
            when(it){
                is MqttResource.Success -> {
                    it.mqttToke?.let {
                        Toast.makeText(this, "Publish Message : ${it.topics} => ${it.response.payload}", Toast.LENGTH_SHORT).show()
                    }
                }
                is MqttResource.Failure -> {
                    Toast.makeText(this, "Publish Message Failed : ${it.message}", Toast.LENGTH_SHORT).show()
                }
                is MqttResource.NoData -> {}
            }
        }
    }

}