package com.example.dbwithfragment

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.item_list.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

// 방 전체 조회
class DataFragment : Fragment() {
    val TAG = "joljak"

    lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference

    lateinit var mArrayList : ArrayList<RoomData>
    lateinit var mArrayList2 : ArrayList<RoomData>
    lateinit var mAdapter: RoomAdapter
    lateinit var mAdapter2: FbAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    public fun newInstance() : DataFragment {
        return DataFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Rooms")

        mArrayList = arrayListOf()
        mArrayList2 = arrayListOf()
        mAdapter = RoomAdapter(mArrayList)
        mAdapter2 = FbAdapter(mArrayList2)

        listView_fb_list.layoutManager = LinearLayoutManager(activity)
        listView_fb_list.adapter = mAdapter2

        mArrayList.clear()
        mAdapter.notifyDataSetChanged()

        mArrayList2.clear()
        mAdapter2.notifyDataSetChanged()

        initDatabase()

        /*
        // FireStore 데이터 자동 업데이트
        val ref = firebaseFirestore.collection("rooms").addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.d(TAG, "onEvent: Listen failed!")
                        return
                    }
                    mArrayList2.clear()
                    for (doc: QueryDocumentSnapshot in value!!) {
                        val room_no = doc.get("roomNo").toString()
                        val room_nm = doc.id
//                        var room_nm = doc.get("room_nm").toString()
                        val temp = doc.get("temp").toString()
                        val hum = doc.get("hum").toString()
                        val gas = doc.get("gas").toString()
                        val dust = doc.get("dust").toString()
                        val light = doc.get("light").toString()
                        val p = RoomData()
                        p.room_no = room_no
                        p.room_nm = room_nm
                        p.temperature = temp
                        p.humidity = hum
                        p.gas = gas
                        p.dust = dust
                        p.light = light
                        mArrayList2.add(p)
                    }
                    mAdapter2.notifyDataSetChanged()
                }
            })
         */
        databaseReference.orderByChild("room_no").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mArrayList.clear()
                for(shot in snapshot.children) {
                    var room_no = ""
                    var room_nm = ""
                    var temp = ""
                    var hum = ""
                    var gas = ""
                    var dust = ""
                    var light = ""
                    for (shot2 in shot.children) {
                        when(shot2.key) {
                            "room_no" -> {
                                room_no = shot2.value.toString()
                            }
                            "room_nm" -> {
                                room_nm = shot2.value.toString()
                            }
                            "temp" -> {
                                temp = shot2.value.toString()
                            }
                            "hum" -> {
                                hum = shot2.value.toString()
                            }
                            "gas" -> {
                                gas = shot2.value.toString()
                            }
                            "dust" -> {
                                dust = shot2.value.toString()
                            }
                            "light" -> {
                                light = shot2.value.toString()
                            }
                        }
                    }
                    val p = RoomData()
                    p.room_no=room_no
                    p.room_nm=room_nm
                    p.temperature=temp
                    p.humidity=hum
                    p.gas=gas
                    p.dust=dust
                    p.light=light
                    mArrayList2.add(p)
                }
                mAdapter2.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    fun initDatabase() {
        firebaseFirestore = FirebaseFirestore.getInstance()

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(object : OnCompleteListener<InstanceIdResult> {
            override fun onComplete(p0: Task<InstanceIdResult>) {
                if(!p0.isSuccessful) {
                    return
                }
                val token = p0.result?.token
                val msg = getString(R.string.msg_token_fmt,token)
                Log.d(TAG, msg)
            }
        })

    }
}

