package com.saraswitty.wittyapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import mu.KotlinLogging
import java.util.*

class MainActivity : AppCompatActivity() {
    private val logger = KotlinLogging.logger {}
    private var callbackManager: CallbackManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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