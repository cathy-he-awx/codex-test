package com.example.abac

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.nio.file.Files
import kotlin.io.path.createTempFile

/**
 * Access control system that delegates policy evaluation to OPA.
 * The class invokes the `opa` CLI to evaluate a Rego policy.
 */
class OpaAccessControl(
    private val policyFile: String,
    private val dataFile: String? = null
) {
    private val mapper = jacksonObjectMapper()

    fun isAccessAllowed(request: AccessRequest): Boolean {
        val inputFile = createTempFile(prefix = "input", suffix = ".json").toFile()
        mapper.writeValue(inputFile, request)

        val args = mutableListOf(
            "opa", "eval", "-f", "json", "-i", inputFile.absolutePath,
            "-d", policyFile
        )
        if (dataFile != null) {
            args.addAll(listOf("-d", dataFile))
        }
        args.add("data.access.allow")

        val process = ProcessBuilder(args)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()
        inputFile.delete()

        val json = mapper.readTree(output)
        val result = json["result"]
        if (result != null && result.isArray && result.size() > 0) {
            val expressions = result[0]["expressions"]
            if (expressions != null && expressions.isArray && expressions.size() > 0) {
                return expressions[0]["value"].asBoolean()
            }
        }
        return false
    }
}
