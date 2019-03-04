package com.wittyapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager

class AuthenticatedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.authenticated_activity)

        val accessToken = AccessToken.getCurrentAccessToken()
        val request = GraphRequest.newMeRequest(
                accessToken
        ) { `object`, response ->
            val textView: TextView = findViewById(R.id.txtName)
            textView.text = `object`.toString()
        }

        val parameters = Bundle()
        parameters.putString("fields", "id, name, email")
        request.parameters = parameters
        request.executeAsync()

        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnLogout.setOnClickListener(View.OnClickListener {
            if (AccessToken.getCurrentAccessToken() != null) {
                GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback {
                    AccessToken.setCurrentAccessToken(null)
                    LoginManager.getInstance().logOut()
                    finish()
                }).executeAsync()
            }
        })
    }
}