import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commerce.data.Products
import com.example.commerce.data.order.products
import com.example.commerce.databinding.TextViewProductNameBinding

class SearchResultsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ProductViewHolder(var binding:TextViewProductNameBinding) : RecyclerView.ViewHolder(binding.root) {

        fun binds(product: Products) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(img)
                name.text = product.name
            }

            itemView.setOnClickListener {
                onClick?.invoke(product)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Products>() {
        override fun areItemsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem.id == newItem.id

        }

        override fun areContentsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(TextViewProductNameBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ProductViewHolder).binds(differ.currentList[position])

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Products) -> Unit)? = null

}
