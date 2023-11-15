package com.example.commerce.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.simplepass.loadingbutton.BuildConfig
import com.bumptech.glide.Glide
import com.example.commerce.R
import com.example.commerce.activities.LoginRegisterActivity
import com.example.commerce.databinding.FragmentProfileBinding
import com.example.commerce.util.Resource
import com.example.commerce.util.showBottomNavigationView
import com.example.commerce.viewModel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintProfile.setOnClickListener {
            Log.d("ProfileFragment", "Profile icon clicked")
            findNavController().navigate(R.id.action_profileFragment3_to_userAccountFragment)
        }

        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment3_to_allOrdersFragment)
        }

        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragment3ToBillingFragment(
                0f,
                emptyArray(),
                false
            )
            findNavController().navigate(action)
        }

        binding.linearLogOut.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"

//        viewModel.user
//            .onEach { resource ->
//                when (resource) {
//                    is Resource.Loading -> {
//                        binding.progressbarSettings.visibility = View.VISIBLE
//                    }
//
//                    is Resource.Success -> {
//                        binding.progressbarSettings.visibility = View.GONE
//                        Glide.with(requireView()).load(resource.data!!.imagePath)
//                            .error(ColorDrawable(Color.BLACK)).into(binding.imageUser)
//                        binding.tvUserName.text =
//                            "${resource.data.firstName} ${resource.data.lastName}"
//                    }
//
//                    is Resource.Error -> {
//                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT)
//                            .show()
//                        binding.progressbarSettings.visibility = View.GONE
//                    }
//
//                    else -> Unit
//                }
//            }
//            .launchIn(lifecycleScope)
    }

        override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }
}
