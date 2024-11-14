package main.java.com.github.mannizcob.AstNodes;

public class AstLiteralNode extends AstNode { // Node for literals, in this case, numbers (e.g. 123.45)
    
    final double value;

    public AstLiteralNode(double value) {
        this.value = value;
    }
    
}
