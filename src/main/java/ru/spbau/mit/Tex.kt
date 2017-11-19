package ru.spbau.mit

@DslMarker
annotation class TexElementMarker

@TexElementMarker
abstract class TexElement(open protected val options: List<String> = emptyList(),
                          open protected val arguments: List<String> = emptyList()) {
    companion object {
        val INDENT = " ".repeat(4)
    }

    abstract fun render(builder: StringBuilder, indent: String)

    fun StringBuilder.appendOptions() {
        if (options.isNotEmpty()) {
            append(options.joinToString(",", "[", "]"))
        }
    }

    fun StringBuilder.appendArguments() {
        if (arguments.isNotEmpty()) {
            append(arguments.joinToString("}{", "{", "}"))
        }
    }

    fun StringBuilder.newLine() {
        append("\n")
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }
}

class Text(private val text: String): TexElement(){
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text")
        builder.newLine()
    }
}

abstract class Tag(open val name: String,
                   override val options: List<String> = emptyList(),
                   override val arguments: List<String> = emptyList()): TexElement() {
    val children = mutableListOf<TexElement>()

    protected fun <T : TexElement> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\begin{$name}")
        if (options.isNotEmpty()) {
            builder.appendOptions()
        }
        builder.newLine()
        for (child in children) {
            child.render(builder, indent + INDENT)
        }
        builder.append("$indent\\end{$name}")
        builder.newLine()
    }
}

abstract class Command(override val name: String,
                       override val options: List<String> = emptyList(),
                       override val arguments: List<String> = emptyList()):
        Tag(name, options, arguments) {

    operator fun String.unaryPlus() {
        children.add(Text(this))
    }

    fun math(formula: String) = initTag(Math(formula), {})

    fun framed(init: Framed.() -> Unit) = initTag(Framed(), init)

    fun flushLeft(init: FlushLeft.() -> Unit) = initTag(FlushLeft(), init)

    fun center(init: Center.() -> Unit) = initTag(Center(), init)

    fun flushRight(init: FlushRight.() -> Unit) = initTag(FlushRight(), init)

    fun itemize(options: List<String> = emptyList(), init: Itemize.() -> Unit) =
            initTag(Itemize(options), init)

    fun enumerate(options: List<String> = emptyList(), init: Enumerate.() -> Unit) =
            initTag(Enumerate(options), init)

    fun customMulti(name: String, options: List<String> = emptyList(),
                    init: CustomMultiLineCommand.() -> Unit) =
            initTag(CustomMultiLineCommand(name, options), init)

    fun customSingle(name: String, options: List<String> = emptyList(),
                     init: CustomSingleLineCommand.() -> Unit = {}) =
            initTag(CustomSingleLineCommand(name, options), init)

    fun customSingleWithEffect(name: String, options: List<String> = emptyList(),
                               init: CustomSingleLineCommandWithEffect.() -> Unit = {}) =
            initTag(CustomSingleLineCommandWithEffect(name, options), init)

}

abstract class SingleLineCommand(override val name: String,
                                 override val arguments: List<String>,
                                 override val options: List<String> = emptyList()):
        Command(name, arguments, options) {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\$name")
        if (options.isNotEmpty()) {
            builder.appendOptions()
        }
        if (arguments.isNotEmpty()) {
            builder.appendArguments()
        }
        builder.newLine()
    }
}

class CustomSingleLineCommand(name: String, arguments: List<String>,
                              options: List<String> = emptyList()):
        SingleLineCommand(name, arguments, options)

class Math(private val formula: String) : SingleLineCommand("math", listOf(formula)) {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$$formula$")
        builder.newLine()
    }
}

class UsePackage(packageName: String, override val options: List<String> = emptyList()):
        SingleLineCommand("usepackage", listOf(packageName), options)

class DocumentClass(documentClassName: String,
                    override val options: List<String> = emptyList()):
        SingleLineCommand("documentclass", listOf(documentClassName), options)

abstract class SingleLineCommandWithEffect(override val name: String,
                                           override val options: List<String> = emptyList()):
        Command(name, options) {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\$name")
        if (options.isNotEmpty()) {
            builder.appendOptions()
        }
        builder.newLine()
        for (child in children) {
            child.render(builder, indent + INDENT)
        }
    }
}

class CustomSingleLineCommandWithEffect(name: String, options: List<String> = emptyList()):
        SingleLineCommandWithEffect(name, options)

class Item(override val options: List<String> = emptyList()):
        SingleLineCommandWithEffect("item", options)

abstract class MultiLineCommand(override val name: String,
                                override val options: List<String> = emptyList()):
        Command(name, options)

class Framed(override val options: List<String> = emptyList()): MultiLineCommand("framed", options)

class FlushLeft: MultiLineCommand("flushleft")

class Center: MultiLineCommand("center")

class FlushRight: MultiLineCommand("flushright")

class CustomMultiLineCommand(name: String, options: List<String> = emptyList()):
        MultiLineCommand(name, options)

class Itemize(override val options: List<String> = emptyList()):
        Tag("itemize", options) {
    fun item(options: List<String> = emptyList(), init: Item.() -> Unit) =
            initTag(Item(options), init)

}

class Enumerate(override val options: List<String> = emptyList()):
        Tag("enumerate", options) {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Document: MultiLineCommand("document") {
    private val usePackages = mutableListOf<UsePackage>()
    private var documentClass: DocumentClass? = null

    fun usePackage(packageName: String, options: List<String> = emptyList(),
                   init: UsePackage.() -> Unit = {}) {
        val usePackage = UsePackage(packageName, options)
        usePackage.init()
        usePackages.add(usePackage)
    }

    fun documentClass(documentClassName: String, options: List<String> = emptyList(),
                      init: DocumentClass.() -> Unit = {}) {
        val documentClass = DocumentClass(documentClassName, options)
        documentClass.init()
        this.documentClass = documentClass
    }

    override fun render(builder: StringBuilder, indent: String) {
        if (documentClass != null) {
            documentClass!!.render(builder, "")
        }
        usePackages.forEach { it.render(builder, "")}
        super.render(builder, indent)
    }
}

fun document(init: Document.() -> Unit): Document {
    val doc = Document()
    doc.init()
    return doc
}