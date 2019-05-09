package cn.edu.zjnu.acm.judge.generator

import cn.edu.zjnu.acm.judge.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.google.gson.Gson
import org.apache.ibatis.javassist.ClassPool
import org.apache.ibatis.javassist.CtMethod
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ClassUtils
import org.springframework.util.ReflectionUtils
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.method.HandlerMethod
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Function
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
class MockGenerator {

    @Autowired
    private val handlerMapping: RequestMappingHandlerMapping? = null

    @Test
    @Throws(IOException::class)
    fun test() {
        val gson = Gson()
        val handlerMethods = handlerMapping!!.handlerMethods
        val map: Map<Class<*>, List<Info>> = handlerMethods.entries
                .groupBy({ it.value.method.declaringClass }, { Info(it.value, it.key) })
        val sw = StringWriter()
        val out = PrintWriter(sw, true)
        for ((key, list) in map) {
            if (!accept(key, list)) {
                continue
            }
            val info2Method = Function { it: Info -> it.handlerMethod.method }
            val methodKey = Function { method: Method ->
                method.declaringClass.name.replace('.', '/') + "." + method.name +
                        ':' + org.springframework.asm.Type.getMethodDescriptor(method)
            }
            val comparator = Comparator.comparing<Info, Method>(info2Method,
                    Comparator.comparing<Method, String>(methodKey, toMethodComparator(key)))!!

            val testClass = TestClass(key,
                    "@AutoConfigureMockMvc",
                    "@RunWith(JUnitPlatform.class)",
                    "@Slf4j",
                    "@SpringBootTest(classes = " + MAIN_CLASS.simpleName + ".class)",
                    "@Transactional",
                    "@WebAppConfiguration")

            testClass.addImport(MAIN_CLASS)
            testClass.addImport(AutoConfigureMockMvc::class.java)
            testClass.addImport(JUnitPlatform::class.java)
            testClass.addImport(RunWith::class.java)
            testClass.addImport(SpringBootTest::class.java)
            testClass.addImport(Transactional::class.java)
            testClass.addImport(WebAppConfiguration::class.java)

            testClass.addImport(Autowired::class.java)
            testClass.addField(MockMvc::class.java, "mvc", "@Autowired")

            for (info in list.asSequence().sortedWith(comparator).toList()) {
                val requestMappingInfo = info.requestMappingInfo
                val requestMethods = requestMappingInfo.methodsCondition.methods
                val requestMethod = if (requestMethods.isEmpty()) "post" else requestMethods.iterator().next().toString().toLowerCase()
                val handlerMethod = info.handlerMethod
                val url = gson.toJson(requestMappingInfo.patternsCondition.patterns.iterator().next())
                generate(key, requestMappingInfo, handlerMethod, url, testClass, requestMethod)
            }
            testClass.write(out)
            val to = OUTPUT_DIR.resolve(key.name.replace(".", "/") + "Test.java")
            Files.createDirectories(to.parent)
            Files.write(to, sw.toString().replace("\t", "    ").toByteArray(StandardCharsets.UTF_8))
            sw.buffer.setLength(0)
        }
    }

    private fun toMethodComparator(key: Class<*>?): Comparator<String> {
        val counter = AtomicInteger()
        val classPool = ClassPool.getDefault()
        val seq = generateSequence(key, { it.superclass }).takeWhile { it.classLoader != null } + key!!.interfaces.asSequence()
        val map = seq
                .flatMap { type -> sequence(classPool, type) }
                .associateBy(
                        { method -> method.declaringClass.name.replace(".", "/") + "." + method.name + ":" + method.signature },
                        { counter.getAndIncrement() }
                )
        return Comparator.comparingInt { s -> map.getOrDefault(s, Integer.MAX_VALUE) }
    }

    private fun sequence(classPool: ClassPool, type: Class<*>): Sequence<CtMethod> {
        type.classLoader.getResourceAsStream(
                type.name.replace(".", "/") + ".class"
        )?.use { `is` ->
            return classPool.makeClass(`is`).declaredMethods
                    .asSequence().filter { method -> !Modifier.isStatic(method.modifiers) }
        }
        return sequenceOf()
    }

