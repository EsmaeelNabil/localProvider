import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class ProvidesLocalOf(val type: KClass<*> = String::class)