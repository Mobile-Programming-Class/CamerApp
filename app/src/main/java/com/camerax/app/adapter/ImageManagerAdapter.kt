package com.camerax.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.camerax.app.R

class ImageManagerAdapter(private val itemList: List<Image>) : RecyclerView.Adapter<ImageManagerAdapter.ViewHolder>() {
    private var context: Context? = null
    var listener: GalleryImageClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvId : TextView = view.findViewById(R.id.tvId)
        val tvCaption : TextView = view.findViewById(R.id.tvCaption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_image, parent,
            false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = itemList.get(position)

        // load image
        Glide.with(context!!)
            .load(image.imageUrl)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.ivIcon)

        holder.tvId.text = itemList[position].docId
        holder.tvCaption.text = itemList[position].title

        // adding click or tap handler for our image layout
        holder.ivIcon.setOnClickListener {
            listener?.onClick(position)
        }
    }
}