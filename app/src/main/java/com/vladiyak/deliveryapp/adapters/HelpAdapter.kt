package com.vladiyak.deliveryapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vladiyak.deliveryapp.R
import com.vladiyak.deliveryapp.data.HelpSection

class HelpAdapter(private val sections: List<HelpSection>) :
    RecyclerView.Adapter<HelpAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_help_section, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section = sections[position]
        holder.titleTextView.text = section.title
        holder.contentTextView.text = section.content

        holder.itemView.setOnClickListener {
            if (holder.contentTextView.visibility == View.VISIBLE) {
                holder.contentTextView.visibility = View.GONE
            } else {
                holder.contentTextView.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = sections.size
}