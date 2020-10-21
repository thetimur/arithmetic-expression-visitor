
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

    private fun getExpandResult(multiplyNode: MultiplyNode) : Node {
        if (multiplyNode.left is NumberNode && multiplyNode.right is NumberNode) {
            return multiplyNode
        } else if (multiplyNode.left is NumberNode && multiplyNode.right is PlusNode) {
            return PlusNode(
                    MultiplyNode(multiplyNode.left, multiplyNode.right.left),
                    MultiplyNode(multiplyNode.left, multiplyNode.right.right)
            )
        } else if (multiplyNode.left is NumberNode && multiplyNode.right is MultiplyNode) {
            return MultiplyNode(multiplyNode.left, multiplyNode.right)
        } else if (multiplyNode.right is NumberNode) {
            return  getExpandResult(MultiplyNode(multiplyNode.right, multiplyNode.left))
        } else if (multiplyNode.left is PlusNode && multiplyNode.right is PlusNode) {
            return PlusNode(
                    PlusNode(
                            MultiplyNode(multiplyNode.left.left, multiplyNode.right.left),
                            MultiplyNode(multiplyNode.left.left, multiplyNode.right.right)
                    ),
                    PlusNode(
                            MultiplyNode(multiplyNode.left.right, multiplyNode.right.left),
                            MultiplyNode(multiplyNode.left.right, multiplyNode.right.right)
                    )
            )
        } else if (multiplyNode.left is MultiplyNode && multiplyNode.right is PlusNode) {
            return PlusNode(
                    MultiplyNode(multiplyNode.left, multiplyNode.right.left),
                    MultiplyNode(multiplyNode.left, multiplyNode.right.right)
            )
        } else if (multiplyNode.right is PlusNode) {
            return  getExpandResult(MultiplyNode(multiplyNode.right, multiplyNode.left))
        }
        return getExpandResult(MultiplyNode(getExpandResult(multiplyNode.left as MultiplyNode), getExpandResult(multiplyNode.right as MultiplyNode)))
    }

    override fun visitMultiplyNode(multiplyNode: MultiplyNode): String {
        val result = getExpandResult(multiplyNode)
        if (result is PlusNode) {
            return visitPlusNode(result)
        }
        return "(${multiplyNode.left.accept(this)} * ${multiplyNode.right.accept(this)})"
    }
}

fun main() {
    val root = MultiplyNode(NumberNode(2), PlusNode(NumberNode(10), NumberNode(20)))
    println(root.accept(PrintVisitor()))
    println(root.accept(CalculateVisitor()))
    println(root.accept(ExpandVisitor()))
}