package com.saraswitty.wittyapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import mu.KotlinLogging
import java.util.*

class MainActivity : AppCompatActivity() {
    private val logger = KotlinLogging.logger {}
    private var callbackManager: CallbackManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        logger.error("getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                    // Log and toast
                    val msg = "[Firebase] Instance ID token: " + token
                    logger.info(msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                })

        FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener { task ->
                    var msg = "Subscribed to topic 'weather'"
                    if (!task.isSuccessful) {
                        msg = "Failed to subscribe to topic 'weather'"
                    }
                    logger.info("[Firebase] Subscribe to topic 'weather': " + msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }

        val btnLoginFacebook = findViewById<Button>(R.id.btnLoginFacebook)

        // Add FB callback to login button
        btnLoginFacebook.setOnClickListener({
            callbackManager = CallbackManager.Factory.create()

            // List of user information that are requested
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onSuccess(loginResult: LoginResult) {
                            logger.debug{"Facebook token: " + loginResult.accessToken.token}
                            val intent = Intent(applicationContext, AuthenticatedActivity::class.java)
                            startActivity(intent)
                        }

                        override fun onCancel() {
                            logger.error{"Login Cancelled"}

                        }

                        override fun onError(error: FacebookException) {
                            logger.error{"Login Failed"}

                        }
                    })
        })
    }

    // TODO Understand what this part of the code does and comment the same
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }
}