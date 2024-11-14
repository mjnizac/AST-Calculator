
# Mathematical Expression Parser

This repository contains a mathematical expression parser built using Java. It aims to parse and evaluate mathematical expressions, supporting operations such as addition, subtraction, multiplication, division, modulus, and trigonometric functions like `sin`, `cos`, etc.

## Features
- **Expression Parsing**: Supports parsing expressions with addition, subtraction, multiplication, division, modulus, and unary functions like `sin(x)`, `cos(x)`, etc.
- **AST (Abstract Syntax Tree)**: Converts expressions into an AST for evaluation.
- **Error Handling**: Provides detailed error messages when parsing fails.
- **Custom Parsers**: Includes methods to build custom parsers, combining them with combinators like `andThen`, `orElse`, `filter`, etc.

---

## Overview of the Parser Design

The parser is designed to break down mathematical expressions and build an Abstract Syntax Tree (AST) from them. Here’s an overview of the main components:

### 1. **Parser Interface**
The `Parser<T>` interface defines the structure for all parsers. A parser takes an input string and attempts to parse it into an object of type `T`. The result is either a successful parsing (`Result.Ok<T>`) or a failure (`Result.Err<T>`).

### 2. **Combinators**
The `Parser` interface also provides several combinators to combine parsers:
- `andThen`: Chains parsers together, executing the second parser only if the first one succeeds.
- `orElse`: Tries an alternative parser if the first parser fails.
- `map`: Transforms the result of the parser.
- `filter`: Adds a validation step to the parser to check if the parsed value meets a certain condition.

### 3. **Basic Parsers**
The basic parsers in the implementation handle:
- **Literals**: Exact string matches (e.g., `"+"`, `"pi"`, `"e"`).
- **Regex**: Regular expressions to match numbers, decimal points, etc.
- **Constants**: Matches mathematical constants such as `pi` and `e`.
- **Operators**: Parsers for binary operators (`+`, `-`, `*`, `/`, `%`) and unary operators (`-`, `sin`, `cos`, etc.).

### 4. **Expression Parsing**
Expressions are parsed hierarchically:
- **Expression**: The highest level, handling addition (`+`) and subtraction (`-`).
- **Term**: Handles multiplication (`*`), division (`/`), and modulus (`%`).
- **Factor**: Handles numbers, parentheses, and functions (e.g., `sin(45)`).
- **Unary Expression**: Handles negative numbers or functions like `-sin(45)`.

### 5. **Error Handling**
If the input cannot be parsed, the parser returns an error with a detailed message indicating what went wrong.

---

## Example Usage

Below is a simple example demonstrating how to parse and evaluate the expression `"2+3"`:

### Main Code Example:

```java
public static void main(String[] args) {
    Parsers parsers = new Parsers();
    Parser<AstNode> parser = parsers.expressionParser();

    String input = "2+3";
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
```

### Output:
```
Expression: (2 + 3)
Result: 5.0
```

---

## Troubleshooting Parsing Errors

If you encounter the error `No parser matched in oneOf`, it means that the parser is not able to recognize any part of the input. This usually happens when:
1. The input doesn’t match any of the defined parsers (e.g., no number, operator, or function matches).
2. The parsers are not defined correctly or are ordered incorrectly.

### Common Mistakes:
- **Incorrect Order of Parsers**: Ensure the parsers are defined in the correct order. For example, `expressionParser` should first check for terms (numbers, functions, etc.), then operators.
- **Missing Parsers**: Ensure all cases are covered in your parser combinators (e.g., unary and binary operators, constants).
- **Parentheses Handling**: When dealing with parentheses, ensure that the parser handles them correctly by checking if opening and closing parentheses match.

### Example Fix:

```java
// A corrected expression parser with binary operators
public Parser<AstNode> expressionParser() {
    return termParser()
            .andThen(first -> binaryOperatorParser()
                    .filter(op -> op.equals("+") || op.equals("-"), "Expected operator: + or -")
                    .andThen(op -> termParser()
                        .map(second -> new AstFunctionNode(op, List.of(first, second)))))
            .orElse(termParser()); // If no operator is found, just return the term
}
```

---

## Parser Implementation

Here are some key methods and their functions:

### 1. **`oneOf` Method**
The `oneOf` method tries multiple parsers and returns the result of the first successful one.

```java
@SafeVarargs
private static <T> Parser<T> oneOf(Parser<T>... parsers) {
    return input -> {
        for (Parser<T> parser : parsers) {
            Result<T> result = parser.parse(input);
            if (result instanceof Result.Ok) { return result; }
        }
        return new Result.Err<>("No parser matched in oneOf.");
    };
}
```

### 2. **`literal` Method**
The `literal` method checks if the input starts with a specific string and returns it.

```java
private Parser<String> literal(String literal) {
    final int length = literal.length();
    return input -> {
        if (input.startsWith(literal)) {
            return new Result.Ok<>(literal, input.substring(length));
        }
        return new Result.Err<>("Expected literal: " + literal);
    };
}
```

### 3. **`factorParser`**
The `factorParser` handles the lowest level of expressions, including numbers, functions, and expressions inside parentheses.

```java
private Parser<AstNode> factorParser() {
    return numberParser()
            .map(value -> new AstLiteralNode(Double.parseDouble(value)))
            .orElse(constantParser())
            .orElse(literal("(").andThen(__ -> expressionParser()
                .andThen(expr -> literal(")").map(___ -> expr))))
            .orElse(unaryFunctionExpressionParser());
}
```

---

## Conclusion

This mathematical expression parser is a flexible and robust system that can handle a variety of mathematical expressions. The key to building an effective parser is to carefully combine smaller parsers for literals, numbers, operators, and functions. The use of parser combinators allows for the creation of complex parsers from simple building blocks, making the parser highly extensible.

By following the steps outlined in this README, you should be able to integrate, extend, and debug this parser for your needs.
