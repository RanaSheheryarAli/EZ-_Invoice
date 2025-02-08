package com.example.ezinvoice.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ezinvoice.apis.SignupApi
import com.example.ezinvoice.models.AppUser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignupViewmodel : ViewModel() {

    // Two-way binding using MutableLiveData
    val UsernameLD = MutableLiveData<String>("")
    val EmailLD = MutableLiveData<String>("")
    val passwordLD = MutableLiveData<String>("")

    var attemptedSignup = false //


    private val _issuccessfull = MutableLiveData(false)
    val issuccessfull: LiveData<Boolean> get() = _issuccessfull

    // Retrofit setup
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.100.22:5000/") // ✅ Use 10.0.2.2 for Emulator
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(SignupApi::class.java)

    // Signup button click handler
    fun onSignupClick() {
        val username = UsernameLD.value?.trim() ?: ""
        val email = EmailLD.value?.trim() ?: ""
        val password = passwordLD.value?.trim() ?: ""

        attemptedSignup = true
        Log.d("Signup", "Username: $username, Email: $email, Password: $password")

        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            val request = AppUser(username, email, password)

            // Convert request to JSON and log it
            val gson = com.google.gson.Gson()
            Log.d("Signup", "JSON Sent: ${gson.toJson(request)}")

            val call = api.signup(request)
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    if (response.isSuccessful) {
                        Log.d("Signup", "Signup Successful")
                        _issuccessfull.value = true
                    } else {
                        Log.d("Signup", "Signup Failed: ${response.errorBody()?.string()}")
                        _issuccessfull.value = false
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.d("Signup", "Network Error: ${t.message}")
                    _issuccessfull.value = false
                }
            })
        } else {
            _issuccessfull.value = false
        }
    }

}
