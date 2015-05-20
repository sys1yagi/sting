package com.sys1yagi.sting

import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.HashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * http://en.wikipedia.org/wiki/Bee_sting
 */
public class Sting(val modules: Array<out Any>) : ObjectGraph {

    var injectMethodCache = HashMap<Class<in Any>, List<Method>>()

    var providedMethods = HashMap<Any, List<Method>>()

    var singletonHolder = HashMap<Class<*>, Any>()

    init {
        modules.forEach {
            inject(it)
        }
    }

    fun extractMethodInjection(clazz: Class<in Any>, methods: MutableList<Method>): List<Method> {
        if (injectMethodCache.containsKey(clazz)) {
            methods.addAll(injectMethodCache.get(clazz))
            return methods
        }
        val fields = clazz.getDeclaredMethods()
        fields.forEach {
            val annotation = it.getAnnotation(javaClass<Inject>());
            if (annotation != null) {
                methods.add(it)
            }
        }
        injectMethodCache.put(clazz, methods)
        return methods
    }

    fun <T> extractMethodInjection(target: T): List<Method> {
        var methods = ArrayList<Method>()
        var clazz: Class<in Any> = target.javaClass
        do {
            extractMethodInjection(clazz, methods)
            clazz = clazz.getSuperclass()
        } while (clazz.getSuperclass() != null)

        return methods
    }

    fun getModuleProvidesMethods(module: Any): List<Method> {
        if (providedMethods.containsKey(module)) {
            return providedMethods.get(module)
        }
        val methods = module.javaClass.getDeclaredMethods()
        val provides = ArrayList<Method>()
        methods.forEach {
            val annotation = it.getAnnotation(javaClass<Provides>())
            if (annotation != null) {
                provides.add(it)
            }
        }
        providedMethods.put(module, provides)
        return provides
    }

    fun <T> injectFromModule(target: T, setter: Method, module: Any): Boolean {
        val provides = getModuleProvidesMethods(module)
        val args = setter.getParameterTypes().first()
        provides.forEach {
            val clazz = it.getReturnType()
            if (clazz.equals(args)) {
                //umm...
                setter.setAccessible(true)
                val instance = if (singletonHolder.containsKey(args)) {
                    singletonHolder.get(args)
                } else {
                    it.setAccessible(true)
                    it.invoke(module)
                }
                if (it.getAnnotation(javaClass<Singleton>()) != null) {
                    singletonHolder.put(args, instance)
                }
                setter.invoke(target, instance)
                return true
            }
        }
        return false
    }

    fun <T> injectFromModules(target: T, setter: Method): Boolean {
        modules.forEach {
            if (injectFromModule(target, setter, it)) {
                return true
            }
        }
        return false
    }

    fun <T> extractConstructor(argsType: Class<T>): Constructor<T> {
        val constructors = argsType.getConstructors()
        constructors.forEach {
            val annotation = it.getAnnotation(javaClass<Inject>())
            if (annotation != null) {
                return it as Constructor<T>
            }
        }
        throw IllegalArgumentException("can't found constructor for injection : " + argsType)
    }

    fun <T> getFromModule(clazz: Class<T>, module: Any): T? {
        val provides = getModuleProvidesMethods(module)
        provides.forEach {
            val returnType = it.getReturnType()
            if (clazz.equals(returnType)) {
                return it.invoke(module) as T
            }
        }
        return null
    }

    fun <T> getFromModules(clazz: Class<T>): T? {
        modules.forEach {
            val value = getFromModule(clazz, it)
            if (value != null) {
                return value
            }
        }
        return null
    }

    fun <T> instantiate(clazz: Class<T>): T {
        val constructor = extractConstructor(clazz)
        val types = constructor.getParameterTypes()
        val args = ArrayList<Any>()
        for (i in 0..types.size() - 1) {
            val type = types.get(i)
            var value = get(type)
            args.add(value)
        }
        val instance =
                if (singletonHolder.containsKey(clazz)) {
                    return singletonHolder.get(clazz) as T
                } else if (args.isEmpty()) {
                    constructor.newInstance()
                } else {
                    constructor.newInstance(*args.toArray())
                }
        if (clazz.getAnnotation(javaClass<Singleton>()) != null) {
            singletonHolder.put(clazz, instance)
        }
        inject(instance)
        return instance!!
    }

    fun <T> injectConstructor(target: T, setter: Method) {
        val clazz = setter.getParameterTypes().first()
        val instance = instantiate(clazz)
        setter.invoke(target, instance)
    }

    override fun <T> get(clazz: Class<T>): T {
        var value: T? = getFromModules(clazz)
        if (value != null) {
            return value!!
        }
        value = instantiate(clazz)
        if (value != null) {
            return value!!
        }

        throw IllegalStateException("can't find object :" + clazz)
    }

    override fun <T> inject(target: T) {
        if (target == null) {
            return
        }
        //Extract injection targets.
        //Sting supports only setter method injection.
        //
        //var a:A by Delegates.notNull()
        //[Inject] set
        //
        //The idiom generate setter method annotated to the class file.
        val setters: List<Method> = extractMethodInjection(target)

        setters.forEach {
            //inject from modules
            if (!injectFromModules(target, it)) {
                //constructor injection
                injectConstructor(target, it)
            }
        }
    }
}
