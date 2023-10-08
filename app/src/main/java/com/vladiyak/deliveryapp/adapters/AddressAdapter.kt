package com.noctambulist.foody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.data.Address
import com.vladiyak.deliveryapp.databinding.AddressItemRvBinding

class AddressAdapter() : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(val binding: AddressItemRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Address, isSelected: Boolean) {
            binding.apply {
                btnAddress.text = address.addressTitle
                if (isSelected) {
                    btnAddress.background =
                        ContextCompat.getDrawable(itemView.context, R.drawable.black_button)
                } else {
                    btnAddress.background =
                        ContextCompat.getDrawable(itemView.context, R.drawable.green_button)
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressViewHolder {
        return AddressViewHolder(
            AddressItemRvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    var selectedAddress = -1
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress == position)

        holder.binding.btnAddress.setOnClickListener {
            if (selectedAddress >= 0) {
                notifyItemChanged(selectedAddress)
            }
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(address)
        }
    }

    init {
        differ.addListListener { _, _ ->
            notifyItemChanged(selectedAddress)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Address) -> Unit)? = null
}

