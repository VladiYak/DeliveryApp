package com.vladiyak.deliveryapp.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.data.Order
import com.vladiyak.deliveryapp.data.OrderStatus
import com.vladiyak.deliveryapp.data.getOrderStatus
import com.vladiyak.deliveryapp.databinding.OrderItemBinding

class OrderHistoryAdapter : RecyclerView.Adapter<OrderHistoryAdapter.AllOrdersViewHolder>() {

    inner class AllOrdersViewHolder(private val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = "#${order.orderId}"
                tvOrderDate.text = order.date
                val resources = itemView.resources

                val colorDrawable = when (getOrderStatus(order.orderStatus)) {
                    is OrderStatus.Ordered -> {
                        ColorDrawable(resources.getColor(R.color.orange))
                    }

                    is OrderStatus.Confirmed -> {
                        ColorDrawable(resources.getColor(R.color.green))
                    }

                    is OrderStatus.Delivered -> {
                        ColorDrawable(resources.getColor(R.color.green))
                    }

                    is OrderStatus.Shipped -> {
                        ColorDrawable(resources.getColor(R.color.green))
                    }

                    is OrderStatus.Canceled -> {
                        ColorDrawable(resources.getColor(R.color.mainRed))
                    }

                    is OrderStatus.Returned -> {
                        ColorDrawable(resources.getColor(R.color.mainRed))
                    }
                }
                imageOrderState.setImageDrawable(colorDrawable)
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllOrdersViewHolder {
        return AllOrdersViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AllOrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)
        holder.itemView.setOnClickListener {
            onClick?.invoke(order)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Order) -> Unit)? = null
}