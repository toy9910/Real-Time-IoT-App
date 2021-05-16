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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
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
    val IP_ADDRESS = "3.35.105.27"
    val TAG = "joljak"

    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference

    lateinit var mJsonString : String
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
        mArrayList = arrayListOf<RoomData>()
        mArrayList2 = arrayListOf<RoomData>()
        mAdapter = RoomAdapter(mArrayList)
        mAdapter2 = FbAdapter(mArrayList2)
        //back.setBackgroundColor(Color.parseColor("#D0BFFF"))

        //listView_main_list.setBackgroundColor(Color.parseColor("#86A6F8"))
        listView_main_list.layoutManager = LinearLayoutManager(activity)
        listView_main_list.adapter = mAdapter


        listView_fb_list.layoutManager = LinearLayoutManager(activity)
        listView_fb_list.adapter = mAdapter2

        val dividerItemDecoration = DividerItemDecoration(activity, LinearLayoutManager(activity).orientation)
        listView_main_list.addItemDecoration(dividerItemDecoration)

        mArrayList.clear()
        mAdapter.notifyDataSetChanged()

        mArrayList2.clear()
        mAdapter2.notifyDataSetChanged()

        initDatabase()

        val task = GetData()
        task.execute("http://" + IP_ADDRESS + "/getjson.php", "")

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
                    p.room_temperature=temp
                    p.room_humidity=hum
                    p.room_gas=gas
                    p.room_dust=dust
                    p.room_light=light
                    mArrayList2.add(p)
                }
                mAdapter2.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }



    inner class GetData : AsyncTask<String, Void, String>() {
        var progressDialog : ProgressDialog? = null
        lateinit var errorString : String
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog.show(activity,"Please Wait",null,true,true)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            progressDialog?.dismiss()

            Log.d(TAG, "response - $result")

            if(result == null)
                Log.e(TAG,errorString)
            else {
                mJsonString = result
                showResult()
                for(i in 0 until mArrayList.size)
                    Log.d(TAG, "response - ${mArrayList.get(i).room_no}")
            }
        }

        override fun doInBackground(vararg params: String?): String? {
            val serverURL = params[0]
            val postParameters = "room_no=" + params[1]

            try {
                val url = URL(serverURL)
                val httpURLConnection = url.openConnection() as HttpURLConnection

                httpURLConnection.readTimeout = 5000
                httpURLConnection.connectTimeout = 5000
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.doInput = true
                httpURLConnection.connect()

                val outputStream = httpURLConnection.outputStream
                outputStream.write(postParameters?.toByteArray(Charset.defaultCharset()))
                outputStream.flush()
                outputStream.close()

                val responseStatusCode = httpURLConnection.responseCode
                Log.d(TAG, "response code - $responseStatusCode");

                var inputStream : InputStream? = null
                if(responseStatusCode == HttpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.inputStream
                else
                    inputStream = httpURLConnection.errorStream

                val inputStreamReader = InputStreamReader(inputStream, Charset.defaultCharset())
                val bufferedReader = BufferedReader(inputStreamReader)

                val sb = StringBuilder()
                var line : String? = null

                while(true) {
                    line = bufferedReader.readLine()
                    if(line != null)
                        sb.append(line)
                    else
                        break
                }
                bufferedReader.close()
                return sb.toString().trim()
            } catch (e : Exception) {
                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();
                return null
            }
        }
    }

    fun showResult() {
        val TAG_JSON = "joljak_dev"
        val TAG_ID = "room_no"
        val TAG_NAME = "room_nm"
        val TAG_TEMPERATURE ="temperature"
        val TAG_HUMIDITY = "humidity"
        val TAG_GAS = "gas"
        val TAG_DUST = "dust"
        val TAG_LIGHT = "light"

        try {
            val jsonObject = JSONObject(mJsonString)
            val jsonArray = jsonObject.getJSONArray(TAG_JSON)

            for(i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)

                val id = item.getString(TAG_ID)
                val name = item.getString(TAG_NAME)
                val temperature = item.getString(TAG_TEMPERATURE)
                val humidity = item.getString(TAG_HUMIDITY)
                val gas = item.getString(TAG_GAS)
                val dust = item.getString(TAG_DUST)
                val light = item.getString(TAG_LIGHT)

                val roomData = RoomData()
                roomData.room_no = id
                roomData.room_nm = name
                roomData.room_temperature = temperature
                roomData.room_humidity = humidity
                roomData.room_gas = gas
                roomData.room_dust = dust
                roomData.room_light = light
                mArrayList.add(roomData)
                mAdapter.notifyDataSetChanged()
            }
        } catch (e: JSONException) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    fun initDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Rooms")

        databaseReference.child("한용방").child("room_no").setValue("1")
        databaseReference.child("한용방").child("room_nm").setValue("한용방")
        databaseReference.child("한용방").child("temp").setValue("21")
        databaseReference.child("한용방").child("hum").setValue("15")
        databaseReference.child("한용방").child("gas").setValue("1")
        databaseReference.child("한용방").child("dust").setValue("0")
        databaseReference.child("한용방").child("light").setValue("60")

        databaseReference.child("성수방").child("room_no").setValue("2")
        databaseReference.child("성수방").child("room_nm").setValue("성수방")
        databaseReference.child("성수방").child("temp").setValue("24")
        databaseReference.child("성수방").child("hum").setValue("20")
        databaseReference.child("성수방").child("gas").setValue("0")
        databaseReference.child("성수방").child("dust").setValue("10")
        databaseReference.child("성수방").child("light").setValue("35")

        databaseReference.child("민기방").child("room_no").setValue("3")
        databaseReference.child("민기방").child("room_nm").setValue("민기방")
        databaseReference.child("민기방").child("temp").setValue("23")
        databaseReference.child("민기방").child("hum").setValue("10")
        databaseReference.child("민기방").child("gas").setValue("0")
        databaseReference.child("민기방").child("dust").setValue("0")
        databaseReference.child("민기방").child("light").setValue("80")

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