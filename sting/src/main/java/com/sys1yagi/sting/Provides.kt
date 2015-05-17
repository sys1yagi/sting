package com.sys1yagi.sting

import java.lang.annotation.Documented
import java.lang.annotation.ElementType.METHOD
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.RUNTIME
import java.lang.annotation.Target

public enum class Type {
    UNIQUE
    SET
    SET_VALUES
}

Documented
Target(METHOD)
Retention(RUNTIME)
annotation public class Provides(public val type: Type = Type.UNIQUE)
