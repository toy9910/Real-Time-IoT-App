package com.example.dbwithfragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.NumberPicker
import android.widget.SeekBar
import android.widget.Spinner
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_one_data.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

// 방별 조회
class OneDataFragment : Fragment() {
    val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    lateinit var mNotificationManager: NotificationManager
    val NOTIFICATION_ID = 0
    val TAG = "joljak"

    lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var datbaseReference: DatabaseReference

    lateinit var spinner: Spinner
    lateinit var room_no : String
    var HI : Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    public fun newInstance(): DataFragment {
        return DataFragment()
    }

    fun createNotificationChannel() {
        mNotificationManager = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(PRIMARY_CHANNEL_ID,"TEST Notification",
                NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.description = "Notification from Mascot"
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun getNotificationBuilder() : NotificationCompat.Builder {
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        notificationIntent.setAction("NOTI")
        val notificationPendingIntent = PendingIntent.getActivity(context,NOTIFICATION_ID,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val GAS_VAL = one_gas.text.toString().toDouble()

        val notifyBuilder = NotificationCompat.Builder(context!!,PRIMARY_CHANNEL_ID)
            .setContentText("자세한 내용을 보려면 클릭하세요.")
            .setSmallIcon(R.drawable.ic_baseline_post_add_24)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
        if(HI > 40.0)
            notifyBuilder.setContentTitle("온도 이상 감지!")
        if(GAS_VAL > 200.0)
            notifyBuilder.setContentTitle("가스 이상 감지!")
        return notifyBuilder
    }

    fun sendNotification() {
        val notifyBuilder = getNotificationBuilder()
        mNotificationManager.notify(NOTIFICATION_ID,notifyBuilder.build())
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_one_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createNotificationChannel()
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        datbaseReference = firebaseDatabase.getReference("Rooms")

        spinner = one_spinner
        spinner.onItemSelectedListener = SpinnerListener()


        // seekbar 현재 디비값 보내는거
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                one_led.text = "$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                one_led.text = "${seekBar!!.progress}"
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                one_led.text = "${seekBar!!.progress}"

                //현재 어떤 방인지, 빛의 밝기를 데이터 보내는과정
                val light = one_led.text.toString()

                datbaseReference.child(room_no).child("light").setValue(light)


                /*
                val ref = firebaseFirestore.collection("rooms").document(room_nm)
                Log.d(TAG, "onStopTrackingTouch: $room_nm")
                ref.update("light",light.toInt()).addOnCompleteListener {
                    Log.d(TAG, "Snapshot successfully updated!!")
                }*/
            }
        })

        layout_temp.setOnClickListener {
            val intent = Intent(context,TempGraphActivity::class.java)
            intent.putExtra("room_no",room_no)
            startActivity(intent)
        }
        layout_hum.setOnClickListener {
            val intent = Intent(context,HumGraphActivity::class.java)
            intent.putExtra("room_no",room_no)
            startActivity(intent)
        }
        layout_dust.setOnClickListener {
            val intent = Intent(context,DustGraphActivity::class.java)
            intent.putExtra("room_no",room_no)
            startActivity(intent)
        }
        layout_gas.setOnClickListener {
            val intent = Intent(context,GasGraphActivity::class.java)
            intent.putExtra("room_no",room_no)
            startActivity(intent)
        }
        layout_light.setOnClickListener {
            startActivity(Intent(context,PycamImageActivity::class.java))
        }
    }


    inner class SpinnerListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            var pos2 = position
            pos2 += 1
            room_no = pos2.toString()


            datbaseReference.orderByChild("room_no").equalTo(pos2.toString()).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("TT", "onDataChange: ${snapshot.child(room_no).child("temp").value}")
                    one_temp?.text = snapshot.child(room_no).child("temp").value.toString()
                    one_humidity?.text = snapshot.child(room_no).child("hum").value.toString()

                    val Tf = one_temp.text.toString().toDouble()
                    val RH = one_humidity.text.toString().toDouble()
                    HI = -42.3 + 2.0 * Tf + 10.1 * RH - 0.2*Tf*RH - ((6.8 / 1000 )*(Tf * Tf)) - ((5.4 / 100 )*(RH * RH)) + ((1.2 / 1000)*(Tf * Tf)*(RH)) + ((8.5 / 10000)*(Tf)*(RH * RH)) - ((1.9 / 1000000)*(Tf * Tf)*(RH * RH))
                    if(HI > 40.0)
                        sendNotification()
                    one_gas?.text = snapshot.child(room_no).child("gas").value.toString()
                    val GAS_VAL = one_gas.text.toString().toDouble()
                    if(GAS_VAL > 200)
                        sendNotification()
                    one_dust?.text = snapshot.child(room_no).child("dust").value.toString()
                    one_led?.text = snapshot.child(room_no).child("light").value.toString()
                }
            })

/*
            datbaseReference.orderByChild("room_no").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
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
                        one_temp.text = temp
//                            if(temp.toDouble() > 50)
//                                sendNotification()
                        one_humidity.text = hum
                        one_gas.text = gas
                        one_dust.text = dust
                        one_led.text = light
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })*/

/*
            val ref = firebaseFirestore.collection("rooms").addSnapshotListener(object :
                EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.d(TAG, "onEvent: Listen failed!")
                        return
                    }
                    for (doc: QueryDocumentSnapshot in value!!) {
                        val room_no = doc.get("roomNo").toString()
                        if(room_no.toInt() == pos2) {
                            room_nm = doc.id
//                          var room_nm = doc.get("room_nm").toString()
                            val temp = doc.get("temp").toString()
                            val hum = doc.get("hum").toString()
                            val gas = doc.get("gas").toString()
                            val dust = doc.get("dust").toString()
                            val light = doc.get("light").toString()

                            one_temp.text = temp
//                            if(temp.toDouble() > 50)
//                                sendNotification()
                            one_humidity.text = hum
                            one_gas.text = gas
                            one_dust.text = dust
                            one_led.text = light
                            seekBar.progress = light.toInt()
                        }
                    }
                }
            })*/
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {
            TODO("Not yet implemented")
        }
    }
}