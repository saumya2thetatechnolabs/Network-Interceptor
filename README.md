# Network Interceptor [![Release](https://jitpack.io/v/saumya2thetatechnolabs/Network-Interceptor.svg?style=flat-square)](https://jitpack.io/#saumya2thetatechnolabs/Network-Interceptor)

Network Interceptor is a library to track your network calls to help you debug your code better. Just shake your device and there it is! 💣 🔥
Happy Coding :) 👨🏽‍💻

<p align="center">
  <img src="https://i.ibb.co/C51Bn48/Screenshot-20220504-164723-Network-Interceptor.jpg" width="350" title="Screenshot 1">
  <img src="https://i.ibb.co/FqGnsVz/Screenshot-20220504-164731-Network-Interceptor.jpg" width="350" alt="accessibility text">
</p>

## Installation

### Import library manually

Install this library module by importing it via File -> New -> Import Module -> Choose the path to
this library

### Use library via adding dependency

Add it in your settings.gradle:

```groovy
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your app-level build.gradle:

```groovy
dependencies {
    implementation 'com.github.saumya2thetatechnolabs:Network-Interceptor:{latest-version}'
}
```

## Implementation

Register sensor listener in you Root activity which stays alive for almost the entire lifecycle of app.
> A better way to handle all the network traces is to register sensor listener on Application lifecycle.

```kotlin
import com.thetatechnolabs.networkinterceptor.gesture.GestureUtils.registerSensorListener
import com.thetatechnolabs.networkinterceptor.gesture.GestureUtils.unRegisterSensorListener

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Register sensor listener in your onCreate
    this.registerSensorListener()
}

override fun onDestroy() {
    super.onDestroy()

    // unregister sensor listener
    this.unRegisterSensorListener()
}
```
---
### With Retrofit

To trace the network call for logging, add `NetworkInterceptor(context)` as below.
> Make sure that you have configured retrofit and gson
>
>Context is required to get access database when the network call is traced

```kotlin
client(OkHttpClient.Builder().apply {
    addInterceptor(NetworkInterceptor(context))
}.build())
```

---
### With Volley

This library provide volley request queue as singleton out of the box. to use it, just access using
any context i.e. `context.volleyRequestQueue` and queue request to it
using `volleyRequestQueue.add()`.

>Make sure volley dependency is configured

To create a GET request use `makeAGetRequest` DSL function as below and add it to request queue.

```kotlin
context.makeAGetRequest<T> {
    url = "url-to-target"
    headers = mutableMapOf("key" to "value")
    modelClass = { YourClass<T> }::class.java
    onSuccess = { T ->
        networkCallResonse with model<T>
    }
    onFailure =
        { localizedMessage, message, networkTimeMs ->
            get network call failure details here with above parameters
        }
}
```

To create a POST request use `makeAPostRequest` DSL function as below and add it to request queue.

```kotlin
context.makeAPostRequest<T> {
    url = "url-to-target"
    headers = mutableMapOf("key" to "value")
    requestParams = mutableMapOf("key" to "value")
    requestBody = JSONObject()
    modelClass = { YourClass<T> }::class.java
    onSuccess = { T ->
        networkCallResonse with model<T>
    }
    onFailure = { localizedMessage, message, networkTimeMs ->
        get network call failure details here with above parameters
    }
}
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would
like to change.

## Copyright

Created by Saumya Macwan on 3rd May 2022. Copyright (c) 2022 . All rights reserved. Last modified
11th May 2022.
