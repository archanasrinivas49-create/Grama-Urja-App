package com.gramaurja.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging

object NotificationHelper {

    private const val CHANNEL_ID   = "power_status_channel"
    private const val CHANNEL_NAME = "Power Status Updates"
    private const val CHANNEL_DESC = "Notifications when power status changes in your zone"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel    = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            channel.description = CHANNEL_DESC
            channel.enableLights(true)
            channel.lightColor = Color.GREEN
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(0, 500, 200, 500)

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun subscribeToZone(zoneId: String) {
        FirebaseMessaging.getInstance()
            .subscribeToTopic(zoneId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Subscribed to zone: $zoneId")
                }
            }
    }

    fun unsubscribeFromZone(zoneId: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(zoneId)
    }

    fun showLocalNotification(
        context : Context,
        title   : String,
        message : String,
        notifId : Int = System.currentTimeMillis().toInt()
    ) {
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)

        if (launchIntent != null) {
            launchIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))

        val notification = builder.build()

        val canNotify = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (canNotify) {
            NotificationManagerCompat.from(context).notify(notifId, notification)
        }
    }
}