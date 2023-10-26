package com.example.commerce.fragments.productAdder

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.commerce.R
import com.example.commerce.data.order.products
import com.example.commerce.databinding.FragmentProductAdderBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

class ProductAdderFragment : Fragment() {
   private lateinit var binding: FragmentProductAdderBinding
    private var selectedImages = mutableListOf<Uri>()
    private val selectedColors = mutableListOf<Int>()
    private val productsStorage = Firebase.storage.reference
    private val fireStore = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductAdderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonColorPicker.setOnClickListener {
            ColorPickerDialog.Builder(requireContext())
                .setTitle("Product Color")
                .setPositiveButton("select", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            selectedColors.add(it.color)
                            updateColors()
                        }
                    }
                })
                .setNegativeButton("Cancel") { colorPicker, _ ->
                    colorPicker.dismiss()
                }.show()
        }

        val selectImagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val intent = result.data
                    if (intent?.clipData != null) {
                        val count = intent.clipData?.itemCount ?: 0
                        (0 until count).forEach {
                            val imageUri = intent.clipData?.getItemAt(it)?.uri
                            imageUri?.let {
                                selectedImages.add(it)
                            }
                        }
                    } else {
                        val imageUri = intent?.data
                        imageUri?.let {
                            selectedImages.add(it)
                        }
                    }
                    updateImages()
                }
            }

        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImagesActivityResult.launch(intent)
        }

        binding.saveProduct.setOnClickListener {
            Log.d("ProductAdderFragment", "Save button clicked")
            val productValidation = validateInformation()
            if (!productValidation) {
                Log.d("ProductAdderFragment", "Validation failed. Check your inputs.")
                Toast.makeText(requireContext(), "Fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveProduct()
        }



    }

    private fun updateColors() {
        var colors = ""
        selectedColors.forEach {
            colors = "$colors ${Integer.toHexString(it)}"
        }
        binding.tvSelectedColors.text = colors
    }

    private fun updateImages() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }


    private fun saveProduct() {
        val name = binding.edName.text.toString().trim()
        val category = binding.edCategory.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val offerPercentage = binding.offerPercentage.text.toString().trim()
        val description = binding.edDescription.text.toString().trim()
        val sizes = getSizesList(binding.edSizes.text.toString().trim())
        val imagesByteArrays = getImagesByteArrays()
        val images = mutableListOf<String>()



        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                showLoading()
            }

            try {
                val uploadTasks = imagesByteArrays.map { imageByteArray ->
                    async {
                        val id = UUID.randomUUID().toString()
                        val imageStorage = productsStorage.child("products/images/$id")
                        val result = imageStorage.putBytes(imageByteArray).await()
                        val downloadUrl = result.storage.downloadUrl.await()
                        downloadUrl.toString()
                    }
                }

                val imageUrls = uploadTasks.awaitAll()
                images.addAll(imageUrls)

                // Rest of your code for saving the product
                val product = products(
                    UUID.randomUUID().toString(),
                    name,
                    category,
                    price.toFloat(),
                    if (offerPercentage.isEmpty()) null else offerPercentage.toFloat(),
                    if (description.isEmpty()) null else description,
                    if (selectedImages.isEmpty()) null else selectedColors,
                    sizes,
                    images
                )
                fireStore.collection("Product").add(product)
                    .addOnSuccessListener {
                        hideLoading()
                        Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show()
                        clearForm()
                    }.addOnFailureListener {
                        hideLoading()
                        Log.e("Error", it.message.toString())
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    hideLoading()
                }
            }
        }
    }

    private fun clearForm() {
        binding.edName.text.clear()
        binding.edCategory.text.clear()
        binding.edPrice.text.clear()
        binding.offerPercentage.text.clear()
        binding.edDescription.text.clear()
        binding.edSizes.text.clear()
        binding.tvSelectedColors.text = ""
        binding.tvSelectedImages.text = ""
        selectedImages.clear()
        selectedColors.clear()
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun getImagesByteArrays(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach { imageUri ->
            val stream = ByteArrayOutputStream()
            val imageBmp = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                imagesByteArray.add(stream.toByteArray())
            }
        }
        return imagesByteArray
    }

    private fun getSizesList(sizesStr: String): List<String>? {
        if (sizesStr.isEmpty())
            return null
        return sizesStr.split(",")
    }

    private fun validateInformation(): Boolean {
        if (binding.edPrice.text.toString().trim().isEmpty())
            return false
        if (binding.edName.text.toString().trim().isEmpty())
            return false
        if (binding.edCategory.text.toString().trim().isEmpty())
            return false
        if (selectedImages.isEmpty())
            return false
        return true
    }
}

