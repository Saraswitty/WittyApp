package com.saraswitty.wittyapp

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class NewUpcomingEvent : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_upcoming_event)

        // Remove notification that called this activity
        // TODO Create new class for notification
        val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(101)
    }
}
