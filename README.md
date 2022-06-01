<h1 align="center">Network Interceptor
  <a href="https://jitpack.io/#saumya2thetatechnolabs/Network-Interceptor"><img alt="Google" src="https://jitpack.io/v/saumya2thetatechnolabs/Network-Interceptor.svg"/>
  </a>
  <br>
</h1>

Network Interceptor is a library to track your network calls to help you debug your code better.
Just shake your device and there it is! 💣 🔥, This library also helps with a structured network
calls (in Retrofit). Please find more details in Implementation section down below. 👇🏽

<p align="center">
  <img src="https://github.com/saumya2thetatechnolabs/Network-Interceptor/blob/phase-two/screenshots/NetworkInterceptorImage.jpg" title="Screenshot 1">
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

Register sensor listener in you Root activity which stays alive for almost the entire lifecycle of
app.
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

#### Structuring network calls for better response handling (MVVM Architecture)

`NetworkResponseCallAdapterFactory` is a call adapter which wraps your usual retrofit
response `Response<T>` into `BaseResponse<T>` which is a sealed interface three implementation data
classes for response handling delegation.

To leverage it add this call adaptor factory to retrofit call adapter factory as below:

```kotlin
Retrofit.Builder()
    .addCallAdapterFactory(NetworkResponseCallAdapterFactory.create())

suspend fun fetchData(): BaseResponse<Data> // Respective model class
```

It's a good idea to have a data source to fetch data to repository :

```kotlin
class RemoteDataSource {
    suspend operator fun invoke(): BaseResponse<Data> {
        return ApiClientCompanion.networkService.fetchData()
    }
}
```

Repository:

```kotlin
class Repository(
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun fetchData(
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    ): Flow<Data> = flow {
        val response = remoteDataSource.invoke()

        response.onSuccess {
            emit(it)
        }.onError {
            onError(it)
        }.onException {
            onError(it.message)
        }
    }.cancellable() // To utilise onCompletion code block
        .onStart { onStart() }
        .onCompletion { onComplete() }
        .flowOn(Dispatchers.IO)
}
```

ViewModel:

```kotlin
class ViewModel(
    private val repo: Repository
) {
    fun fetchData(): StateFlow<Data> = repo.fetchData(
        onStart = {/* Loader Starts */ },
        onComplete = {/* Loader Ends */ },
        onError = {/* Handle error */ }
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        initialValue
    )
}
```

This `StateFlow` can be collected in activity to subscribe to ui, This flow is cancellable thus its
a good idea to unsubscribe to flow collector once our UI has been modified with data till the next
network call

```kotlin
val dataNetworkCall = lifecycleScope.launch {
    viewModel.fetchData().collectLatest { data ->

    }
}

// cancel this job once its done its job xD.
dataNetworkCall.cancel()
```

---

### With Volley

This library provide volley request queue as singleton out of the box. to use it, just access using
any context i.e. `context.volleyRequestQueue` and queue request to it
using `volleyRequestQueue.add()`.

> Make sure volley dependency is configured

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
