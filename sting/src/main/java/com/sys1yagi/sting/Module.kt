package com.sys1yagi.sting

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

Retention(RetentionPolicy.RUNTIME)
Target(ElementType.TYPE)
annotation public class Module(
        public vararg val includes: Class<*> = array()
)
