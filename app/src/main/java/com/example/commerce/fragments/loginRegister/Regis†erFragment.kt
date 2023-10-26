package com.example.commerce.fragments.loginRegister

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.commerce.R
import com.example.commerce.data.User
import com.example.commerce.databinding.FragmentRegisterBinding
import com.example.commerce.util.RegisterValidation
import com.example.commerce.util.Resource
import com.example.commerce.viewModel.RegisterViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.oAuthCredential
import com.google.firebase.database.FirebaseDatabase
import com.google.rpc.context.AttributeContext
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.zip.Inflater
private val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> ()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(requireContext())
        var db = FirebaseDatabase.getInstance()
        auth = Firebase.auth

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            buttonRegisterRegister.setOnClickListener {
                if (edFirstNameRegister.text.isNullOrBlank() ||
                    edLastNameRegister.text.isNullOrBlank() ||
                    edEmailRegister.text.isNullOrBlank() ||
                    edPasswordRegister.text.isNullOrBlank()) {
                    showEmptyFieldsDialog()
                } else {
                    val user = User(
                        edFirstNameRegister.text.toString().trim(),
                        edLastNameRegister.text.toString().trim(),
                        edEmailRegister.text.toString().trim()
                    )
                    val password = edPasswordRegister.text.toString()
                    viewModel.createAccountWithEmailAndPassword(user, password)

                    // Navigate to the login fragment after a successful registration
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
            }
        }


        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }


        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonRegisterRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonRegisterRegister.revertAnimation()
                        clearFields()
                    }
                    is Resource.Error -> {
                        Log.e(TAG,it.message.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.edEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.edPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
    }
}
    }

    private fun showEmptyFieldsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Empty Fields")
            .setMessage("Please fill in all the fields.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun clearFields() {
        binding.edFirstNameRegister.text.clear()
        binding.edLastNameRegister.text.clear()
        binding.edEmailRegister.text.clear()
        binding.edPasswordRegister.text.clear()
    }
}

