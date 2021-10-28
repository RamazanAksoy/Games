package com.ramazanaksoy.games.adapter


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ramazanaksoy.games.R
import com.ramazanaksoy.games.database.GamesEntity
import com.ramazanaksoy.games.models.Resultt


class ItemListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    fun bindItems(itemModel: GamesEntity, clickListener: (GamesEntity) -> Unit) {
        //verilerin listede gösterilmesi
        val tvNameGame = itemView.findViewById(R.id.tvNameGame) as TextView
        val tvRating = itemView.findViewById(R.id.tvRating) as TextView
        val image = itemView.findViewById(R.id.imageGame) as ImageView

        tvNameGame.text= itemModel.name
        tvRating.text= itemModel.rating.toString()+"  -  "+itemModel.released.toString()
        val media = itemModel.background_image
            Glide.with(itemView.context)
                .load(media)
                .into(image)
        itemView.setOnClickListener { clickListener(itemModel) }
    }
}