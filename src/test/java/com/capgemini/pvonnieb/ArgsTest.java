package com.capgemini.pvonnieb;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgsTest {

    public static final String DEFAULT_SCHEMA = "b, d#, s*";

    @Test
    void usageForEmptySchema() throws ParseException {
        Args args = new Args("", new String[]{});

        String usageString = args.usage();

        assertThat(usageString).describedAs("Usage message for empty schema should be an empty String.")
                .isEqualTo("");
    }

    @Test
    void usageForInvalidSchema() throws ParseException {
        Args args = new Args("a*,s*,d*,f*", new String[]{});

        String usageString = args.usage();

        assertThat(usageString)
                .describedAs("The usage message should display the schema String surrounded by '-[' and ']'.")
                .isEqualTo("-[a*,s*,d*,f*]");
    }

    @Test
    @SuppressWarnings("squid:S00112")
        // this method SHOULD throw a generic Exception
    void errorMessageShouldThrowForOk() throws Exception {
        Args args = new Args("", new String[]{});
        Exception thrown = assertThrows(Exception.class,
                args::errorMessage,
                "Expected errorMessage() to throw, but it didn't.");
        assertThat(thrown.getMessage())
                .describedAs("errorMessage should throw exception for OK case.")
                .isEqualTo("TILT: Should not get here.");
    }

    @Test
    void errorMessageShouldIdentifyUnexpectedArgument() throws Exception {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-s", "hello", "-t"});

        String errorMessage = args.errorMessage();

        assertThat(errorMessage).describedAs("The error message should mention the unexpected argument '-t'.")
                .isEqualTo("Argument(s) -t unexpected.");
    }

    @Test
    void errorMessageShouldIdentifyMissingString() throws Exception {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s"});

        String errorMessage = args.errorMessage();

        assertThat(errorMessage).describedAs("The error message should mention the missing string parameter for '-s'.")
                .isEqualTo("Could not find string parameter for -s.");
    }

    @Test
    void errorMessageShouldIdentifyInvalidInteger() throws Exception {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "asdf"});

        String errorMessage = args.errorMessage();

        assertThat(errorMessage).describedAs("The error message should mention the invalid integer parameter for '-d'.")
                .isEqualTo("Argument -d expects an integer but was 'asdf'.");
    }

    @Test
    void errorMessageShouldIdentifyMissingInteger() throws Exception {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d"});

        String errorMessage = args.errorMessage();

        assertThat(errorMessage).describedAs("The error message should mention the missing integer parameter for '-d'.")
                .isEqualTo("Could not find Integer parameter for -d.");
    }

    @Test
    void getStringShouldReturnStringParameter() throws ParseException {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s", "asdf"});

        assertThat(args.getString('s')).describedAs("The returned String should be equal to the argument of '-s'.")
                .isEqualTo("asdf");
    }

    @Test
    void getStringShouldReturnEmptyStringForUnknownArg() throws ParseException {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s", "asdf"});

        assertThat(args.getString('z')).describedAs("The returned String should be equal to ''.")
                .isEqualTo("");
    }

    @Test
    void getIntShouldReturnIntParameter() throws ParseException {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s", "asdf"});

        assertThat(args.getInt('d')).describedAs("The returned Integer should be equal to the argument of '-d'.")
                .isEqualTo(3);
    }

    @Test
    void getIntShouldReturnZeroForUnknownArg() throws ParseException {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s", "asdf"});

        assertThat(args.getInt('z')).describedAs("The returned Integer should be equal to Zero.")
                .isEqualTo(0);
    }

    @Test
    void getBooleanShouldReturnBooleanParameter() throws ParseException {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s", "asdf"});

        assertThat(args.getBoolean('b'))
                .describedAs("The returned Boolean should be true.")
                .isTrue();
    }

    @Test
    void getBooleanShouldReturnFalseForUnknownArg() throws ParseException {
        Args args = new Args(DEFAULT_SCHEMA, new String[]{"-b", "-d", "3", "-s", "asdf"});

        assertThat(args.getBoolean('z'))
                .describedAs("The returned Boolean should be false.")
                .isFalse();
    }
}