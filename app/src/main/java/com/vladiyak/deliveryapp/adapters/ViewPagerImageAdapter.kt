package com.vladiyak.deliveryapp.adapters

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.databinding.ViewpagerImageItemBinding

class ViewPagerImageAdapter :
    RecyclerView.Adapter<ViewPagerImageAdapter.ViewPagerImageViewHolder>() {

    inner class ViewPagerImageViewHolder(private val binding: ViewpagerImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imagePath: String) {
            binding.apply {
                Glide.with(itemView).load(imagePath)
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .into(binding.imageProductDetails)

                val translationAnimator = ObjectAnimator.ofFloat(
                    binding.imageProductDetails,
                    "translationX", -binding.imageProductDetails.width.toFloat(), 0f
                ).apply {
                    duration = 5000
                    interpolator = LinearInterpolator()
                }

                val fadeInAnimator = ObjectAnimator.ofFloat(
                    binding.imageProductDetails,
                    "alpha", 0f, 1f
                ).apply {
                    duration = 5000
                    interpolator = LinearInterpolator()
                }

                val animatorSet = AnimatorSet().apply {
                    playTogether(translationAnimator, fadeInAnimator)
                }

                animatorSet.start()
            }
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPagerImageViewHolder {
        return ViewPagerImageViewHolder(
            ViewpagerImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerImageViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}