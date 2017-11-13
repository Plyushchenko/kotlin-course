package ru.spbau.mit.funInterpreter

import java.util.*

class Context(
        private val builtInVariableNames: Set<Identifier> = BUILT_IN_VARIABLE_NAMES,
        private val builtInFunctionNames: Set<Identifier> = BUILT_IN_FUNCTION_NAMES,
        private val scopes: ArrayDeque<Scope> = ArrayDeque()
) {
    companion object {
        val BUILT_IN_VARIABLE_NAMES: Set<Identifier> = emptySet()
        val BUILT_IN_FUNCTION_NAMES = setOf(Identifier("println"))
    }

    data class Scope(val variables: MutableMap<Identifier, Int?> = mutableMapOf(),
                     val functions: MutableMap<Identifier, Function> = mutableMapOf()
    )

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
        val variables = currentScope.variables
        if (builtInVariableNames.contains(name) || variables.containsKey(name))
            throw RedefinitionException(name.name)
        variables.put(name, value)
    }

    fun addFunction(function: Function) {
        val currentScope = scopes.peekLast()
        val name = function.name
        val functions = currentScope.functions
        if (builtInFunctionNames.contains(name) || functions.containsKey(name))
            throw RedefinitionException(name.name)
        functions.put(name, function)
    }

    data class VariableInformation(val name: Identifier, val value: Int?, val scope: Scope)
    private fun getVariableInformation(name: Identifier): VariableInformation {
        val scopesIterator = scopes.descendingIterator()
        while (scopesIterator.hasNext())
        {
            val currentScope = scopesIterator.next()
            val variables = currentScope.variables
            if (variables.containsKey(name)) {
                return VariableInformation(name, variables.getValue(name), currentScope)
            }
        }
        throw NotFoundException(name.name)
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
        throw NotFoundException(name.name)
    }
}
