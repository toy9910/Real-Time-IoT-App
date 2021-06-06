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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class TempGraphActivity : AppCompatActivity() {
    val IP_ADDRESS = "3.36.237.233"
    val TAG = "joljak"

    lateinit var mJsonString : String
    val dataVals = ArrayList<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp_graph)

        val room_no = intent.getStringExtra("room_no")


        val cal = Calendar.getInstance()

        val format_start: DateFormat = SimpleDateFormat("yyyy-MM-dd 00:00:00")
        val format_end: DateFormat = SimpleDateFormat("yyyy-MM-dd 23:59:59")

        val cur_date_start = format_start.format(cal.time)
        val cur_date_end = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.time)

        Log.d(TAG, "onCreate: \n start : $cur_date_start \n end : $cur_date_end")
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
        val task = GetTempAvgData()
        task.execute("http://" + IP_ADDRESS + "/temp_graph_getjson.php",cur_date_start,cur_date_end,day1_start,day1_end
            ,day2_start,day2_end,day3_start,day3_end,day4_start,day4_end,day5_start,day5_end,day6_start,day6_end,room_no)
    }


    inner class GetTempAvgData : AsyncTask<String, Void, String>() {
        var progressDialog : ProgressDialog? = null
        lateinit var errorString : String
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog.show(this@TempGraphActivity,"Please Wait",null,true,true)
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
            val postParameters = "date1=" + params[1] + "&date2=" + params[2] + "&date3=" + params[3] + "&date4=" + params[4] +
                    "&date5=" + params[5] + "&date6=" + params[6] + "&date7=" + params[7] + "&date8=" + params[8] +
                    "&date9=" + params[9] + "&date10=" + params[10] + "&date11=" + params[11] + "&date12=" + params[12] +
                    "&date13=" + params[13] + "&date14=" + params[14] + "&room_no=" + params[15]
            Log.d(TAG, "doInBackground: $postParameters")

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
        val TAG_TEMP = "avg_temp"

        try {
            val jsonObject = JSONObject(mJsonString)
            val jsonArray = jsonObject.getJSONArray(TAG_JSON)
            Log.d(TAG, "AddEntry: ${jsonArray.length()}")

            for(i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)

                val measure_time = item.getString(TAG_TIME)
                Log.d(TAG, "AddEntry: $measure_time")
                val stringBuilder = java.lang.StringBuilder()
                var time = measure_time.slice(IntRange(5,9))
                stringBuilder.append(time)
                stringBuilder.delete(2,3)
                stringBuilder.insert(2,"/")
                val avg_temp = item.getString(TAG_TEMP).toFloat()

                Log.d(TAG, "AddEntry: ${dataVals.size.toFloat()+1} ${avg_temp} ${stringBuilder.toString()}")
                dataVals.add(Entry((dataVals.size+1).toFloat(),avg_temp,stringBuilder.toString()))
            }
        } catch (e: JSONException) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}