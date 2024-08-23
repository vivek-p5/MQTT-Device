package com.example.mqttdemo.di

import android.content.Context
import com.example.mqttdevice.data.MqttRepositoryImpl
import com.example.mqttdevice.domain.MqttRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    /*@Singleton
    @Provides
    fun provideMqttClient(@ApplicationContext context:Context): MqttAndroidClient {
        return MqttAndroidClient(context,"tcp://192.168.0.26:1883"*//*"tcp://broker.hivemq.com:1883"*//*, MqttClient.generateClientId())
    }*/

    @Provides
    @Singleton
    fun provideMqttRepository(mqttClient:com.example.mqttdevice.utils.MqttClient): MqttRepository {
        return MqttRepositoryImpl(mqttClient)
    }

    @Singleton
    @Provides
    fun provideNewMqttClient(@ApplicationContext context: Context):com.example.mqttdevice.utils.MqttClient{
        return com.example.mqttdevice.utils.MqttClient(context)
    }
}