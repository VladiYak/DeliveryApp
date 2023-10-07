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
import com.vladiyak.deliveryapp.databinding.SpicyOfferRvItemBinding

class SpicyOfferAdapter : RecyclerView.Adapter<SpicyOfferAdapter.SpicyOfferViewHolder>() {

    inner class SpicyOfferViewHolder(private val binding: SpicyOfferRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView)
                    .load(product.images[0])
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .into(imgSpicyOffer)

                val formattedNewPrice = String.format(
                    "TK. %.1f",
                    product.offerPercentage
                ) // Assuming you have a field for the new price in your Product model
                tvNewPrice.text = formattedNewPrice

                tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                val formattedOldPrice = String.format("TK. %.1f", product.price)
                tvOldPrice.text = formattedOldPrice

                tvSpicyOfferName.text = product.name
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
        parent: ViewGroup,
        viewType: Int
    ): SpicyOfferViewHolder {
        return SpicyOfferViewHolder(
            SpicyOfferRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: SpicyOfferViewHolder, position: Int) {
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