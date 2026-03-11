package com.bellako.kiwi.common.data

object ScriptVariables {
    private val variables: Map<String, () -> String> =
        mapOf(
            "NAME" to { "kk" },
        )

    fun getValue(name: String): String = variables[name]?.invoke() ?: ""
}
