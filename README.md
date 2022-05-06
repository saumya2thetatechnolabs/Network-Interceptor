# Network Interceptor [![Release](https://jitpack.io/v/saumya2thetatechnolabs/Network-Interceptor.svg)](https://jitpack.io/#saumya2thetatechnolabs/Network-Interceptor)

Network Interceptor is a library to track your network calls to help you debug your code better.
Happy Coding :)

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

## Usage

Usage in MainActivity.
(This can also be done on Application lifecycle. Just register it once the app is created and
unregister it when it terminates.)

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

    // unregister it on onDestroy
    this.unRegisterSensorListener()
}
```

Must add an OkHttpClient as below in Retrofit Builder.
(Context is required to get the database instance when the network call is fired.)

```kotlin
client(OkHttpClient.Builder().apply {
    addInterceptor(NetworkInterceptor(context))
}.build())
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would
like to change.

Please make sure to update tests as appropriate.

## Copyright

Created by Saumya Macwan on 3rd May 2022. Copyright (c) 2022 . All rights reserved. Last modified
6th May 2022.
