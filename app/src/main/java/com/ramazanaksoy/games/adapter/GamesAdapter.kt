package com.ramazanaksoy.games.models

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ramazanaksoy.games.R
import com.ramazanaksoy.games.adapter.ItemListViewHolder
import com.ramazanaksoy.games.database.GamesEntity
import com.ramazanaksoy.games.models.Resultt

class GamesAdapter(val itemList: ArrayList<GamesEntity>, val search:Boolean, val clickListener: (GamesEntity) -> Unit) :
    RecyclerView.Adapter<ItemListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        return ItemListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_blog,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        //search true ise bütün elemanlar listelenir
        if(search)
        {
                holder.bindItems(itemList[position], clickListener)
        }
        //search false ise ilk 3 eleman pageviewde geri kalan elemanlar recylerviewde listelenir.
        else {
            if (position + 3 < itemList.size)
                holder.bindItems(itemList[position + 3], clickListener)
        }
    }
}
