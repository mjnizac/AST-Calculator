package main.java.com.github.mannizcob.Parsers;

import java.util.function.Function;
import java.util.function.Predicate;

import main.java.com.github.mannizcob.Results.Result;

public interface Parser<T> {
    
    Result<T> parse(String input);

    // Combine two parsers, second is executed only if the first one succeeds
    default <U> Parser<U> andThen(Function<T, Parser<U>> nextParserFunction) {
        return input -> {
            Result<T> result = this.parse(input);
            return (result instanceof Result.Ok<T> okResult)
                ?   nextParserFunction.apply(okResult.getValue()).parse(okResult.getRest())
                :   (Result<U>) result;
        };
    }

    // Combine two parsers, second is executed only if the first one fails
    default <U> Parser<U> orElse(Parser<U> alternativeParser) {
        return input -> {
            Result<T> result = this.parse(input);
            return (result instanceof Result.Err)
                ? alternativeParser.parse(input)
                : (Result<U>) result;
        };
    }

    // Apply a function to the result of the parser
    default <U> Parser<U> map(Function<T, U> mapFunction) {
        return input -> {
            Result<T> result = this.parse(input);
            return (result instanceof Result.Ok<T> okResult)
                ? new Result.Ok<>(mapFunction.apply(okResult.getValue()), okResult.getRest())
                : (Result<U>) result;
        };
    }

    default Parser<T> filter(Predicate<T> predicate, String errorMessage) {
        return input -> {
            Result<T> result = this.parse(input);
            if (result instanceof Result.Ok<T> ok) {
                T value = ok.getValue();
                if (predicate.test(value)) {
                    return result; // Return the original successful result
                } else {
                    return new Result.Err<>(errorMessage); // Fail if predicate doesn't match
                }
            }
            return result; // Pass through the error result as-is
        };
    }
}
