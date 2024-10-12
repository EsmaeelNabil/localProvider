## LocalProvider

Help to create LocalCompositeProviders statefull holders that can be used between `composables` and screens.

##### Example

this is the only thing you need to do to create a local stateful provider

```kotlin
@ProvidesLocalOf(type = String::class)
interface LoginInfo
```

now you have access to `ProvideLocalLoginInfo{ LocalLoginInfo.current }` and `LocalLoginInfo.current` in your composable and it's children

---

#### Example 
```kotlin
@Composable
fun HomeScreen() {
    
    ProvideLocalLoginInfo {
        val loginInfo = LocalLoginInfo.current
        // use loginInfo.mutableValue to get and set the value
        // loginInfo.mutableValue = "new value" 
        // val value = loginInfo.mutableValue
        // it's a mutableStateOf, so you can use it as a state.
        
        LoginScreen()
        
        loginInfo.mutableValue // this will always return the last value you set anywhere in your tree
    }
    
}


@Composable
fun LoginScreen() {
    val loginInfo = LocalLoginInfo.current
    
    // after login success
    loginInfo.mutableValue = "the value you want to share between your screens" 
    
}
```

Supported types and their initial value are

``` javascript
@ProvidesLocalOf(type = String::clas) // default value -> ""
@ProvidesLocalOf(type = Int::clas) // default value -> 0
@ProvidesLocalOf(type = Boolean::clas) // default value -> false
@ProvidesLocalOf(type = Float::clas) // default value -> 0.0f
@ProvidesLocalOf(type = Double::clas) // default value -> 0.0
@ProvidesLocalOf(type = Long::clas) // default value -> 0L
@ProvidesLocalOf(type = Short::clas) // default value -> 0
@ProvidesLocalOf(type = Byte::clas) // default value -> 0
@ProvidesLocalOf(type = Char::clas) // default value -> '\\u0000'
@ProvidesLocalOf(type = Unit::clas) // default value -> Unit
@ProvidesLocalOf(type = Any::clas) // default value -> Any()
```

in case you needed to use `List::class`, `Map::class`, `Set::class` or any other generic type
you can use this approach instead.

```kotlin
data class YourDataClass(val yourValue: Map<String, Int> = emptyMap())
```

> [!IMPORTANT]
> Provide default values for all variables in the data class

##### Under the hood Generations

Generated code, when you use the `@ProvidesLocalOf` with `type` `String::class`

```kotlin
val LocalLoginInfo = compositionLocalOf { LoginInfoStateHolder() }

class LoginInfoStateHolder(string: String = "") : LoginInfo {
    var mutableValue: String by mutableStateOf(string)
}

@Composable
fun ProvideLocalScreenOnResult(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalScreenOnResult provides ScreenOnResultStateHolder()) {
        content()
    }
}
```

