package com.sys1yagi.sting.sample.api

import android.os.Handler
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import java.io.IOException
import javax.inject.Inject
import kotlin.properties.Delegates

public class KotlinSite {

    @Inject constructor()

    private var handler: Handler by Delegates.notNull()
        @Inject set

    private var okHttpClient: OkHttpClient by Delegates.notNull()
        @Inject set

    fun getSite(success: (String?) -> Unit, error: (Exception?) -> Unit) {
        val request = Request.Builder().url("http://kotlinlang.org/").get().build()
        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(request: Request?, e: IOException?) {
                handler.post({
                    error(e)
                })
            }

            override fun onResponse(response: Response?) {
                val string = response?.body()?.string()
                handler.post({
                    success(string)
                })
            }
        })
    }
}
