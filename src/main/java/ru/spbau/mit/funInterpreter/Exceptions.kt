package ru.spbau.mit.funInterpreter

open class InterpretationException(message: String) : Exception(message)

class WrongArgumentsNumberException(name: String, actual: Int, expected: Int) :
        InterpretationException("Wrong arguments number at function `$name`: "
                + " actual = $actual, " + "expected = $expected.")

class RedefinitionException(name: String) : InterpretationException("Redefinition of `$name`")

class NotFoundException(name: String, type: Type
) : InterpretationException("Not found: `$name` ${type.stringValue}") {
    companion object {
        enum class Type(val stringValue: String) {
            FUNCTION("function"),
            VARIABLE("variable");
        }
    }
}
