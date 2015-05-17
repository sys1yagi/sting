package com.sys1yagi.sting.sample.modules

import android.os.Handler
import android.os.Looper
import com.squareup.okhttp.OkHttpClient
import com.sys1yagi.sting.Module
import com.sys1yagi.sting.Provides
import javax.inject.Singleton

Module
public class AppModule {
    Provides
    Singleton
    public fun provideOkHttp(): OkHttpClient {
        return OkHttpClient()
    }

    Provides
    Singleton
    public fun provideHandler(): Handler {
        return Handler(Looper.getMainLooper())
    }
}
