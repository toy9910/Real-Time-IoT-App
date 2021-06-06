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
import android.widget.SeekBar
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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
    lateinit var spinner: Spinner
    lateinit var room_nm : String
    lateinit var room_no : String

    private lateinit var callback: OnBackPressedCallback

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

        val notifyBuilder = NotificationCompat.Builder(context!!,PRIMARY_CHANNEL_ID)
            .setContentTitle("온도 이상 감지!")
            .setContentText("자세한 내용을 보려면 클릭하세요.")
            .setSmallIcon(R.drawable.ic_baseline_post_add_24)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
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
//        createNotificationChannel()
        firebaseFirestore = FirebaseFirestore.getInstance()

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

                val ref = firebaseFirestore.collection("rooms").document(room_nm)
                Log.d(TAG, "onStopTrackingTouch: $room_nm")
                ref.update("light",light.toInt()).addOnCompleteListener {
                    Log.d(TAG, "Snapshot successfully updated!!")
                }
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

    }


    inner class SpinnerListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            var pos2 = position
            pos2 += 1
            Log.d(TAG, "onItemSelected: $pos2")
            room_no = pos2.toString()

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
            })
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {
            TODO("Not yet implemented")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                var a = activity as MainActivity
                a.replaceFragment(StartMenuFragment())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}