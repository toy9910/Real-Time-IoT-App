package com.example.dbwithfragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list.view.*

class FbAdapter (list: ArrayList<RoomData>) : RecyclerView.Adapter<CustomViewHolder2>() {
    var mList : ArrayList<RoomData> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder2 {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list,parent,false)
        return CustomViewHolder2(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder2, position: Int) {
        val p = mList.get(position)
        holder.setHolder(p)
    }
}

class CustomViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setHolder(personalData: RoomData) {
        itemView.textView_room_no.text = personalData.room_no
        itemView.textView_room_nm.text = personalData.room_nm
        itemView.textView_room_temp.text = personalData.room_temperature
        itemView.textView_room_hum.text = personalData.room_humidity
        itemView.textView_room_gas.text = personalData.room_gas
        itemView.textView_room_dust.text = personalData.room_dust
        itemView.textView_room_light.text = personalData.room_light

        val tv_background = itemView.textView_room_dust.background as GradientDrawable

        when(personalData.room_dust.toInt()) {
            in 0..49 ->  {
                tv_background.setColor(Color.parseColor("#08E100"))
            }
            in 50..69 ->  {
                tv_background.setColor(Color.parseColor("#F5A623"))
            }
            in 70..100 ->  {
                tv_background.setColor(Color.parseColor("#D93218"))
            }
        }
    }
}

