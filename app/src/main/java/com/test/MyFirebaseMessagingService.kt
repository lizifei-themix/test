package com.test

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TEST", "NEW_TOKEN=$token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val dataJson = JSONObject()
        message.data.forEach { (k, v) -> dataJson.put(k, v) }
        val root = JSONObject().apply {
            put("from", message.from ?: JSONObject.NULL)
            put("messageId", message.messageId ?: JSONObject.NULL)
            put("sentTime", message.sentTime)
            put("data", dataJson)
            put("notificationTitle", message.notification?.title ?: JSONObject.NULL)
            put("notificationBody", message.notification?.body ?: JSONObject.NULL)
        }
        val pretty = root.toString(2)
        getSharedPreferences("fcm_test", MODE_PRIVATE).edit().putString("last_payload", pretty).apply()
        Log.d("FCM_TEST", "RAW_PAYLOAD=$pretty")
        listOf("tdCampaignId", "campaignId", "campaign_id", "td_campaign_id").forEach { key ->
            message.data[key]?.let { value -> Log.d("FCM_TEST", "$key=$value") }
        }
    }
}
