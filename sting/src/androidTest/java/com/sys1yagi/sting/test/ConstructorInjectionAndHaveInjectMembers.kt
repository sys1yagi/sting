package com.sys1yagi.sting.test

import javax.inject.Inject
import kotlin.properties.Delegates

public class ConstructorInjectionAndHaveInjectMembers [Inject]
(var awesomeHelper: AwesomeHelper) {

    var model: Model by Delegates.notNull()
        [Inject] set
}
