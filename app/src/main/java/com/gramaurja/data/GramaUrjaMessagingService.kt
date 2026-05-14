package com.gramaurja.data

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class GramaUrjaMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "Power Status Update"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "Power status has changed in your zone"

        NotificationHelper.showLocalNotification(
            context = applicationContext,
            title   = title,
            message = body
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("FCM Token: $token")
    }
}