    private fun generate(key: Class<*>, requestMappingInfo: RequestMappingInfo, handlerMethod: HandlerMethod, url: String, testClass: TestClass, lowerMethod: String) {
        val method = handlerMethod.method
        val sw = StringWriter()
        val out = PrintWriter(sw)
        out.println("/**")
        out.println(""" * Test of ${method.name} method, of class ${key.simpleName}.""")
        for (type in method.parameterTypes) {
            testClass.addImport(type)
        }
        out.println(" *")
        out.println(""" * {@link ${key.simpleName}#${method.name}${method.parameterTypes.map { it.simpleName }.joinToString(", ", "(", ")}")}""")
        out.println(" */")
        testClass.addImport(Test::class.java)
        out.println("@Test")
        out.println("public void test" + StringUtils.capitalize(method.name) + "() throws Exception {")
        out.println("""	log.info("${method.name}");""")

        val variableDeclares = ArrayList<String>(4)
        val params = LinkedHashMap<String, Class<*>>(4)
        var body: String? = null
        var bodyType: Class<*>? = null
        val methodParameters = handlerMethod.methodParameters
        val parameters = method.parameters
        val files = ArrayList<String>(4)
        val pathVariables = ArrayList<String>(4)
        val headers = Maps.newTreeMap<String, String>()
        var locale: String? = null
        for (methodParameter in methodParameters) {
            val type = methodParameter.parameterType
            val typeName = type.simpleName
            var name = ""
            testClass.addImport(type)
            var unknown = false
            val requestParam = methodParameter.getParameterAnnotation(RequestParam::class.java)
            val pathVariable = methodParameter.getParameterAnnotation(PathVariable::class.java)
            val requestHeader = methodParameter.getParameterAnnotation(RequestHeader::class.java)
            if (requestParam != null) {
                name = requestParam.value
                if (name.isEmpty()) {
                    name = requestParam.name
                }
            } else if (pathVariable != null) {
                name = pathVariable.value
                if (name.isEmpty()) {
                    name = pathVariable.name
                }
                if (name.isEmpty()) {
                    name = parameters[methodParameter.parameterIndex].name
                }
                pathVariables.add(name)
                variableDeclares.add("\t" + typeName + " " + name + " = " + getDefaultValue(type) + ";")
                continue
            } else if (methodParameter.hasParameterAnnotation(RequestBody::class.java)) {
                body = "request"
                bodyType = type
                variableDeclares.add("\t" + typeName + " request = " + getDefaultValue(type) + ";")
                continue
            } else if (requestHeader != null) {
                name = requestHeader.value
                if (name.isEmpty()) {
                    name = requestHeader.name
                }
                if (name.isEmpty()) {
                    name = parameters[methodParameter.parameterIndex].name
                }
                val camelCase = camelCase(name)
                headers[name] = camelCase
                variableDeclares.add("\t$typeName $camelCase = ${getDefaultValue(type)};")
                continue
            } else if (HttpServletResponse::class.java == type || HttpServletRequest::class.java == type) {
                continue
            } else if (Locale::class.java == type) {
                locale = "locale"
                variableDeclares.add("\t$typeName $locale = Locale.getDefault();")
                continue
            } else {
                unknown = true
            }
            if (name.isEmpty()) {
                name = parameters[methodParameter.parameterIndex].name
            }
            if (unknown && type.classLoader != null && type != MultipartFile::class.java) {
                ReflectionUtils.doWithFields(type, { field -> process(field.name, camelCase(field.name), field.type, params, files, variableDeclares, testClass, method, lowerMethod) },
                        { field -> !Modifier.isStatic(field.modifiers) })
                continue
            } else if (unknown) {
                System.err.println("param ${methodParameter.parameterIndex} with type $typeName in $method has no annotation")
            }
            process(name, camelCase(name), type, params, files, variableDeclares, testClass, method, lowerMethod)
        }
        for (variableDeclare in variableDeclares) {
            out.println(variableDeclare)
        }
        testClass.addImport(MvcResult::class.java)
        if (files.isEmpty()) {
            testClass.addStaticImport(MockMvcRequestBuilders::class.java, lowerMethod)
            out.print("\tMvcResult result = mvc.perform($lowerMethod($url")
            for (pathVariable in pathVariables) {
                out.print(", $pathVariable")
            }
            out.print(")")
        } else {
            val methodName = if (!ClassUtils.hasMethod(MockMvcRequestBuilders::class.java, "multipart", String::class.java, Array<String>::class.java)) {
                "fileUpload"
            } else "multipart"
            testClass.addStaticImport(MockMvcRequestBuilders::class.java, methodName)
            out.print("\tMvcResult result = mvc.perform($methodName($url")
            for (pathVariable in pathVariables) {
                out.print(", $pathVariable")
            }
            out.print(")")
            for (file in files) {
                out.print(".file($file)")
            }
        }

        val newLine = params.size >= 2
        for ((paramName, paramType) in params) {
            val variableName = camelCase(paramName)
            val value = when {
                paramType.isPrimitive -> "${com.google.common.primitives.Primitives.wrap(paramType).getSimpleName()}.toString($variableName)"
                paramType == String::class.java -> variableName
                else -> "$variableName?.toString() ?: \"\""
            }
            if (newLine) {
                out.println()
                out.print("\t\t\t")
            }
            out.print(".param(\"$paramName\", $value)")
        }

        for ((key1, value) in headers) {
            out.println()
            out.print("\t\t\t.header(\"$key1\", $value)")
        }

        if (locale != null) {
            out.println()
            out.print("\t\t\t.locale($locale)")
        }

        when (lowerMethod) {
            "get", "delete" -> {
                if (body != null) {
                    System.err.println("RequestBody annotation found on $method with request method $lowerMethod")
                }
                if (!requestMappingInfo.consumesCondition.isEmpty) {
                    System.err.println("request consumes " + requestMappingInfo.consumesCondition + " found on " + method)
                }
            }
        }
        if (body != null) {
            out.println()
            if (bodyType == String::class.java || bodyType == ByteArray::class.java) {
                out.print("\t\t\t.content($body)")
            } else {
                testClass.addField(ObjectMapper::class.java, "objectMapper", "@Autowired")
                out.print("\t\t\t.content(objectMapper.writeValueAsString($body))")
            }
            testClass.addImport(MediaType::class.java)
            out.print(".contentType(MediaType.APPLICATION_JSON)")
        }
        testClass.addStaticImport(MockMvcResultMatchers::class.java, "status")
        out.println(")")
        out.println("\t\t\t.andExpect(status().isOk())")
        out.println("\t\t\t.andReturn();")
        out.println("}")
        testClass.addMethod(sw.toString())
    }

