package com.capgemini.pvonnieb.exception;

import org.junit.jupiter.api.Test;

import static com.capgemini.pvonnieb.exception.ArgsException.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;

class ArgsExceptionTest {

    @Test
    void testUnexpectedArgumentMessage() {
        ArgsException e = new ArgsException('x', "x", UNEXPECTED_ARGUMENT);
        assertThat(e.getMessage()).isEqualTo("Argument(s) -x unexpected.");
    }

    @Test
    void testInvalidArgumentMessage() {
        ArgsException e = new ArgsException('-', "d*", INVALID_ARGUMENT_NAME);
        assertThat(e.getMessage())
                .isEqualTo("Bad character: '" + e.getErrorArgumentId() +
                        "' in Args format: '" + e.getErrorParameter() + "'.");
    }

    @Test
    void testInvalidFormatMessage() {
        ArgsException e = new ArgsException('x', "~~", INVALID_FORMAT);
        assertThat(e.getMessage()).isEqualTo("Argument: 'x' has invalid format: '~~'.");
    }

    @Test
    void testMissingStringMessage() {
        ArgsException e = new ArgsException('x', null, ArgsException.ErrorCode.MISSING_STRING);
        assertThat(e.getMessage()).isEqualTo("Could not find string parameter for -x.");
    }

    @Test
    void testMissingIntegerMessage() {
        ArgsException e = new ArgsException('x', null, ArgsException.ErrorCode.MISSING_INTEGER);
        assertThat(e.getMessage()).isEqualTo("Could not find integer parameter for -x.");
    }

    @Test
    void testInvalidIntegerMessage() {
        ArgsException e = new ArgsException('x', "Forty two", ArgsException.ErrorCode.INVALID_INTEGER);
        assertThat(e.getMessage()).isEqualTo("Argument -x expects an integer but was 'Forty two'.");
    }

    @Test
    void testMissingDoubleMessage() {
        ArgsException e = new ArgsException('x', null, ArgsException.ErrorCode.MISSING_DOUBLE);
        assertThat(e.getMessage()).isEqualTo("Could not find double parameter for -x.");
    }

    @Test
    void testInvalidDoubleMessage() {
        ArgsException e = new ArgsException('x', "Forty two point five", ArgsException.ErrorCode.INVALID_DOUBLE);
        assertThat(e.getMessage()).isEqualTo("Argument -x expects a double but was 'Forty two point five'.");
    }
}