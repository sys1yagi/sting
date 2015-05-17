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

    var injectMethodCache = HashMap<Class<Any>, List<Method>>()

    var providedMethods = HashMap<Any, List<Method>>()

    var singletonHolder = HashMap<Class<*>, Any>()

    fun <T> extractMethodInjection(target: T): List<Method> {
        val clazz: Class<Any> = target.javaClass
        if (injectMethodCache.containsKey(clazz)) {
            return injectMethodCache.get(clazz)
        }

        val fields = clazz.getDeclaredMethods()
        val methods = ArrayList<Method>()
        fields.forEach {
            val annotation = it.getAnnotation(javaClass<Inject>());
            if (annotation != null) {
                methods.add(it)
            }
        }
        injectMethodCache.put(clazz, methods)
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

    fun <T> injectFromModule(target: T, method: Method, module: Any): Boolean {
        val provides = getModuleProvidesMethods(module)
        provides.forEach {
            val clazz = it.getReturnType()
            val args = method.getParameterTypes().first()
            if (clazz.equals(args)) {
                //umm...
                method.setAccessible(true)
                val instance = if (singletonHolder.containsKey(args)) {
                    singletonHolder.get(args)
                } else {
                    it.setAccessible(true)
                    it.invoke(module)
                }
                if (it.getAnnotation(javaClass<Singleton>()) != null) {
                    singletonHolder.put(args, instance)
                }
                method.invoke(target, instance)
                return true
            }
        }
        return false
    }

    fun <T> injectFromModules(target: T, method: Method): Boolean {
        @ModuleInject modules.forEach {
            if (injectFromModule(target, method, it)) {
                @ModuleInject return true
            }
        }
        return false
    }

    fun <T> extractConstructor(argsType: Class<T>): Constructor<T> {
        val constructors = argsType.getConstructors()
        @FindConstructor constructors.forEach {
            it.getAnnotations()
            val annotation = it.getAnnotation(javaClass<Inject>())
            if (annotation != null) {
                @FindConstructor return it as Constructor<T>
            }
        }
        throw IllegalArgumentException("can't found constructor for injection : " + argsType)
    }

    fun <T> getFromModule(clazz: Class<T>, module: Any): T? {
        val provides = getModuleProvidesMethods(module)
        @ModuleGet provides.forEach {
            val returnType = it.getReturnType()
            if (clazz.equals(returnType)) {
                @ModuleGet return it.invoke(module) as T
            }
        }
        return null
    }

    fun <T> getFromModules(clazz: Class<T>): T? {
        @ModuleGet modules.forEach {
            val value = getFromModule(clazz, it)
            if (value != null) {
                @ModuleGet return value
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
