package com.saraswitty.wittyapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import mu.KotlinLogging

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
        val resultIntent = Intent(this, NewUpcomingEvent::class.java)

        val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Snooze Intent for the snooze button in the notification
        val snoozeIntent = Intent(this, SnoozeActivity::class.java)
        val snoozePendingIntent: PendingIntent =
                PendingIntent.getActivity(this, 1, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Event Intent for the sign me up button in the notification
        val checkEventPageIntent = Intent(this, NewUpcomingEvent::class.java)
        val checkEventPagePendingIntent: PendingIntent =
                PendingIntent.getActivity(this, 1, checkEventPageIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Use NotificationCompat instead of Notification for backward compatibility
        val notification = NotificationCompat.Builder(this@AuthenticatedActivity,
                channelID)
                .setContentTitle("New Event")
                .setContentText("This is an example event.")
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.sneh_large_icon))
                .setSmallIcon(R.drawable.sneh_small_icon)
                .setChannelId(channelID)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.sneh_small_icon, "Sign Me Up",
                        checkEventPagePendingIntent)
                .addAction(R.drawable.sneh_small_icon, "Snooze",
                        snoozePendingIntent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(resources, R.drawable.sneh_large_icon))
                        .setBigContentTitle("Hello")
                        .bigLargeIcon(null))
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText("This is some random loooooooooooooong text"))
                .build()

        // Send the notification
        notificationManager?.notify(notificationID, notification)
    }
}