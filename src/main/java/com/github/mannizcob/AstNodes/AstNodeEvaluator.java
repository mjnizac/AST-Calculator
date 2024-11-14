package main.java.com.github.mannizcob.AstNodes;

public class AstNodeEvaluator {

    public double evaluate(AstNode node) {

        // Literal node
        if (node instanceof AstLiteralNode) {
            return ((AstLiteralNode) node).value;
        }

        // Function node
        if (node instanceof AstFunctionNode funcNode) {
            String operator = funcNode.function;

            // Evaluate the arguments
            double[] args = funcNode.arguments.stream()
                                      .mapToDouble(this::evaluate)
                                      .toArray();

            // Binary operators
            switch (operator) {
                case "+" -> { return args[0] + args[1]; }
                case "-" -> { return args[0] - args[1]; }
                case "*" -> { return args[0] * args[1]; }
                case "/" -> { return args[0] / args[1]; }
                case "%" -> { return args[0] % args[1]; }
            }

            // Unary operators
            switch (operator) {
                case "sen" -> { return Math.sin(Math.toRadians(args[0])); }
                case "cos" -> { return Math.cos(Math.toRadians(args[0])); }
                case "tang" -> { return Math.tan(Math.toRadians(args[0])); }
                case "arcsen" -> { return Math.toDegrees(Math.asin(args[0])); }
                case "arccos" -> { return Math.toDegrees(Math.acos(args[0])); }
                case "arctang" -> { return Math.toDegrees(Math.atan(args[0])); }
            }

            throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }

        throw new IllegalArgumentException("Unknown node type: " + node.getClass());
    }
    
}
