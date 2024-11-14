package main.java.com.github.mannizcob;

import main.java.com.github.mannizcob.AstNodes.AstNode;
import main.java.com.github.mannizcob.AstNodes.AstNodeEvaluator;
import main.java.com.github.mannizcob.Parsers.Parser;
import main.java.com.github.mannizcob.Parsers.Parsers;
import main.java.com.github.mannizcob.Results.Result;

public class AstCalculator {

    public static void main(String[] args) {
        Parsers parsers = new Parsers();
        Parser<AstNode> parser = parsers.factorParser();

        String input = "sen(54)";
        Result<AstNode> result = parser.parse(input);

        if (result instanceof Result.Ok<AstNode> okResult) {
            AstNode tree = okResult.getValue(); 
            System.out.println("Expression: " + tree.toString());

            AstNodeEvaluator evaluator = new AstNodeEvaluator();
            double value = evaluator.evaluate(tree);
            System.out.println("Result: " + value);
        } 

        if (result instanceof Result.Err<AstNode> errResult) {
            System.out.println("Error parsing expression: " + errResult.getErrorMessage());
        }
    }

}
