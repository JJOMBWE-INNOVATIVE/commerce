package com.example.commerce.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.commerce.R
import com.example.commerce.databinding.ActivityShoppingBinding

class ShoppingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)






    }
}