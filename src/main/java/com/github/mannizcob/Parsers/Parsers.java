package main.java.com.github.mannizcob.Parsers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.com.github.mannizcob.AstNodes.AstFunctionNode;
import main.java.com.github.mannizcob.AstNodes.AstLiteralNode;
import main.java.com.github.mannizcob.AstNodes.AstNode;
import main.java.com.github.mannizcob.Results.Result;

public class Parsers {

    // Check if one of the parsers given as arguments succeeds
    @SafeVarargs
    public static <T> Parser<T> oneOf(Parser<T>... parsers) {
        return input -> {
            for (Parser<T> parser : parsers) {
                Result<T> result = parser.parse(input);
                if (result instanceof Result.Ok) { return result; }
            }
            return new Result.Err<>("No parser matched in oneOf.");
        };
    }

    // Create a parser with a literal string
    public Parser<String> literal(String literal) {
        final int length = literal.length();
        return input -> {
            if (input.length() < length) {
                return new Result.Err<>("Expected literal: " + literal);
            }
            return input.startsWith(literal)
                    ? new Result.Ok<>(literal, input.substring(length))
                    : new Result.Err<>("Expected literal: " + literal);
        };
    }

    // Create a parser with a regular expression
    public Parser<String> regex(String regex) {
        final Pattern pattern = Pattern.compile("^" + regex);
        return input -> {
            Matcher matcher = pattern.matcher(input);
            return matcher.find()
                    ? new Result.Ok<>(matcher.group(), input.substring(matcher.end()))
                    : new Result.Err<>("No match for regex: " + regex);
        };
    }

    // Parser that matches a number with an optional decimal part (e.g. -123 or 123.45) using a regular expression
    public Parser<AstNode> numberParser() {
        return regex("([+-]?)\\d+(\\.\\d+)?").map(value -> new AstLiteralNode(Double.parseDouble(value)));
    }

    // Parser that matches a constant (pi or e)
    public Parser<AstNode> constantParser() {
        return oneOf(
            literal("pi").orElse(literal("PI")).map(__ -> new AstLiteralNode(Math.PI)),
            literal("e").map(__ -> new AstLiteralNode(Math.E))
        );
    }

    // Parser that matches an operator (+, -, *, /, %)
    public Parser<String> binaryOperatorParser() {
        return oneOf(
                literal("+"),
                literal("-"),
                literal("*"),
                literal("/"),
                literal("%")
            );
    }

    // Parser that matches a function (sen, cos, tang, arcsen, arccos, arctang)
    public Parser<String> unaryFunctionParser() {
        return oneOf(
                literal("sen"),
                literal("cos"),
                literal("tang"),
                literal("arcsen"),
                literal("arccos"),
                literal("arctang"));
    }        

    // Parser for a unary expression with a function and an argument (e.g. sen(45) )
    public Parser<AstNode> unaryFunctionExpressionParser() {
        return unaryFunctionParser()
                .andThen(op -> literal("(")
                .andThen(__ -> expressionParser()
                .andThen(arg -> literal(")")
                .map(___ -> new AstFunctionNode(op, List.of(arg))))));
    }

    // Factor parser: handles numbers, parenthesized expressions, and functions
    public Parser<AstNode> factorParser() {
        return numberParser()
                .orElse(constantParser())
                .orElse(literal("(").andThen(__ -> expressionParser()
                .andThen(expr -> literal(")")
                .map(___ -> expr))))
                .orElse(unaryFunctionExpressionParser());
    }

    // Term parser: handles multiplication, division, and modulus
    public Parser<AstNode> termParser() {
        return factorParser()
                .andThen(first -> binaryOperatorParser()
                .filter(op -> op.equals("*") || op.equals("/") || op.equals("%"), "Expected operator: *, /, or %")
                .andThen(op -> factorParser()
                .map(second -> new AstFunctionNode(op, List.of(first, second))))
                .orElse(factorParser()) // Just return the factor if no operator is found
        );
    }

    // Expression parser: handles addition and subtraction
    public Parser<AstNode> expressionParser() {
        return termParser()
                .andThen(first -> binaryOperatorParser()
                .filter(op -> op.equals("+") || op.equals("-"), "Expected operator: + or -")
                .andThen(op -> termParser()
                .map(second -> new AstFunctionNode(op, List.of(first, second))))
                .orElse(termParser()) // Just return the term if no operator is found
        );
    }

}
