package com.thetatechnolabs.networkinterceptor.models

data class Weather(
    val dataseries: List<Datasery>,
    val `init`: String,
    val product: String
)