package com.example.commerce.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.commerce.data.Products
import com.google.firebase.firestore.FirebaseFirestore

class SearchViewModel : ViewModel() {
    private val productsCollection = FirebaseFirestore.getInstance().collection("Product")

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String>
        get() {
            return _searchQuery
        }

    private val _filteredProducts = MutableLiveData<List<Products>>()
    val filteredProducts: LiveData<List<Products>>
        get() {
            return _filteredProducts
        }

    init {
        // Initialize the search query
        _searchQuery.value = ""
    }

    // Search for products
    fun searchProducts(searchQuery: String) {
        // Use the provided searchQuery for filtering
        productsCollection
            .whereEqualTo("name", searchQuery)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val filteredProducts = mutableListOf<Products>()
                for (document in querySnapshot.documents) {
                    filteredProducts.add(document.toObject(Products::class.java)!!)
                }

                // Submit the filtered products to the adapter
                _filteredProducts.value = filteredProducts
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "searchProducts: ")
            }
    }

}
