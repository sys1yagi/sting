package com.sys1yagi.sting.test

import com.sys1yagi.sting.Module
import com.sys1yagi.sting.Provides

Module
open public class AppModule {

    Provides
    open public fun provideModel(): Model {
        return Model("model", 10)
    }
}
