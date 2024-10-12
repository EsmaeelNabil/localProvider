import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType

class LocalProviderProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(ProvidesLocalOf::class.simpleName!!)
        symbols.forEach { symbol ->
            if (symbol is KSClassDeclaration && symbol.isValid()) {
                processClass(symbol)
            }
        }
        return emptyList()
    }

    private fun KSClassDeclaration.isValid(): Boolean {
        return this.annotations.any { it.shortName.asString() == ProvidesLocalOf::class.simpleName }
    }

    private fun processClass(clazz: KSClassDeclaration) {

        val implementationClassName = "${clazz}StateHolder"

        // Create the local composition
        generateLocalComposition(clazz, implementationClassName)

        // Create the val code
        generatedDelegate(clazz, implementationClassName)

        generateProviderComposable(clazz, implementationClassName)

    }

    private fun generateProviderComposable(
        clazz: KSClassDeclaration,
        implementationClassName: String
    ) {
        val clazzName = clazz.simpleName.asString()
        val packageName = clazz.packageName.asString()

        val code = """
            import androidx.compose.runtime.Composable
            import Local$clazzName
            import $implementationClassName
            import androidx.compose.runtime.CompositionLocalProvider
            
            @Composable
            fun ProvideLocal${clazz.simpleName.asString()}(content: @Composable () -> Unit) {
                CompositionLocalProvider(Local${clazz.simpleName.asString()} provides $implementationClassName()) {
                    content()
                }
            }
        """.trimIndent()

        codeGenerator.createNewFile(
            dependencies = Dependencies(false, clazz.containingFile!!),
            packageName = packageName,
            fileName = "Local${clazzName}Provider"
        ).use { outputStream ->
            outputStream.writer().use { writer ->
                writer.write(code)
            }
        }
    }

    private fun generateLocalComposition(
        clazz: KSClassDeclaration,
        implementationClassName: String
    ) {
        val clazzName = clazz.simpleName.asString()
        val packageName = clazz.packageName.asString()

        val localComposition = """
                import androidx.compose.runtime.compositionLocalOf
                import $packageName.$clazzName
    
                val Local$clazzName = compositionLocalOf { $implementationClassName() }
                """.trimIndent()

        // Code generation logic, e.g., output to a new Kotlin file
        codeGenerator.createNewFile(
            dependencies = Dependencies(false, clazz.containingFile!!),
            packageName = packageName,
            fileName = "Local$clazzName"
        ).use { outputStream ->
            outputStream.writer().use { writer ->
                writer.write(localComposition)
            }
        }
    }

    private fun generatedDelegate(
        clazz: KSClassDeclaration,
        implementationClassName: String
    ) {
        val clazzName = clazz.simpleName.asString()
        val packageName = clazz.packageName.asString()

        val annotation = clazz.annotations.find {
            it.shortName.asString() == ProvidesLocalOf::class.simpleName
        }

        val stateArgument = annotation?.arguments?.find { it.name?.asString() == "type" }
        val stateArgType = stateArgument!!.value as KSType
        val defaultArgValue = stateArgType.getDefaultValue()
        val argName = stateArgType.toString().lowercase()

        val parentInterfaceImport = "import $packageName.$clazzName"
        val argumentTypeImport =
            "import ${stateArgType.declaration.packageName.asString()}.$stateArgType"

        val code = """
            import androidx.compose.runtime.getValue
            import androidx.compose.runtime.mutableStateOf
            import androidx.compose.runtime.setValue
            $parentInterfaceImport
            $argumentTypeImport

            class $implementationClassName($argName: $stateArgType = $defaultArgValue) : $clazzName {
                var mutableValue: $stateArgType by mutableStateOf($argName)
            }
            
            """.trimIndent()

        codeGenerator.createNewFile(
            dependencies = Dependencies(false, clazz.containingFile!!),
            packageName = packageName,
            fileName = implementationClassName
        ).use { outputStream ->
            outputStream.writer().use { writer ->
                writer.write(code)
            }
        }
    }

}

fun KSType.getDefaultValue(): String = when (this.toString()) {
    "String" -> "\"\""
    "Int" -> "0"
    "Boolean" -> "false"
    "Float" -> "0.0f"
    "Double" -> "0.0"
    "Long" -> "0L"
    "Short" -> "0"
    "Byte" -> "0"
    "Char" -> "'\\u0000'"
    "Unit" -> "Unit"
    "Any" -> "Any()"

    else -> {
        if (this.toString().contains("<")) {
            error(
                """
            | 
            | -------------------------------------------------------------------------------
            || - Type $this is not supported.                                                
            || - Use a data class with default values instead:                               |
            ||                                                                               |           
            ||  data class SomeDataClass(val YourValue: $this = defaultValue)               
            ||  
            ||  @GenerateLocalComposite(type = SomeDataClass::class)
            ||  interface What_You_Want_To_Generate_A_Stateful_shared_Implementation_For
            |                                                                                 |
            | --------------------------------------------------------------------------------
            |
        """.trimMargin()
            )
        } else {
            // a class with default constructor values.
            "${this}()"
        }
    }
}