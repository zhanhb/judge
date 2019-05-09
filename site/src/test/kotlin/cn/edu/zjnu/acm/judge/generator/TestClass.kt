package cn.edu.zjnu.acm.judge.generator

import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import java.io.BufferedReader
import java.io.PrintWriter
import java.io.StringReader
import java.util.*
import kotlin.streams.toList

class TestClass(_for: Class<*>, superclass: Class<*>, vararg annotations: String) {

    private val _package: String
    private val superclass: String?
    private val imports = TreeSet<String>()
    private val className = _for.simpleName + "Test"
    private val classAnnotations: List<String>
    private val fields = LinkedHashMap<String, Field>(4)
    private val methods = ArrayList<Method>(4)

    constructor(_for: Class<*>, vararg annotations: String) : this(_for, Any::class.java, *annotations)

    init {
        this.superclass = getSuperclass(superclass)
        this._package = _for.getPackage().name
        this.classAnnotations = ImmutableList.copyOf(annotations)
    }

    private fun getSuperclass(superclass: Class<*>?): String? {
        return if (superclass == null || superclass == Any::class.java) {
            null
        } else if (superclass.isPrimitive) {
            throw IllegalArgumentException()
        } else {
            addImport(superclass)
            superclass.simpleName
        }
    }

    fun addImport(className: String) {
        imports.add("import $className;")
    }

    fun addImport(_import: Class<*>) {
        val aPackage = _import.getPackage()
        if (aPackage == null || aPackage.name == "java.lang") {
            return
        }
        addImport(_import.canonicalName)
    }

    fun addStaticImport(targetClass: Class<*>, field: String) {
        imports.add("import static " + targetClass.canonicalName + "." + field + ";")
    }

    fun write(out: PrintWriter) {
        out.println("package $_package;")
        out.println()
        for (aImport in imports) {
            out.println(aImport)
        }
        out.println()
        for (classAnnotation in classAnnotations) {
            out.println(classAnnotation)
        }
        out.print("public class $className ")
        if (superclass != null) {
            out.print("extends $superclass ")
        }
        out.println("{")
        out.println()
        for (field in fields.values) {
            field.write(out, 1)
        }
        out.println()
        for (method in methods) {
            method.write(out, 1)
            out.println()
        }
        out.println("}")
    }

    fun addField(type: Class<*>, name: String, vararg annotations: String) {
        addImport(type)
        fields[name] = Field(type.simpleName, name, ImmutableList.copyOf(annotations))
    }

    fun addMethod(methodContent: String) {
        methods.add(Method(BufferedReader(StringReader(methodContent)).lines().toList()))
    }

    private class Field internal constructor(private val typeName: String, private val variableName: String, private val annotations: List<String>) {

        internal fun write(out: PrintWriter, indent: Int) {
            val prefix = Strings.repeat("\t", indent)
            for (annotation in annotations) {
                out.println(prefix + annotation)
            }
            out.println(prefix + "private " + typeName + " " + variableName + ";")
        }

    }

    private class Method internal constructor(private val lines: List<String>) {

        internal fun write(out: PrintWriter, indent: Int) {
            val prefix = Strings.repeat("\t", indent)
            for (line in lines) {
                if (line.isEmpty()) {
                    out.println()
                } else {
                    out.println(prefix + line)
                }
            }
        }

    }

}
