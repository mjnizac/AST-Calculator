package main.java.com.github.mannizcob.AstNodes;

import java.util.List;

public class AstFunctionNode extends AstNode { // Node for functions with a function name and an argument list (e.g. sen(45))
    
    final String function; // The function name, e.g., "sen", "cos", "tang", "arcsen", "arccos", "arctang", "ln", "log", "sqrt", "abs", "exp"
    final List<AstNode> arguments; 

    public AstFunctionNode(String function, List<AstNode> arguments) {
        this.function = function;
        this.arguments = arguments;
    }
    
}
