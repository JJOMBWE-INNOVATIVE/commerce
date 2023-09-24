package com.example.commerce.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commerce.data.Products
import com.example.commerce.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore


) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Products>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Products>>> = _specialProducts

    private val _bestDealsProducts =
        MutableStateFlow<Resource<List<Products>>>(Resource.Unspecified())
    val bestDealsProducts: StateFlow<Resource<List<Products>>> = _bestDealsProducts

    private val _bestProducts = MutableStateFlow<Resource<List<Products>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Products>>> = _bestProducts

    private val pagingInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
    }
    fun fetchSpecialProducts(){
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }
        firestore.collection("Product")
            .whereEqualTo("category", "Special product").get().addOnSuccessListener { result ->
                val specialProductsList = result.toObjects(Products::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }

    }

    fun fetchBestDeals() {
        viewModelScope.launch {
            _bestDealsProducts.emit(Resource.Loading())
        }
        firestore.collection("Product").whereEqualTo("category", "best deals").get()
            .addOnSuccessListener { result ->
                val bestDealsProducts = result.toObjects(Products::class.java)
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Success(bestDealsProducts))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
//                firestore.collection("Product").whereEqualTo("category", "chair").orderBy(
//                    "id",Query.Direction.ASCENDING
//                )
                firestore.collection("Product").limit(pagingInfo.bestProductsPage * 10).get()
                    .addOnSuccessListener { result ->
                        val bestProducts = result.toObjects(Products::class.java)
                        pagingInfo.isPagingEnd = bestProducts == pagingInfo.oldBestProducts
                        pagingInfo.oldBestProducts = bestProducts
                        viewModelScope.launch {
                            _bestProducts.emit(Resource.Success(bestProducts))
                        }
                        pagingInfo.bestProductsPage++
                    }.addOnFailureListener {
                        viewModelScope.launch {
                            _bestProducts.emit(Resource.Error(it.message.toString()))
                        }
                    }
            }
        }
    }

}

internal data class PagingInfo(
    var bestProductsPage: Long = 1,
    var oldBestProducts: List<Products> = emptyList(),
    var isPagingEnd: Boolean = false
)
