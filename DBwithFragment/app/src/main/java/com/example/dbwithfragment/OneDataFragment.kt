package com.example.dbwithfragment

import android.app.ProgressDialog
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
    val IP_ADDRESS = "3.36.237.233"
    val TAG = "joljak"

    lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var mJsonString: String
    lateinit var spinner: Spinner
    lateinit var room_nm : String
    lateinit var room_no : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    public fun newInstance(): DataFragment {
        return DataFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_one_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        layout_light.setOnClickListener {
            startActivity(Intent(context,PycamImageActivity::class.java))
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
}