package com.example.commerce.fragments.shopping

import SearchResultsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.commerce.R
import com.example.commerce.databinding.FragmentSearchBinding
import com.example.commerce.viewModel.SearchViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var binding: FragmentSearchBinding
    private var myAdapter = SearchResultsAdapter()
    val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        prepareRecyclerView()
        binding.searchImg.setOnClickListener {
            searchProducts()
        }

        observeLiveData()

        var searchJob : Job? = null
        binding.editTextSearch.addTextChangedListener { searchQuery ->
            searchJob?.cancel()
            searchJob = lifecycleScope.launch{
                delay(500)
                viewModel.searchProducts(searchQuery.toString())
            }
        }

}

    private fun observeLiveData() {
        viewModel.filteredProducts.observe(viewLifecycleOwner, Observer {
        search ->
            myAdapter.differ.submitList(search)
        })
    }


    private fun searchProducts() {
        viewModel.searchProducts(binding.editTextSearch.text.toString())
    }


    private fun prepareRecyclerView() {
        myAdapter = SearchResultsAdapter()
        binding.recyclerViewResults.apply {
            layoutManager = GridLayoutManager(context,2, GridLayoutManager.VERTICAL,false)
            adapter = myAdapter
        }
    }
}
