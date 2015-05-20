package com.sys1yagi.sting

import android.app.Application
import android.support.test.runner.AndroidJUnit4
import android.test.ApplicationTestCase
import com.sys1yagi.sting.test.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.properties.Delegates

RunWith(javaClass<AndroidJUnit4>())
public class ObjectGraphTest : ApplicationTestCase<Application>(javaClass<Application>()) {
    Test
    public fun createObjectGraph() {
        val graph = ObjectGraph.create(AppModule())
        assertThat(graph, `is`(notNullValue()))
    }

    //module provides
    class Injected {
        var model: Model by Delegates.notNull()
            [Inject] set
    }

    Test
    public fun provides() {
        val graph = ObjectGraph.create(AppModule())
        val injected = Injected()
        graph.inject(injected)
        assertThat(injected.model, `is`(notNullValue()))
        assertThat(injected.model.a, `is`("model"))
        assertThat(injected.model.b, `is`(10))
    }

    //get
    Test
    public fun get() {
        val graph = ObjectGraph.create(AppModule())
        val model = graph.get(javaClass<Model>())
        assertThat(model, `is`(notNullValue()))
        assertThat(model!!.a, `is`("model"))
        assertThat(model!!.b, `is`(10))
    }

    //constructor injection
    class Injected2 {
        var awesomeUtil: AwesomeUtil by Delegates.notNull()
            [Inject] set
    }

    Test
    public fun constructorInjection() {
        val graph = ObjectGraph.create(AppModule())
        val injected = Injected2()
        graph.inject(injected)
        assertThat(injected.awesomeUtil, `is`(notNullValue()))
        assertThat(injected.awesomeUtil.model, `is`(notNullValue()))
        assertThat(injected.awesomeUtil.model.a, `is`("model"))
        assertThat(injected.awesomeUtil.model.b, `is`(10))
    }

    //constructor has a argument.
    class Injected3 {
        var awesomeHelper: AwesomeHelper by Delegates.notNull()
            [Inject] set
    }

    Test
    public fun constructorInjectionComplex() {
        val graph = ObjectGraph.create(AppModule())
        val injected = Injected3()
        graph.inject(injected)
        assertThat(injected.awesomeHelper, `is`(notNullValue()))
        assertThat(injected.awesomeHelper.awesomeUtil, `is`(notNullValue()))
        assertThat(injected.awesomeHelper.awesomeUtil.model, `is`(notNullValue()))
        assertThat(injected.awesomeHelper.awesomeUtil.model.a, `is`("model"))
        assertThat(injected.awesomeHelper.awesomeUtil.model.b, `is`(10))
    }

    //constructor has 3 arguments.
    class Injected4 {
        var instance: ConstructorInjection3Arguments by Delegates.notNull()
            [Inject] set
    }

    Test
    public fun constructorHas3Arguments() {
        val graph = ObjectGraph.create(AppModule())
        val injected = Injected4()
        graph.inject(injected)
        assertThat(injected.instance, `is`(notNullValue()))
        assertThat(injected.instance.awesomeUtil, `is`(notNullValue()))
        assertThat(injected.instance.awesomeUtil.model, `is`(notNullValue()))
        assertThat(injected.instance.awesomeUtil.model.a, `is`("model"))
        assertThat(injected.instance.awesomeUtil.model.b, `is`(10))
        assertThat(injected.instance.model1, `is`(notNullValue()))
        assertThat(injected.instance.model2, `is`(notNullValue()))
        assertThat(injected.instance.model1.hashCode(),
                `is`(not(injected.instance.model2.hashCode())))
    }

    //module override
    class TestModule : AppModule() {
        Provides
        override fun provideModel(): Model {
            return Model("test", 100)
        }
    }

    Test
    public fun moduleOverride() {
        val graph = ObjectGraph.create(TestModule())
        val model = graph.get(javaClass<Model>())
        assertThat(model, `is`(notNullValue()))
        assertThat(model.a, `is`("test"))
        assertThat(model.b, `is`(100))
    }

    //multi module

    class Injected5 {
        var model: Model by Delegates.notNull()
            [Inject] set
        var awesomeUtil: AwesomeUtil by Delegates.notNull()
            [Inject] set
    }

    Test
    public fun multiModule() {
        val graph = ObjectGraph.create(AppModule(), AppModule2())
        val injected = Injected5()
        graph.inject(injected)
        assertThat(injected.model, `is`(notNullValue()))
        assertThat(injected.model.a, `is`("model"))
        assertThat(injected.model.b, `is`(10))
        assertThat(injected.awesomeUtil, `is`(notNullValue()))
        assertThat(injected.awesomeUtil.model, `is`(notNullValue()))
        assertThat(injected.awesomeUtil.model.a, `is`("awesome"))
        assertThat(injected.awesomeUtil.model.b, `is`(20))
    }

    //provides order
    Test
    public fun providesOrder() {
        val graph = ObjectGraph.create(AppModule3(), AppModule(), AppModule2())
        val injected = Injected5()
        graph.inject(injected)
        assertThat(injected.model, `is`(notNullValue()))
        assertThat(injected.model.a, `is`("model2"))
        assertThat(injected.model.b, `is`(11))
        assertThat(injected.awesomeUtil, `is`(notNullValue()))
        assertThat(injected.awesomeUtil.model, `is`(notNullValue()))
        assertThat(injected.awesomeUtil.model.a, `is`("awesome"))
        assertThat(injected.awesomeUtil.model.b, `is`(20))
    }

    //singleton
    Test
    public fun singletonProvides() {
        val graph = ObjectGraph.create(AppModule4());
        val injected1 = Injected()
        graph.inject(injected1)
        val injected5 = Injected5()
        graph.inject(injected5)
        assertThat(injected1.model, `is`(notNullValue()))
        assertThat(injected5.model, `is`(notNullValue()))
        assertThat(injected1.model, `is`(injected5.model))
    }

    class Injected6 {
        var a: SingletonObject by Delegates.notNull()
            [Inject] set
        var b: SingletonObject by Delegates.notNull()
            [Inject] set
    }

    Test
    public fun singletonConstructor() {
        val graph = ObjectGraph.create(AppModule());
        val injected = Injected6()
        graph.inject(injected)
        assertThat(injected.a, `is`(notNullValue()))
        assertThat(injected.b, `is`(notNullValue()))
        assertThat(injected.a, `is`(injected.b))
    }

    //inject to module
    Test
    public fun injectToModule() {
        val graph = ObjectGraph.create(AppModule(), AppModule5());
        val injected = Injected2()
        graph.inject(injected)
        assertThat(injected.awesomeUtil, `is`(notNullValue()))
        assertThat(injected.awesomeUtil.model, `is`(notNullValue()))
        assertThat(injected.awesomeUtil.model.a, `is`("model"))
        assertThat(injected.awesomeUtil.model.b, `is`(10))
    }

    //inheritance
    class Injected7 {
        var awesomeUtil2: AwesomeUtil2 by Delegates.notNull()
            [Inject] set
    }

    Test
    public fun inheritance() {
        val graph = ObjectGraph.create(AppModule())
        val injected = Injected7()
        graph.inject(injected)
        assertThat(injected.awesomeUtil2, `is`(notNullValue()))
        assertThat(injected.awesomeUtil2.model, `is`(notNullValue()))
        assertThat(injected.awesomeUtil2.model.a, `is`("model"))
        assertThat(injected.awesomeUtil2.model.b, `is`(10))
    }

    //bind provider

    //include annotation

    //TODO
    //scope support

}
