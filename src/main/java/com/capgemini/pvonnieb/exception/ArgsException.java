package com.capgemini.pvonnieb.exception;

/**
 * A custom exception for Args.
 * It should be used instead of generic exceptions
 * and extended as needed.
 */
public class ArgsException extends Exception {
    private final char errorArgumentId;
    private final String errorParameter;
    private final ErrorCode errorCode;

    /**
     * Constructor that allows a custom message to be set.
     */
    public ArgsException(char errorArgumentId, String errorParameter, ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorArgumentId = errorArgumentId;
        this.errorParameter = errorParameter;
    }

    /**
     * Convenient constructor that makes use of canned error messages for common error codes
     */
    public ArgsException(char errorArgument, String errorParameter, ErrorCode errorCode) {
        super(errorMessage(errorArgument, errorParameter, errorCode));
        this.errorArgumentId = errorArgument;
        this.errorParameter = errorParameter;
        this.errorCode = errorCode;
    }

    private static String errorMessage(char errorArgumentId, String errorParameter, ErrorCode errorCode) {
        switch (errorCode) {
            case UNEXPECTED_ARGUMENT:
                return unexpectedArgumentMessage(errorParameter);
            case INVALID_ARGUMENT_NAME:
                return String.format("Bad character: '%c' in Args format: '%s'.", errorArgumentId, errorParameter);
            case INVALID_FORMAT:
                return String.format("Argument: '%c' has invalid format: '%s'.", errorArgumentId, errorParameter);
            case MISSING_STRING:
                return String.format("Could not find string parameter for -%c.", errorArgumentId);
            case INVALID_INTEGER:
                return String.format("Argument -%c expects an integer but was '%s'.", errorArgumentId, errorParameter);
            case MISSING_INTEGER:
                return String.format("Could not find integer parameter for -%c.", errorArgumentId);
            case INVALID_DOUBLE:
                return String.format("Argument -%c expects a double but was '%s'.", errorArgumentId, errorParameter);
            case MISSING_DOUBLE:
                return String.format("Could not find double parameter for -%c.", errorArgumentId);
            default:
                return "An error occurred, but no matching error message was found.";
        }
    }

    private static String unexpectedArgumentMessage(String errorParameter) {
        return "Argument(s) -" + errorParameter + " unexpected.";
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public char getErrorArgumentId() {
        return errorArgumentId;
    }

    public String getErrorParameter() {
        return errorParameter;
    }

    public enum ErrorCode {
        UNEXPECTED_ARGUMENT, INVALID_ARGUMENT_NAME, INVALID_FORMAT,
        MISSING_STRING,
        MISSING_INTEGER, INVALID_INTEGER,
        MISSING_DOUBLE, INVALID_DOUBLE
    }
}
