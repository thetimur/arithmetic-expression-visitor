
interface Node {
    fun <R> accept(visitor: Visitor<R>) : R
}

class NumberNode(var number: Int) : Node {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitNumberNode(this)
    }

    override fun toString(): String {
        return this.number.toString()
    }
}

class PlusNode(val left: Node, val right: Node) : Node {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitPlusNode(this)
    }
}

class MultiplyNode(val left: Node, val right: Node) : Node {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitMultiplyNode(this)
    }
}

interface Visitor<R> {
    fun visitNumberNode(numberNode: NumberNode) : R
    fun visitPlusNode(plusNode: PlusNode) : R
    fun visitMultiplyNode(multiplyNode: MultiplyNode): R
}

class PrintVisitor: Visitor<String> {
    override fun visitNumberNode(numberNode: NumberNode) : String {
        return numberNode.toString()
    }

    override fun visitPlusNode(plusNode: PlusNode) : String {
        return "(${plusNode.left.accept(this)} + ${plusNode.right.accept(this)})"
    }

    override fun visitMultiplyNode(multiplyNode: MultiplyNode): String {
        return "(${multiplyNode.left.accept(this)} * ${multiplyNode.right.accept(this)})"
    }

}

class CalculateVisitor: Visitor<Int> {
    override fun visitNumberNode(numberNode: NumberNode): Int {
        return numberNode.number
    }

    override fun visitPlusNode(plusNode: PlusNode): Int {
        return plusNode.left.accept(this) + plusNode.right.accept(this)
    }

    override fun visitMultiplyNode(multiplyNode: MultiplyNode): Int {
        return multiplyNode.left.accept(this) * multiplyNode.right.accept(this)
    }
}

class ExpandVisitor: Visitor<String> {
    override fun visitNumberNode(numberNode: NumberNode): String {
        return numberNode.toString()
    }

    override fun visitPlusNode(plusNode: PlusNode): String {
        return "(${plusNode.left.accept(this)} + ${plusNode.right.accept(this)})"
    }

    override fun visitMultiplyNode(multiplyNode: MultiplyNode): String {
        val result = PlusNode(multiplyNode.left, multiplyNode.left)
        return visitPlusNode(result)
    }
}

fun main() {
    val root = PlusNode(NumberNode(20), MultiplyNode(NumberNode(2), NumberNode(10)))
    println(root.accept(PrintVisitor()))
    println(root.accept(CalculateVisitor()))
    println(root.accept(ExpandVisitor()))
}