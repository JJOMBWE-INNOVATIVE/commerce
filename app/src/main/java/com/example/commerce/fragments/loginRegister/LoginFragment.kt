package com.example.commerce.fragments.loginRegister

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.commerce.R
import com.example.commerce.activities.ShoppingActivity
import com.example.commerce.databinding.FragmentLoginBinding
import com.example.commerce.dialog.setupBottomSheetDialog
import com.example.commerce.util.Resource
import com.example.commerce.viewModel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }


        binding.apply {
            buttonLoginLogin.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.login(email, password)
                } else {
                    showEmptyFieldsAlertDialog()
                }
            }

            viewModel.loginResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Success<FirebaseUser> -> {
                        if (result.data != null) {
                            binding.LoginProgressBar.visibility = View.VISIBLE
                            val userId = result.data.uid
                            if (viewModel.checkIsAdmin( userId = userId, email = "jjombwenathan7@gmail.com", password = "123456789" )) {
                                findNavController().navigate(R.id.action_loginFragment_to_productAdderFragment)
                            } else {

                                findNavController().navigate(R.id.action_loginFragment_to_shoppingActivity)
                            }
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(),"Error during login",Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                    }
                }
            }
        }




        binding.tvForgotPasswordLogin.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect{
                when (it) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        Snackbar.make(requireView(),"Reset link was sent to your email", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(),"Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit

                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.LoginProgressBar.visibility = View.VISIBLE
                        binding.buttonLoginLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.LoginProgressBar.visibility = View.GONE
                        binding.buttonLoginLogin.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.buttonLoginLogin.revertAnimation()
                    }
                    else -> Unit

                }
            }
        }

    }

    private fun showEmptyFieldsAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage("Email and password are required.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}

