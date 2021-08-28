package com.example.redditwalls.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.redditwalls.databinding.ImageItemBinding
import com.example.redditwalls.models.Image

class ImagesAdapter(
    private val loadLowRes: Boolean,
    private val imageListener: ImageClickListener
) :
    PagingDataAdapter<Image, ImageViewHolder>(ImageComparator) {

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(image = it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LayoutInflater.from(parent.context).let {
            ImageViewHolder(
                ImageItemBinding.inflate(it, parent, false),
                loadLowRes,
                imageListener
            )
        }

    object ImageComparator : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.imageLink == newItem.imageLink
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }
    }
}

