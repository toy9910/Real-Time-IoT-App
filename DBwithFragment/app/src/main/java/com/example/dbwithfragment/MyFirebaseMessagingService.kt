package com.example.dbwithfragment

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "joljak"

    override fun onMessageReceived(p0: RemoteMessage) {
        Log.d(TAG, "msg : ${p0.toString()}")

        if(p0.data.isNotEmpty()) {
            Log.d(TAG, "data : ${p0.data.toString()}")
            sendTopNotification(p0.data["title"].toString(), p0.data["body"].toString())
            if(true) {
                scheduleJob()
            } else {
                handleNow()
            }
        }
    }

    fun scheduleJob() {
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
    }

    fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }


    private fun sendTopNotification(title: String?, body: String) {
        val CHANNEL_DEFAULT_IMPORTANCE = "channel_id"
        val ONGOING_NOTIFICATION = 1

// 알림 클릭시 앱 화면 띄우는 intent 생성
        val notificationIntent = Intent(this,MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        notificationIntent.setAction("NOTI")
        val pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0)

// 알림 생성
        val notification = Notification.Builder(this,CHANNEL_DEFAULT_IMPORTANCE)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_DEFAULT_IMPORTANCE,"Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(ONGOING_NOTIFICATION,notification)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.i(TAG, "Refreshed token: +$p0")
    }
}