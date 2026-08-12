# Ktor Recipe App — Clean Architecture (data / domain / app)

Two screens — a searchable recipe list and a recipe detail screen — using
**TheMealDB** (free, no API key required) via **Ktor Client**, structured with
a lightweight Clean Architecture: `domain` / `data` / `app`.

## Package layout

```
domain/                      <- pure Kotlin, zero Ktor/Android imports
  model/Meal.kt               MealSummary, MealDetail, Ingredient
  repository/MealRepository.kt   interface only
  usecase/GetMealsUseCase.kt
  usecase/GetMealDetailUseCase.kt

data/                        <- everything network/Ktor-related lives here
  remote/MealApiService.kt      Ktor HttpClient calls (main Ktor learning file)
  remote/dto/MealDto.kt         raw JSON-shaped classes
  repository/MealRepositoryImpl.kt  implements domain interface, DTO -> domain mapping
  di/NetworkModule.kt           builds the Ktor HttpClient (engine + plugins)
  di/RepositoryModule.kt        binds MealRepository -> MealRepositoryImpl

app/                          <- presentation layer
  list/MealListScreen.kt + MealListViewModel.kt
  detail/MealDetailScreen.kt + MealDetailViewModel.kt
  navigation/NavGraph.kt
  MainActivity.kt, KtorRecipeApp.kt
```

**The dependency rule:** `app` depends on `domain` (via use cases), `data`
depends on `domain` (implements its interface), but `domain` depends on
nothing. Ktor only appears in `data/remote/` and `data/di/NetworkModule.kt` —
nowhere else in the codebase imports it.

## Setup

1. Create a new Android Studio "Empty Activity" project, package
   `com.example.ktorrecipe`.
2. Merge `app/build.gradle.kts.snippet` into your `app/build.gradle.kts`.
3. Copy everything under `app/src/main/java/com/example/ktorrecipe/` into your
   project, keeping the `domain/`, `data/`, `app/` folder structure.
4. In `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />

<application
    android:name=".app.KtorRecipeApp"
    ...>
    <activity android:name=".app.MainActivity" ... />
</application>
```

5. Run it. Main screen opens pre-loaded with "chicken" recipes; type any
   keyword ("pasta", "beef", "soup") and tap Go to re-search. Tap a recipe to
   see the detail screen.

## Reading order, if you want to trace one request end to end

1. `app/list/MealListScreen.kt` — user taps "Go"
2. `app/list/MealListViewModel.kt` — calls `getMealsUseCase(query)`
3. `domain/usecase/GetMealsUseCase.kt` — forwards to the repository interface
4. `data/repository/MealRepositoryImpl.kt` — calls `MealApiService`, maps DTO -> domain
5. `data/remote/MealApiService.kt` — the actual `client.get(...).body<T>()` Ktor call
6. `data/di/NetworkModule.kt` — where that `client` was configured (engine + plugins)

Every file has comments explaining its role in this chain — that's the main
thing worth reading closely, more than the UI code.

## Ktor implementation, step by step — compared with Retrofit

If Retrofit is what you already know, this section walks through the same
steps side by side, so you can see exactly where Ktor does the same job
differently. Retrofit hides a lot of this behind annotations; Ktor spells
each step out as actual code you write yourself — that's really the whole
difference between the two.

### Step 1 — Add the dependency

**Retrofit:** one dependency (plus a converter, e.g. Gson or Moshi, for JSON).
```kotlin
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
```

**Ktor:** several small dependencies, because Ktor is built from separate
pieces you opt into individually — core, an engine, a JSON converter, logging.
```kotlin
implementation("io.ktor:ktor-client-core:2.3.12")
implementation("io.ktor:ktor-client-android:2.3.12")               // engine
implementation("io.ktor:ktor-client-content-negotiation:2.3.12")   // JSON plugin
implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
implementation("io.ktor:ktor-client-logging:2.3.12")               // optional
```
*(See `app/build.gradle.kts.snippet` in this project.)*

### Step 2 — Build the client

