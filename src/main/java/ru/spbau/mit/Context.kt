package ru.spbau.mit

import java.util.*

class Context(
        private val builtInVariableNames: Set<Identifier> = BUILT_IN_VARIABLE_NAMES,
        private val builtInFunctionNames: Set<Identifier> = BUILT_IN_FUNCTION_NAMES,
        private val scopes: ArrayDeque<Scope> = ArrayDeque()
) {
    companion object {
        val BUILT_IN_VARIABLE_NAMES: Set<Identifier> = setOf()
        val BUILT_IN_FUNCTION_NAMES = setOf(Identifier("println"))

    }

    data class Scope(val variables: MutableMap<Identifier, Int?> = mutableMapOf(),
                     val functions: MutableMap<Identifier, Function> = mutableMapOf())

    fun enterScope() {
        scopes.add(Scope())
    }

    fun leaveScope() {
        scopes.removeLast()
    }

    fun assignVariable(name: Identifier, value: Int) {
        val scope = getVariableInformation(name).scope
        scope.variables.put(name, value)
    }

    fun addVariable(name: Identifier, value: Int?) {
        val currentScope = scopes.peekLast()
        if (builtInVariableNames.contains(name)
                || currentScope.variables.containsKey(name))
            throw Exception()
        currentScope.variables.put(name, value)
    }

    fun addFunction(function: Function) {
        val currentScope = scopes.peekLast()
        if (builtInFunctionNames.contains(function.name)
                || currentScope.functions.containsKey(function.name))
            throw Exception()
        currentScope.functions.put(function.name, function)
    }

    data class VariableInformation(val name: Identifier, val value: Int?, val scope: Scope)
    private fun getVariableInformation(name: Identifier):  VariableInformation {
        val scopesIterator = scopes.descendingIterator()
        while (scopesIterator.hasNext())
        {
            val currentScope = scopesIterator.next()
            val variables = currentScope.variables
            if (variables.containsKey(name)) {
                return VariableInformation(name, variables.getValue(name), currentScope)
            }
        }
        throw Exception()
    }

    fun getVariableValue(name: Identifier): Int? = getVariableInformation(name).value

    fun getFunction(name: Identifier): Function {
        val scopesIterator = scopes.descendingIterator()
        while (scopesIterator.hasNext())
        {
            val currentScope = scopesIterator.next()
            val functions = currentScope.functions
            if (functions.containsKey(name)) {
                return currentScope.functions.getValue(name)
            }
        }
        throw Exception()
    }


}
