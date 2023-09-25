package com.example.commerce.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val product: Products,
    val quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null
): Parcelable {
    constructor() : this(Products(), 1, null, null)
}
