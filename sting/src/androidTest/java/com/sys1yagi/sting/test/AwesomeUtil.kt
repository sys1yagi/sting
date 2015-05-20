package com.sys1yagi.sting.test


import javax.inject.Inject
import kotlin.properties.Delegates

open public class AwesomeUtil [Inject]() {

    var model: Model by Delegates.notNull()
        [Inject] set
}
