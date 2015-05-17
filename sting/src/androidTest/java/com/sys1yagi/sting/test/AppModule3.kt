package com.sys1yagi.sting.test

import com.sys1yagi.sting.Provides

public class AppModule3 {
    Provides
    open public fun provideModel(): Model {
        return Model("model2", 11)
    }
}
