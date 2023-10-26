package com.example.commerce.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commerce.util.Resource
import com.example.commerce.util.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel(){

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()
    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()
    val loginResult: LiveData<Resource<FirebaseUser>> = MutableLiveData()
    var lastLoginEmail: String? = null
    var lastLoginPassword: String? = null

    fun login(email: String, password: String) {
        lastLoginEmail = email
        lastLoginPassword = password

        if (email.isNotEmpty() && password.isNotEmpty()) {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        val firebaseUser = authResult.user

                        if (firebaseUser != null) {
                            val userId = firebaseUser.uid ?: "IdNull"
                            val isAdmin = checkIsAdmin(email, password, userId)

                            if (isAdmin) {
                                (loginResult as MutableLiveData).value =
                                    Resource.Success(firebaseUser, Role.ADMIN)
                            } else {
                                (loginResult as MutableLiveData).value =
                                    Resource.Success(firebaseUser, Role.USER)
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        (loginResult as MutableLiveData).value =
                            Resource.Error(exception.message ?: "An error occurred")
                    }
            } catch (e: Exception) {
                (loginResult as MutableLiveData).value =
                    Resource.Error(e.message ?: "An error occurred")
            }
        } else {
            (loginResult as MutableLiveData).value =
                Resource.Error("Email and password are required")
        }


    }
       fun checkIsAdmin(email: String, password: String, userId: String): Boolean {
            return    (lastLoginEmail == "jjombwenathan7@gmail.com" && lastLoginPassword == "123456789" && userId != "IdNull")
        }



    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }

        firebaseAuth
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(email,Role.ADMIN))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }

    }


}



