package com.example.dbwithfragment

import android.app.ProgressDialog
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_temp_graph.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HumGraphActivity : AppCompatActivity() {
    val IP_ADDRESS = "3.36.237.233"
    val TAG = "joljak"

    lateinit var mJsonString : String
    val dataVals = ArrayList<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hum_graph)

        val room_no = intent.getStringExtra("room_no")

        val cal = Calendar.getInstance()
        cal.time = Date()
        val format_start: DateFormat = SimpleDateFormat("yyyy-MM-dd 00:00:00")
        val format_end: DateFormat = SimpleDateFormat("yyyy-MM-dd 23:59:59")

        val cur_date_start = format_start.format(cal.time)
        val cur_date_end = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.time)
        Log.d(TAG, "onCreate: \n start : ${format_start.format(cal.time)} \n end : ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.time)}")
        cal.add(Calendar.DATE,-1)
        val day1_start = format_start.format(cal.time)
        val day1_end = format_end.format(cal.time)
        Log.d(TAG, "onCreate: \n start : ${format_start.format(cal.time)} \n end : ${format_end.format(cal.time)}")
        cal.add(Calendar.DATE,-1)
        val day2_start = format_start.format(cal.time)
        val day2_end = format_end.format(cal.time)
        Log.d(TAG, "onCreate: \n start : ${format_start.format(cal.time)} \n end : ${format_end.format(cal.time)}")
        cal.add(Calendar.DATE,-1)
        val day3_start = format_start.format(cal.time)
        val day3_end = format_end.format(cal.time)
        Log.d(TAG, "onCreate: \n start : ${format_start.format(cal.time)} \n end : ${format_end.format(cal.time)}")
        cal.add(Calendar.DATE,-1)
        val day4_start = format_start.format(cal.time)
        val day4_end = format_end.format(cal.time)
        Log.d(TAG, "onCreate: \n start : ${format_start.format(cal.time)} \n end : ${format_end.format(cal.time)}")
        cal.add(Calendar.DATE,-1)
        val day5_start = format_start.format(cal.time)
        val day5_end = format_end.format(cal.time)
        Log.d(TAG, "onCreate: \n start : ${format_start.format(cal.time)} \n end : ${format_end.format(cal.time)}")
        cal.add(Calendar.DATE,-1)
        val day6_start = format_start.format(cal.time)
        val day6_end = format_end.format(cal.time)
        Log.d(TAG, "onCreate: \n start : ${format_start.format(cal.time)} \n end : ${format_end.format(cal.time)}")

        dataVals.clear()
        val task = GetHumAvgData()
        task.execute("http://" + IP_ADDRESS + "/hum_graph_getjson.php", cur_date_start, cur_date_end
            , day1_start, day1_end, day2_start, day2_end, day3_start, day3_end, day4_start, day4_end, day5_start, day5_end,
            day6_start, day6_end, room_no)
    }

    inner class GetHumAvgData : AsyncTask<String, Void, String>() {
        var progressDialog : ProgressDialog? = null
        lateinit var errorString : String
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog.show(this@HumGraphActivity,"Please Wait",null,true,true)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            progressDialog?.dismiss()

            Log.d(TAG, "response - $result")

            if(result == null)
                Log.e(TAG,errorString)
            else {
                mJsonString = result
                AddEntry()

                val lineDataset = LineDataSet(dataVals, "온도")
                lineDataset.setCircleColor(Color.GREEN)
                lineDataset.circleRadius = 4f
                lineDataset.lineWidth = 1.5f
                lineDataset.color = Color.GREEN


                val data = LineData(lineDataset)
                data.setValueTextSize(10f)
                tempChart.data = data
                val customValueFormatter: CustomValueFormatter = CustomValueFormatter(tempChart)
                tempChart.xAxis.setValueFormatter(customValueFormatter)


                val xAxis = tempChart.xAxis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textSize = 12f
                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    granularity = 1f
                    axisMaximum = 7f
                    axisMinimum = 0f
                    isGranularityEnabled = true
                }


                tempChart.apply {
                    description.text = ""
                    axisRight.isEnabled = false
                    axisLeft.axisMaximum = 60f
                    axisLeft.axisMinimum = 0f
                    legend.apply {
                        textSize = 15f
                        verticalAlignment = Legend.LegendVerticalAlignment.TOP
                        horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
//                orientation = Legend.LegendOrientation.HORIZONTAL
//                setDrawInside(false)
                    }
                    tempChart.invalidate()
                }
            }
        }

        override fun doInBackground(vararg params: String?): String? {
            val serverURL = params[0]
            val postParameters = "cur_date_start=" + params[1] + "&cur_date_end=" + params[2] +
                    "&day1_start=" + params[3] + "&day1_end=" + params[4] + "&day2_start=" + params[5] +
                    "&day2_end=" + params[6] + "&day3_start=" + params[7] + "&day3_end=" + params[8] +
                    "&day4_start=" + params[9] + "&day4_end=" + params[10] + "&day5_start=" + params[11] +
                    "&day5_end=" + params[12] + "&day6_start=" + params[13] + "&day6_end=" + params[14] +
                    "&room_no=" + params[15]

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

    fun AddEntry() {
        val TAG_JSON = "joljak_dev"
        val TAG_TIME = "measure_time"
        val TAG_HUM = "avg_hum"

        try {
            val jsonObject = JSONObject(mJsonString)
            val jsonArray = jsonObject.getJSONArray(TAG_JSON)

            for(i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)

                val measure_time = item.getString(TAG_TIME)
                val stringBuilder = java.lang.StringBuilder()
                var time = measure_time.slice(IntRange(5,9))
                stringBuilder.append(time)
                stringBuilder.delete(2,3)
                stringBuilder.insert(2,"/")
                val avg_hum = item.getString(TAG_HUM).toFloat()

                Log.d(TAG, "AddEntry: ${dataVals.size.toFloat()+1} ${avg_hum} ${stringBuilder.toString()}")
                dataVals.add(Entry((dataVals.size+1).toFloat(),avg_hum,stringBuilder.toString()))
            }
        } catch (e: JSONException) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}