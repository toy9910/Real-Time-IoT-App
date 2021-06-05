package com.example.dbwithfragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list.view.*

class FbAdapter (list: ArrayList<RoomData>) : RecyclerView.Adapter<CustomViewHolder2>(), ItemTouchHelperListener {
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

    override fun onItemMove(from_position: Int, to_position: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onItemSwipe(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onRightClick(position: Int, viewHolder: RecyclerView.ViewHolder) {
        mList.removeAt(position)
        notifyItemRemoved(position)
    }
}

class CustomViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setHolder(personalData: RoomData) {
        itemView.textView_room_no.text = personalData.room_no
        itemView.textView_room_nm.text = personalData.room_nm
        itemView.textView_room_temp.text = personalData.temperature
        itemView.textView_room_hum.text = personalData.humidity
        itemView.textView_room_gas.text = personalData.gas
        itemView.textView_room_dust.text = personalData.dust
        itemView.textView_room_light.text = personalData.light

        //val tv_background = itemView.textView_room_dust.background as GradientDrawable

        when(personalData.room_no.toInt()% 5) {
            0 ->  {
                itemView.card_view.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.one_data_purple))
            }
            1 ->  {
                itemView.card_view.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.one_data_blue))
            }
            2 ->  {
                itemView.card_view.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.one_data_red))
            }
            3 ->  {
                itemView.card_view.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.one_data_yellow))
            }
            4 ->  {
                itemView.card_view.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.one_data_green))
            }
        }

        when(personalData.dust.toInt()) {
            in 0..25 ->  {
                itemView.icon.setImageResource(R.drawable.happy)
            }
            in 25..50 ->  {
                itemView.icon.setImageResource(R.drawable.soso)
            }
            in 50..75 ->  {
                itemView.icon.setImageResource(R.drawable.bad)
            }
            in 75..100 ->  {
                itemView.icon.setImageResource(R.drawable.mask)
            }
        }

        /*
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

         */
    }
}

