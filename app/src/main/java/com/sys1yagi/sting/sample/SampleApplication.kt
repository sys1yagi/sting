package com.sys1yagi.sting.sample

import android.app.Application
import com.sys1yagi.sting.ObjectGraph
import com.sys1yagi.sting.sample.modules.AppModule
import kotlin.platform.platformStatic
import kotlin.properties.Delegates

public class SampleApplication : Application() {

    companion object {
        platformStatic var graph: ObjectGraph by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        graph = ObjectGraph.create(AppModule())
    }
}
