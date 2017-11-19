package ru.spbau.mit

@DslMarker
annotation class TexElementMarker

@TexElementMarker
abstract class TexElement(open protected val options: List<String> = emptyList(),
                          open protected val arguments: List<String> = emptyList()) {
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

abstract class Tag(override val options: List<String> = emptyList(),
                   override val arguments: List<String> = emptyList()): TexElement() {
    val children = mutableListOf<TexElement>()

    operator fun String.unaryPlus() {
        children.add(Text(this))
    }

    protected fun <T : TexElement> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    fun math(formula: String, init: Math.() -> Unit = {}) = initTag(Math(formula), init)
}

/*
\name[options]{arguments}
 */
abstract class SingleLineCommand(private val name: String,
                                 override val arguments: List<String>,
                                 override val options: List<String> = emptyList()): Tag() {
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

/*
\name[options]
    something
*/
abstract class SingleLineCommandWithEffect(private val name: String,
                                           override val options: List<String> = emptyList()):
        Tag() {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\$name")
        if (options.isNotEmpty()) {
            builder.appendOptions()
        }
        builder.newLine()
        for (child in children) {
            child.render(builder, indent + " ".repeat(4))
        }
    }
}

class Item(override val options: List<String> = emptyList()):
        SingleLineCommandWithEffect("item", options)

/*
\begin[options]{name}
.
.
\end{name}
 */
abstract class MultiLineCommand(open protected val name: String,
                                override val options: List<String> = emptyList()): Tag() {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\begin{$name}")
        if (options.isNotEmpty()) {
            builder.appendOptions()
        }
        builder.newLine()
        for (child in children) {
            child.render(builder, indent + " ".repeat(4))
        }
        builder.append("$indent\\end{$name}")
        builder.newLine()

    }

    fun framed(init: Framed.() -> Unit) = initTag(Framed(), init)

    fun flushLeft(init: FlushLeft.() -> Unit) = initTag(FlushLeft(), init)

    fun center(init: Center.() -> Unit) = initTag(Center(), init)

    fun flushRight(init: FlushRight.() -> Unit) = initTag(FlushRight(), init)

    fun itemize(options: List<String> = emptyList(), init: Itemize.() -> Unit) =
            initTag(Itemize(options), init)

    fun enumerate(options: List<String> = emptyList(), init: Enumerate.() -> Unit) =
            initTag(Enumerate(options), init)

}

class Framed(override val options: List<String> = emptyList()): MultiLineCommand("framed", options)

class FlushLeft: MultiLineCommand("flushleft")

class Center: MultiLineCommand("center")

class FlushRight: MultiLineCommand("flushright")

/*
\begin[options]{name}
.
\item[item.options]
    something
 */
class Itemize(override val options: List<String> = emptyList()):
        MultiLineCommand("itemize", options) {
    fun item(options: List<String> = emptyList(), init: Item.() -> Unit) =
            initTag(Item(options), init)

}

class Enumerate(override val options: List<String> = emptyList()):
        MultiLineCommand("enumerate", options) {
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