package com.thetatechnolabs.networkinterceptorexample.data.models


data class Datasery(
    val cloudcover: Int,
    val lifted_index: Int,
    val prec_type: String,
    val rh2m: Int,
    val seeing: Int,
    val temp2m: Int,
    val timepoint: Int,
    val transparency: Int,
    val wind10m: Wind10m
)