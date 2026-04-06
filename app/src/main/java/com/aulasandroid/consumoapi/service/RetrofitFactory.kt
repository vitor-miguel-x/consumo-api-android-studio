package com.aulasandroid.consumoapi.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactory {

    private val baseUrl = "https://viacep.com.br/ws/"
    private val retrofitFactory = Retrofit
        .Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    fun getEnderecoService(): EnderecoService {
        return retrofitFactory.create(EnderecoService::class.java)
    }
}