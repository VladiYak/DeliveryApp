package com.vladiyak.deliveryapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.data.Product
import com.vladiyak.deliveryapp.databinding.PopularNowRvItemBinding

class PopularNowAdapter : RecyclerView.Adapter<PopularNowAdapter.PopularNowViewHolder>() {

    inner class PopularNowViewHolder(private val binding: PopularNowRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0])
                    .placeholder(R.drawable.loading_animation).error(R.drawable.ic_broken_image)
                    .into(imgProduct)

                val formattedNewPrice = if (product.offerPercentage != null) {
                    String.format("$%.1f", product.offerPercentage)
                } else {
                    ""
                }
                tvNewPrice.text = formattedNewPrice
                tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvPrice.text = "$${product.price}"
                tvName.text = product.name
            }
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): PopularNowViewHolder {
        val binding = PopularNowRvItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PopularNowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PopularNowViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }

        val fadeInAnimation =
            AnimationUtils.loadAnimation(holder.itemView.context, android.R.anim.slide_in_left)
        holder.itemView.startAnimation(fadeInAnimation)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Product) -> Unit)? = null
}