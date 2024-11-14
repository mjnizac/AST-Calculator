package main.java.com.github.mannizcob.Results;

public abstract class Result<T> {
    
    private Result() { }

    // Method to check if the result is a success
    public abstract boolean isSuccess();

    // Class for results with success
    public static class Ok<T> extends Result<T> { // Result with a value and the rest of the input
        
        private final T value;
        private final String rest;

        public Ok(T value, String rest) {
            this.value = value;
            this.rest = rest;
        }

        public T getValue() {
            return value;
        }

        public String getRest() {
            return rest;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    // Class for results with errors
    public static class Err<T> extends Result<T> { // Result with an error message
        
        private final String errorMessage;

        public Err(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }
    }

}
