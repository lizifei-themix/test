package com.test

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : Activity() {
    private lateinit var tokenText: TextView
    private lateinit var payloadText: TextView
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        buildUi()
        loadToken()
        renderSavedPayload()
        renderIntentExtras(intent)
    }

    override fun onResume() {
        super.onResume()
        if (::payloadText.isInitialized) renderSavedPayload()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        renderIntentExtras(intent)
        renderSavedPayload()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        root.addView(TextView(this).apply {
            text = "FCM Payload Test"
            textSize = 24f
        })

        root.addView(TextView(this).apply {
            text = "\nAndroid package: com.test\n\nFCM Token"
            textSize = 16f
        })

        tokenText = TextView(this).apply {
            text = "Loading..."
            setTextIsSelectable(true)
        }
        root.addView(tokenText)

        root.addView(Button(this).apply {
            text = "Copy FCM Token"
            setOnClickListener {
                if (token.isBlank()) {
                    Toast.makeText(this@MainActivity, "Token not ready", Toast.LENGTH_SHORT).show()
                } else {
                    val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("FCM Token", token))
                    Toast.makeText(this@MainActivity, "Copied", Toast.LENGTH_SHORT).show()
                }
            }
        })

        root.addView(Button(this).apply {
            text = "Refresh Token"
            setOnClickListener { loadToken() }
        })

        root.addView(TextView(this).apply {
            text = "\nLast Push Payload"
            textSize = 18f
        })

        payloadText = TextView(this).apply {
            text = "No push received yet."
            setTextIsSelectable(true)
        }
        root.addView(payloadText)

        root.addView(Button(this).apply {
            text = "Clear Payload"
            setOnClickListener {
                getSharedPreferences("fcm_test", MODE_PRIVATE)
                    .edit()
                    .remove("last_payload")
                    .apply()
                payloadText.text = "No push received yet."
            }
        })

        root.addView(TextView(this).apply {
            text = """

                Check these keys:
                - tdCampaignId
                - campaignId
                - campaign_id
                - td_campaign_id

                Send a Push from Engage Studio to the FCM token shown above.
                When a message is received, reopen this app if needed.
            """.trimIndent()
        })

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)
    }

    private fun loadToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                tokenText.text = "FCM token error:\n${task.exception?.message}"
                Log.e("FCM_TEST", "Token failure", task.exception)
                return@addOnCompleteListener
            }
            token = task.result
            tokenText.text = token
            Log.d("FCM_TEST", "TOKEN=$token")
        }
    }

    private fun renderSavedPayload() {
        val saved = getSharedPreferences("fcm_test", MODE_PRIVATE)
            .getString("last_payload", null)
        if (!saved.isNullOrBlank()) payloadText.text = saved
    }

    private fun renderIntentExtras(intent: Intent?) {
        val extras = intent?.extras ?: return
        if (extras.keySet().isEmpty()) return

        val lines = extras.keySet().sorted().map { key ->
            "$key = ${extras.get(key)}"
        }
        val block = "Launch Intent extras:\n" + lines.joinToString("\n")
        payloadText.text = block
        Log.d("FCM_TEST", block)
    }

    private fun requestNotificationPermission() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }
}
