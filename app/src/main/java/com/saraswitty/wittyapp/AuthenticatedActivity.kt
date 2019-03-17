package com.saraswitty.wittyapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import mu.KotlinLogging
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.view.View
import android.annotation.SuppressLint

// TODO Understand why we need the below line
@SuppressLint("ByteOrderMark")
class AuthenticatedActivity : AppCompatActivity() {
    private val logger = KotlinLogging.logger {}
    private var notificationManager: NotificationManager? = null

    // API 26 and above requires a notification channel to send notification
    private fun createNotificationChannel(id: String, name: String,
                                  description: String) {
        if (Build.VERSION.SDK_INT < 26) return

        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(id, name, importance)

        channel.description = description
        notificationManager?.createNotificationChannel(channel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authenticated_activity)

        // Get the current fb access token
        val accessToken = AccessToken.getCurrentAccessToken()

        val request = GraphRequest.newMeRequest(
                accessToken
        ) { `object`, response ->
            val textView: TextView = findViewById(R.id.txtName)
            textView.text = `object`.toString()
        }

        val parameters = Bundle()

        // Values that are requested
        parameters.putString("fields", "id, name, email")
        request.parameters = parameters
        logger.debug{"Sent async request for Facebook information of the user"}
        request.executeAsync()

        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Logout button functionality
        btnLogout.setOnClickListener(View.OnClickListener {
            if (AccessToken.getCurrentAccessToken() != null) {
                logger.debug{"Facebook logout called"}
                GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback {
                    AccessToken.setCurrentAccessToken(null)
                    LoginManager.getInstance().logOut()
                    finish()
                }).executeAsync()
            }
        })

        notificationManager =
                getSystemService(
                        Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(
                "com.saraswitty.wittymail.allNotifications",
                "Notifications",
                "Notifications")
    }

    // Button onClick function handler
    fun sendNotification(view: View) {
        val channelID = "com.saraswitty.wittymail.allNotifications"
        val notificationID = 101

        // Use NotificationCompat instead of Notification for backward compatibility
        val notification = NotificationCompat.Builder(this@AuthenticatedActivity,
                channelID)
                .setContentTitle("Example Notification")
                .setContentText("This is an  example notification.")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setChannelId(channelID)
                .build()

        // Send the notification
        notificationManager?.notify(notificationID, notification)
    }
}