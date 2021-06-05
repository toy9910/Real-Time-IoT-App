package com.example.dbwithfragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.impl.utils.ForceStopRunnable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_pycam_image.*
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.ref.Reference
import kotlin.coroutines.coroutineContext

class PycamImageActivity : AppCompatActivity() {
    lateinit var pycamList: ArrayList<PycamData>
    lateinit var pycamAdapter: PycamAdapter

    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference

    lateinit var mReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pycam_image)
        setReceiver()

        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.getReferenceFromUrl("gs://test2-8d4e7.appspot.com").child("img/")

        pycamList = arrayListOf()
        pycamAdapter = PycamAdapter(pycamList)

        rv_pycam.layoutManager = LinearLayoutManager(applicationContext)
        rv_pycam.adapter = pycamAdapter

        pycamList.clear()
        pycamAdapter.notifyDataSetChanged()

        setSwipeFunction()

        GlobalScope.launch {
            async(Dispatchers.Default) { getData() }.await()
            pycamAdapter.notifyDataSetChanged()
        }
    }

    fun getData() {
        storageReference.listAll().addOnSuccessListener(object : OnSuccessListener<ListResult> {
            override fun onSuccess(p0: ListResult?) {
                pycamList.clear()
                for(item in p0?.items!!) {
                    Log.d("TEST", "onSuccess: ${item.name}")

                    item.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                        override fun onSuccess(p0: Uri?) {
                            Log.d("TEST", "onSuccess: ${p0.toString()}")
                            Glide.with(applicationContext).asBitmap().load(p0).into(object :
                                SimpleTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    val data = PycamData(resource,item.name)
                                    pycamList.add(data)
                                    pycamAdapter.notifyDataSetChanged()
                                }
                            })
                            Log.d("TEST", "onCreate!!!!!!!!: ${pycamList.size}")
                        }
                    }).addOnFailureListener(object : OnFailureListener {
                        override fun onFailure(p0: Exception) {
                            Log.d("TEST", "onFailure: FAILED!!!!!!!!")
                        }
                    })
                }
            }
        })
    }

    fun setSwipeFunction() {
        val helper = ItemTouchHelper(ItemTouchHelperCallback(pycamAdapter, this))
        helper.attachToRecyclerView(rv_pycam)

        val simpleCallback : ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                pycamList.removeAt(viewHolder.layoutPosition)
                pycamAdapter.notifyItemRemoved(viewHolder.layoutPosition)
            }


        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(rv_pycam)
    }

    fun setReceiver() {
        val filter = IntentFilter("DELETE IMAGE")
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val title = intent?.getStringExtra("title")
                val delRef = storageReference.child(title!!)
                delRef.delete().addOnSuccessListener {
                    Log.d("TEST", "Success Listener : DELETE SUCCESS!")
                }
            }
        }
        registerReceiver(mReceiver,filter)
    }
}