package com.sys1yagi.sting.test

import com.sys1yagi.sting.Module
import com.sys1yagi.sting.Provides

import javax.inject.Inject
import kotlin.properties.Delegates

Module
public class AppModule5 {

    var model: Model by Delegates.notNull()
        [Inject] set

    Provides
    public fun provideModel(): AwesomeUtil {
        val awesomeUtil = AwesomeUtil()
        awesomeUtil.model = model
        return awesomeUtil
    }
}