**Retrofit:** build a `Retrofit` object with a base URL and a converter factory.
```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://www.themealdb.com/api/json/v1/1/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

**Ktor:** build an `HttpClient`, but you also choose an **engine** (the thing
that actually sends bytes over the network — Android, OkHttp, CIO, etc.) and
**install plugins** one at a time for things Retrofit gives you for free
(JSON parsing, logging, timeouts).
```kotlin
val client = HttpClient(Android) {
    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    install(Logging) { level = LogLevel.BODY }
    install(HttpTimeout) { requestTimeoutMillis = 15_000 }
}
```
*(See `data/di/NetworkModule.kt` in this project.)*

This is the biggest mental shift: Retrofit's builder already knows how to
talk HTTP and parse JSON. Ktor's client starts nearly empty, and you turn on
each capability as a plugin. More typing, but nothing is hidden from you.

### Step 3 — Define the API calls

**Retrofit:** you write an **interface**. Each function is annotated with
its HTTP method and path; Retrofit generates the real implementation for you
at runtime.
```kotlin
interface MealApi {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealListResponseDto

    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): MealListResponseDto
}
```

**Ktor:** there's no interface and no code generation. You write a normal
class with normal suspend functions, and inside each one you call
`client.get(url) { ... }` yourself.
```kotlin
class MealApiService(private val client: HttpClient) {
    suspend fun searchMeals(query: String): MealListResponseDto =
        client.get("$BASE_URL/search.php") { parameter("s", query) }.body()

    suspend fun getMealById(id: String): MealListResponseDto =
        client.get("$BASE_URL/lookup.php") { parameter("i", id) }.body()
}
```
*(See `data/remote/MealApiService.kt` in this project.)*

Retrofit's `@GET`/`@POST`/`@PUT`/`@PATCH`/`@DELETE` annotations map to Ktor's
`client.get(...)`, `client.post(...)`, `client.put(...)`, `client.patch(...)`,
`client.delete(...)` — same HTTP verbs, just called directly as functions
instead of declared as annotations.

### Step 4 — Send the request and get a parsed object back

**Retrofit:** calling the interface function IS the network call — Retrofit
already knows (from the annotation + converter) how to turn the response into
your data class.
```kotlin
val result = mealApi.searchMeals("chicken")   // already a MealListResponseDto
```

**Ktor:** `client.get(...)` returns an `HttpResponse`. You then call
`.body<T>()` on it to deserialize that response into your data class, using
whatever type Kotlin can infer or that you specify.
```kotlin
val response: HttpResponse = client.get(url) { parameter("s", "chicken") }
val result: MealListResponseDto = response.body()
// or written in one line, like this project does:
val result = client.get(url) { parameter("s", "chicken") }.body<MealListResponseDto>()
```

### Step 5 — Use it in the repository/ViewModel

This step is basically identical in both — a suspend function call inside a
coroutine, wrapped in try/catch or `Result`. The architecture above this line
(repository, use case, ViewModel) doesn't care whether Retrofit or Ktor is
underneath, which is exactly why this project keeps Ktor contained to
`data/remote/` and `data/di/`.

### Quick side-by-side summary

| Step | Retrofit | Ktor |
|---|---|---|
| Dependency | 1 library + 1 converter | core + engine + JSON plugin + (optional) logging plugin |
| Client setup | `Retrofit.Builder()` | `HttpClient(engine) { install(plugins) }` |
| Defining calls | Interface with `@GET`/`@POST`/etc. annotations | Regular suspend functions calling `client.get()`/`client.post()`/etc. |
| JSON parsing | Handled by the converter you registered | Handled by the `ContentNegotiation` plugin you installed |
| Getting the result | Function call returns the parsed object directly | `client.get(...)` returns a response; you call `.body<T>()` to parse it |
| Code generation | Yes (Retrofit generates the interface implementation) | No — you write the actual calling code yourself |

## Next experiments

- Swap `Android` engine for `OkHttp` in `NetworkModule.kt` — one-line change.
- Add a Ktor `Auth` plugin against an API that needs a bearer token, to see
  Ktor's auth handling instead of Retrofit's `Interceptor`.
- Write a `FakeMealRepository : MealRepository` for a Compose `@Preview` — this
  is the payoff of having the domain interface at all.
