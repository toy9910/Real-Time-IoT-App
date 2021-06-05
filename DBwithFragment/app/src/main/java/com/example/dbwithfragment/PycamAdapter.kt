package com.example.dbwithfragment

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SimpleAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_pycam_image.*
import kotlinx.android.synthetic.main.image_dialog.*
import kotlinx.android.synthetic.main.item_list.view.*
import kotlinx.android.synthetic.main.pycam_image_item_list.view.*

class PycamAdapter(list: ArrayList<PycamData>) : RecyclerView.Adapter<PycamViewHolder>(), ItemTouchHelperListener {
    var mList : ArrayList<PycamData> = list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PycamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pycam_image_item_list,parent,false)
        return PycamViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: PycamViewHolder, position: Int) {
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

class PycamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var vh_title : String
    lateinit var vh_image : Bitmap
    fun setHolder(pycamData: PycamData) {
        itemView.tv_title.text = pycamData.title
        itemView.iv_pycam.setImageBitmap(pycamData.image)
        vh_title = pycamData.title!!
        vh_image = pycamData.image!!
    }
    init {
        itemView.setOnClickListener {
            val dialog = Dialog(itemView.context)
            dialog.setContentView(R.layout.image_dialog)
            dialog.setTitle(vh_title)
            Glide.with(itemView.context).load(vh_image).into(dialog.iv_dialog)
            dialog.show()
        }
    }
}