    private fun camelCase(name: String): String {
        var tmp = name.replace("[^a-zA-Z]+([a-zA-Z])".toRegex()) { it.groupValues[1].toUpperCase() }
        tmp = if (tmp.isEmpty()) "" else Character.toLowerCase(tmp.get(0)) + tmp.substring(1)
        return if ("referer" == tmp) "referrer" else tmp
    }

    private fun process(paramName: String, variableName: String, type: Class<*>, requestParams: MutableMap<String, Class<*>>, files: MutableList<String>, variableDeclares: MutableList<String>, testClass: TestClass, method: Method, lowerMethod: String) {
        if (type == MultipartFile::class.java) {
            val msg = "upload a multipart file, but request method is %s, %s"
            assertThat(lowerMethod).withFailMessage(msg, lowerMethod, method)
                    .isEqualTo("post")

            testClass.addImport(MockMultipartFile::class.java)
            variableDeclares.add("\tbyte[] ${variableName}Content = null;")
            variableDeclares.add("\tMockMultipartFile $variableName = new MockMultipartFile(\"$paramName\", ${variableName}Content);")
            files.add(variableName)
        } else {
            requestParams[paramName] = type
            testClass.addImport(type)
            variableDeclares.add("\t${type.simpleName} $variableName = ${getDefaultValue(type)};")
        }
    }

    private class Info internal constructor(internal val handlerMethod: HandlerMethod, internal val requestMappingInfo: RequestMappingInfo)

    companion object {

        private val DEFAULT_VALUES = ImmutableMap.builder<Class<*>, String>()
                .put(Boolean::class.javaPrimitiveType!!, "false")
                .put(Byte::class.javaPrimitiveType!!, "0")
                .put(Char::class.javaPrimitiveType!!, "0")
                .put(Short::class.javaPrimitiveType!!, "0")
                .put(Int::class.javaPrimitiveType!!, "0")
                .put(Long::class.javaPrimitiveType!!, "0")
                .put(Float::class.javaPrimitiveType!!, "0")
                .put(Double::class.javaPrimitiveType!!, "0")
                .put(String::class.java, "\"\"")
                .build()
        private val MAIN_CLASS = Application::class.java
        private val OUTPUT_DIR = Paths.get("target/mock")

        private fun accept(key: Class<*>, list: List<*>): Boolean {
            return key.getPackage().name.startsWith(MAIN_CLASS.getPackage().name) && key.enclosingClass == null && list.isNotEmpty()
        }

        private fun getDefaultValue(type: Class<*>): String {
            return DEFAULT_VALUES[type] ?: "null"
        }
    }

}
