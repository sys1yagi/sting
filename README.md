
Sting
===========

Sting is DI library for Kotlin.

# Caution

Sting does not use APT. So there is a performance issue. You __should not__ use this for your product.

# Getting Started

Not yet upload to Bintray. You need checkout and reference library.

__build.gradle__

```groovy
dependencies {
    //make symbolic link into your project.
    compile project(path: ':sting')
}
```

__Make module__

Sting provides "Module", "Provides" annotations. You can use `javax.inject.Singleton`.

```java
import android.os.Handler
import android.os.Looper
import com.squareup.okhttp.OkHttpClient
import com.sys1yagi.sting.Module
import com.sys1yagi.sting.Provides
import javax.inject.Singleton

Module
public class AppModule {
    Provides
    Singleton
    public fun provideOkHttp(): OkHttpClient {
        return OkHttpClient()
    }

    Provides
    Singleton
    public fun provideHandler(): Handler {
        return Handler(Looper.getMainLooper())
    }
}
```

__Create ObjectGraph__

```java
public class SampleApplication : Application() {

    companion object {
        platformStatic var graph: ObjectGraph by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        graph = ObjectGraph.create(AppModule())
    }
}
```

__Inject__


```java
public class MainActivity : AppCompatActivity() {

    var okHttpClient: OkHttpClient by Delegates.notNull()
        [Inject] set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SampleApplication.graph.inject(this)
    }
}
```

## Constructor Injection

No arguments.

```java
public class AwesomeUtil [Inject] () {
    var model: Model by Delegates.notNull()
        [Inject] set
}
```

1 argument or more.

```java
public class AwesomeHelper [Inject] (var awesomeUtil: AwesomeUtil) {

}
```

## Singleton

```java
import com.sys1yagi.sting.Module
import com.sys1yagi.sting.Provides
import javax.inject.Singleton

Module
public class AppModule {
    Provides
    Singleton
    public fun provideOkHttp(): OkHttpClient {
        return OkHttpClient()
    }
}
```

constructor injection.

```java
Singleton
public class SingletonObject [Inject] () {
  //...
}
```

# License

```
 Copyright 2015 Toshihiro.Yagi

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ```
