package com.sys1yagi.sting.test

import com.sys1yagi.sting.Module
import com.sys1yagi.sting.Provides

Module
public class AppModule2 {
    Provides
    public fun providesAwesomeUtil(): AwesomeUtil {
        val awesomeUtil = AwesomeUtil()
        awesomeUtil.model = Model("awesome", 20)
        return awesomeUtil
    }
}
