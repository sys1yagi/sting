package com.sys1yagi.sting.test

import com.sys1yagi.sting.Module
import com.sys1yagi.sting.Provides

import javax.inject.Singleton

Module
public class AppModule4 {

    Provides
    Singleton
    public fun providesModel(): Model {
        return Model("singleton", 1000)
    }

}
