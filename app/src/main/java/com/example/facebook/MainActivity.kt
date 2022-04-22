package com.example.facebook

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Base64.encode
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {

    lateinit var callbackManager: CallbackManager
    private val EMAIL = "email"
    var TAG = "agoncoding"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val button = findViewById<LoginButton>(R.id.login_button)
        button.setOnClickListener {
            button.setReadPermissions(listOf(EMAIL, "name"))

            callbackManager = CallbackManager.Factory.create()

            LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        toast("Logged successfully!")

                        Log.e(TAG, "onSuccess:")
                        val graphRequest =
                            GraphRequest.newMeRequest(result?.accessToken) { obj, response ->
                                try {
                                    if (obj.has("id")) {
                                        Log.d("FACEBOOKDATA", obj.getString("name"))
                                        Log.d("FACEBOOKDATA", obj.getString("email"))
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        val param = Bundle()
                        param.putString("fields", "name, email, id, picture.type(large)")
                        graphRequest.parameters = param
                        graphRequest.executeAsync()
                    }

                    override fun onCancel() {
                        Log.e(TAG, "onCancel: ")
                        toast("Cancelled")
                    }

                    override fun onError(error: FacebookException?) {
                        error?.printStackTrace()
                        toast("${error?.localizedMessage}")
                    }
                })
        }

        getHashkey()
    }

    fun toast(string:String){
        Toast.makeText(this@MainActivity, string, Toast.LENGTH_SHORT).show()
    }

    fun getHashkey() {
        try {
            val info = packageManager.getPackageInfo(
                applicationContext.packageName, PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.i("Base64", Base64.encodeToString(md.digest(), Base64.NO_WRAP))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("Name not found", e.message, e)
        } catch (e: NoSuchAlgorithmException) {
            Log.d("Error", e.message, e)
        }
    }

    private fun printHashKey() {
        try {
            val info: PackageInfo =
                this.packageManager.getPackageInfo(this.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(encode(md.digest(), 0))
                Log.d(TAG, "printHashKey() Hash key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.d(TAG, "printHashKey()", e)
        } catch (e: Exception) {
            Log.d(TAG, "printHashKey()", e)
        }

    }
}


