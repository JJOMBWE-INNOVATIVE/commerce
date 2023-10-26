

package com.example.commerce.viewModel

import androidx.compose.ui.semantics.Role
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.commerce.data.User
import com.example.commerce.data.UserModel
import com.example.commerce.util.Constants.USER_COLLECTION
import com.example.commerce.util.RegisterFieldsState
import com.example.commerce.util.RegisterValidation
import com.example.commerce.util.Resource
import com.example.commerce.util.validateEmail
import com.example.commerce.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private var DataBase: FirebaseDatabase = FirebaseDatabase.getInstance()


) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage


    fun createAccountWithEmailAndPassword(user:User, password: String) {

        if (checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.let { firebaseUser ->
                        saveUserInfo(firebaseUser.uid, user)

                        val userid = firebaseUser.uid ?: "IdNull"
                        val owner = UserModel(userid, user.email, password)

                        DataBase.reference.child("User").child(userid)
                            .setValue(owner)
                            .addOnSuccessListener {

                                _toastMessage.postValue("Registration was Successful")
                            }
                            .addOnFailureListener {
                                _toastMessage.postValue("Registration failed")
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    _register.value = Resource.Error(exception.message.toString())
                }

        } else {
            val registerFieldsState = RegisterFieldsState(
                validateEmail(user.email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun saveUserInfo(userUid: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user,com.example.commerce.util.Role.ADMIN )
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success

        return shouldRegister
    }








}


