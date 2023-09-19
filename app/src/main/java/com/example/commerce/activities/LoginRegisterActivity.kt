package com.example.commerce.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.commerce.databinding.ActivityShoppingBinding
import com.example.commerce.databinding.ActivityLoginRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)






    }
}