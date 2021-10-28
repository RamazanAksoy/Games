package com.ramazanaksoy.games.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.ramazanaksoy.games.R
import com.ramazanaksoy.games.database.GamesEntity

class PageViewAdapter(var list: List<GamesEntity>, var ctx: Context, val clickListener: (GamesEntity) -> Unit) : PagerAdapter() {
    lateinit var layoutInflater: LayoutInflater
    lateinit var context: Context

    override fun getCount(): Int {
        return 3
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view.equals(`object`)

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(ctx)
        val view = layoutInflater.inflate(R.layout.item, container, false)
        val img = view.findViewById<ImageView>(R.id.pageImage)
        //ilk 3 eleman pageviewde listelenir.
        if (position<3)
        {
            val media = list[position].background_image
            Glide.with(container.context)
                .load(media)
                .into(img)
            container.addView(view, 0)
            view.setOnClickListener {
                clickListener(list[position])
            }
        }

        return view

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


}

