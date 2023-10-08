package com.noctambulist.foody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.data.CartProduct
import com.vladiyak.deliveryapp.databinding.BillingProductsRvItemBinding

class BillingAdapter : RecyclerView.Adapter<BillingAdapter.BillingViewHolder>() {

    inner class BillingViewHolder(val binding: BillingProductsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(billing: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(billing.product.images[0])
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .into(imageCartProduct)
                tvProductCartName.text = billing.product.name
                val formattedPrice = String.format("TK. %.1f", billing.product.offerPercentage)
                tvProductCartPrice.text = formattedPrice
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BillingViewHolder {
        return BillingViewHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: BillingViewHolder, position: Int) {
        val billing = differ.currentList[position]
        holder.bind(billing)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}