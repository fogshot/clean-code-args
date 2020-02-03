package com.capgemini.pvonnieb;

import com.capgemini.pvonnieb.exception.ArgsException;
import org.junit.jupiter.api.Test;

import static com.capgemini.pvonnieb.exception.ArgsException.ErrorCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgsTest {

    public static final String DEFAULT_SCHEMA = "b, d#, s*, x##";
    public static final String CONSTRUCTOR_SHOULD_HAVE_THROWN = "Args constructor should have thrown, but didn't.";
    public static final String A_DOUBLE_VALUE = "-3.67";

    @Test
    void usageForEmptySchema() throws ArgsException {
        Args args = new Args("", new String[0]);

        String usageString = args.usage();
        assertThat(usageString).describedAs("Usage message for empty schema should be an empty String.")
                .isEqualTo("");
    }

    @Test
    void usageForSchemaWithoutArguments() throws ArgsException {
        Args args = new Args("a*, s*, d*, f*", new String[]{});

        String usageString = args.usage();

        assertThat(usageString)
                .describedAs("The usage message should display the schema String surrounded by '-[' and ']'.")
                .isEqualTo("-[a*, s*, d*, f*]");
    }

    @Test
    void constructWithoutSchemaButWithArgument() {
        ArgsException thrown = assertThrows(ArgsException.class,
                () -> new Args("", new String[]{"-s"}),
                CONSTRUCTOR_SHOULD_HAVE_THROWN);
        assertThat(thrown.getErrorCode()).isEqualByComparingTo(ErrorCode.UNEXPECTED_ARGUMENT);
        assertThat(thrown.getErrorParameter()).isEqualTo("s");
    }

    @Test
    void constructWithoutSchemaButWithMultipleArguments() {
        ArgsException thrown = assertThrows(ArgsException.class,
                () -> new Args("", new String[]{"-s", "-y"}),
                CONSTRUCTOR_SHOULD_HAVE_THROWN);
        assertThat(thrown.getErrorCode()).isEqualByComparingTo(ErrorCode.UNEXPECTED_ARGUMENT);
        assertThat(thrown.getErrorParameter()).isEqualTo("sy");
    }

    @Test
    void constructWithNonLetterSchema() {
        ArgsException thrown = assertThrows(ArgsException.class,
                () -> new Args("*", new String[]{}),
                CONSTRUCTOR_SHOULD_HAVE_THROWN);
        assertThat(thrown.getErrorCode()).isEqualByComparingTo(ErrorCode.INVALID_ARGUMENT_NAME);
        assertThat(thrown.getErrorArgumentId()).isEqualTo('*');
    }

    @Test
    void constructWithSpacesInSchema() throws ArgsException {
        Args args = new Args("a, b", new String[]{"-a", "-b"});
        assertThat(args.has('a'));
        assertThat(args.has('b'));
    }

    @Test
    void constructWithoutSpacesInSchema() throws ArgsException {
        Args args = new Args("a,b", new String[]{"-a", "-b"});
        assertThat(args.has('a'));
        assertThat(args.has('b'));
    }

    @Test
    void constructWithInvalidArgumentFormat() {
        ArgsException e = assertThrows(ArgsException.class, () -> new Args("f┼", new String[]{}),
                CONSTRUCTOR_SHOULD_HAVE_THROWN);

        assertThat(e.getErrorCode()).describedAs("Invalid Argument format should produce this error code.")
                .isEqualByComparingTo(ErrorCode.INVALID_FORMAT);
        assertThat(e.getErrorArgumentId()).describedAs("The error message should contain the argument char.")
                .isEqualTo('f');
    }

    @Test
    void shouldThrowUnexpectedArgument() {

        ArgsException e = assertThrows(ArgsException.class,
                () -> new Args(DEFAULT_SCHEMA, new String[]{"-s", "hello", "-t"}),
                CONSTRUCTOR_SHOULD_HAVE_THROWN);

        assertThat(e.getErrorCode()).isEqualByComparingTo(ErrorCode.UNEXPECTED_ARGUMENT);
    }

    @Test
    void errorMessageShouldIdentifyMissingString() {
        ArgsException e = assertThrows(ArgsException.class,
                () -> new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s"}),
                CONSTRUCTOR_SHOULD_HAVE_THROWN);

        assertThat(e.getErrorArgumentId()).isEqualTo('s');
        assertThat(e.getErrorCode()).isEqualByComparingTo(ErrorCode.MISSING_STRING);
    }

    @Test
    void errorMessageShouldIdentifyInvalidInteger() {
        ArgsException e = assertThrows(ArgsException.class,
                () -> new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "asdf", "-x", A_DOUBLE_VALUE}),
                CONSTRUCTOR_SHOULD_HAVE_THROWN);

        assertThat(e.getErrorCode()).isEqualByComparingTo(ErrorCode.INVALID_INTEGER);
        assertThat(e.getErrorArgumentId()).isEqualTo('d');
    }

    @Test
    void errorMessageShouldIdentifyMissingInteger() {
        ArgsException e = assertThrows(ArgsException.class,
                () -> new Args(DEFAULT_SCHEMA, new String[]{"-b", "-x", A_DOUBLE_VALUE, "-d"}),
                CONSTRUCTOR_SHOULD_HAVE_THROWN);
        assertThat(e.getErrorCode()).isEqualByComparingTo(ErrorCode.MISSING_INTEGER);
        assertThat(e.getErrorArgumentId()).isEqualTo('d');
    }

    @Test
    void errorMessageShouldIdentifyInvalidDouble() {
        ArgsException e = assertThrows(ArgsException.class,
                () -> new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "1", "-s", "asdf", "-x", "asdf"}),
                CONSTRUCTOR_SHOULD_HAVE_THROWN);
        assertThat(e.getErrorCode()).isEqualByComparingTo(ErrorCode.INVALID_DOUBLE);
        assertThat(e.getErrorArgumentId()).isEqualTo('x');
    }

    @Test
    void errorMessageShouldIdentifyMissingDouble() {
        ArgsException e = assertThrows(ArgsException.class,
                () -> new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "1", "-s", "asdf", "-x"}),
                CONSTRUCTOR_SHOULD_HAVE_THROWN);
        assertThat(e.getErrorCode()).isEqualByComparingTo(ErrorCode.MISSING_DOUBLE);
        assertThat(e.getErrorArgumentId()).isEqualTo('x');
    }

    @Test
    void getStringShouldReturnStringParameter() throws ArgsException {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s", "asdf"});

        assertThat(args.getString('s')).describedAs("The returned String should be equal to the argument of '-s'.")
                .isEqualTo("asdf");
    }

    @Test
    void getStringShouldReturnEmptyStringForUnknownArg() throws ArgsException {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s", "asdf"});

        assertThat(args.getString('z')).describedAs("The returned String should be equal to ''.")
                .isEqualTo("");
    }

    @Test
    void getIntShouldReturnIntParameter() throws ArgsException {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s", "asdf"});

        assertThat(args.getInt('d')).describedAs("The returned Integer should be equal to the argument of '-d'.")
                .isEqualTo(3);
    }

    @Test
    void getIntShouldReturnZeroForUnknownArg() throws ArgsException {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s", "asdf"});

        assertThat(args.getInt('z')).describedAs("The returned Integer should be equal to Zero.")
                .isEqualTo(0);
    }

    @Test
    void getBooleanShouldReturnBooleanParameter() throws ArgsException {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s", "asdf"});

        assertThat(args.getBoolean('b'))
                .describedAs("The returned Boolean should be true.")
                .isTrue();
    }

    @Test
    void getBooleanShouldReturnFalseForUnknownArg() throws ArgsException {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s", "asdf"});

        assertThat(args.getBoolean('z'))
                .describedAs("The returned Boolean should be false.")
                .isFalse();
    }

    @Test
    void getDoubleShouldReturnDoubleParameter() throws ArgsException {
        Args args = new Args("b, x##", new String[]{"-b", "-x", A_DOUBLE_VALUE});
        assertThat(args.has('x'));
        assertThat(args.getDouble('x')).isEqualTo(-3.67);
    }
}