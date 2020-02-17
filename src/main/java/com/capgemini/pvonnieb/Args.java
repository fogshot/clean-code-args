package com.capgemini.pvonnieb;

import com.capgemini.pvonnieb.exception.ArgsException;

import java.util.*;
import java.util.stream.Collectors;

import static com.capgemini.pvonnieb.exception.ArgsException.ErrorCode.*;

/**
 * Utility class to parse command line arguments.
 * It can parse multiple types of single letter arguments.
 * <p>
 * Usage:
 * Args args = new Args(schema, args);
 * - where 'schema' is a schema string defining the types and names of the parameters.
 * Example: "l, p#, d*, v##" sets up 4 arguments of the following names and types
 * - l (boolean)
 * - p (integer)
 * - d (string)
 * - v (double)
 * <p>
 * - and 'args' holds arguments and their parameters passed to the application (e.g. java main.java -l -p 3002 -d /var/tmp/
 */
public class Args {
    private String schema;

    private Set<Character> unexpectedArguments = new TreeSet<>();
    private Map<Character, ArgumentMarshaller> marshallers = new HashMap<>();
    private Set<Character> argsFound = new HashSet<>();
    private Iterator<String> currentArgument;
    private List<String> argsList;

    public Args(String schema, String[] args) throws ArgsException {
        this.schema = schema;
        this.argsList = Arrays.asList(args);
        parse();
    }

    private void parse() throws ArgsException {
        parseSchema();
        parseArguments();
    }

    private void parseSchema() throws ArgsException {
        for (String element : schema.split(",")) {
            if (element.length() > 0) {
                parseSchemaElement(element.trim());
            }
        }
    }

    private void parseSchemaElement(String element) throws ArgsException {
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        if (elementTail.length() == 0) {
            marshallers.put(elementId, new BooleanArgumentMarshaller());
        } else if (elementTail.equals("*")) {
            marshallers.put(elementId, new StringArgumentMarshaller());
        } else if (elementTail.equals("#")) {
            marshallers.put(elementId, new IntegerArgumentMarshaller());
        } else if (elementTail.equals("##")) {
            marshallers.put(elementId, new DoubleArgumentMarshaller());
        } else {
            throw new ArgsException(elementId, elementTail, INVALID_FORMAT);
        }
    }

    private void validateSchemaElementId(char elementId) throws ArgsException {
        if (!Character.isLetter(elementId)) {
            throw new ArgsException(elementId, schema, INVALID_ARGUMENT_NAME);
        }
    }

    private void parseArguments() throws ArgsException {
        for (currentArgument = argsList.iterator(); currentArgument.hasNext(); ) {
            String arg = currentArgument.next();
            parseArgument(arg);
        }
        if (!unexpectedArguments.isEmpty()) {
            throw new ArgsException('\0', unexpectedArguments.stream().map(Objects::toString)
                    .collect(Collectors.joining()), UNEXPECTED_ARGUMENT);
        }
    }

    private void parseArgument(String arg) throws ArgsException {
        if (arg.startsWith("-")) {
            parseElements(arg);
        }
    }

    private void parseElements(String arg) throws ArgsException {
        for (int i = 1; i < arg.length(); i++) {
            parseElement(arg.charAt(i));
        }
    }

    private void parseElement(char argChar) throws ArgsException {
        if (setArgument(argChar)) {
            argsFound.add(argChar);
        } else {
            unexpectedArguments.add(argChar);
        }
    }

    private boolean setArgument(char argChar) throws ArgsException {
        ArgumentMarshaller m = marshallers.get(argChar);
        if (m == null) {
            return false;
        }
        try {
            m.set(currentArgument);
            return true;
        } catch (ArgsException e) {
            throw new ArgsException(argChar, e.getErrorParameter(), e.getErrorCode());
        }
    }

    /**
     * Obtain the value of a Boolean argument (b).
     *
     * @param arg the name of the argument
     * @return the value of the argument's parameter, or false if no value could be found.
     */
    public boolean getBoolean(char arg) {
        ArgumentMarshaller am = marshallers.get(arg);
        try {
            return am != null && (Boolean) am.get();
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * Obtain the value of a String argument (s*).
     *
     * @param arg the name of the argument
     * @return the value of the argument's parameter, or "" if no value could be found.
     */
    public String getString(char arg) {
        ArgumentMarshaller am = marshallers.get(arg);
        try {
            return am == null ? "" : (String) am.get();
        } catch (ClassCastException e) {
            return "";
        }
    }

    /**
     * Obtain the value of an Integer argument (d#).
     *
     * @param arg the name of the argument
     * @return the value of the argument's parameter, or 0 if no value could be found.
     */
    public int getInt(char arg) {
        ArgumentMarshaller am = marshallers.get(arg);
        try {
            return am == null ? 0 : (Integer) am.get();
        } catch (ClassCastException e) {
            return 0;
        }
    }

    /**
     * Obtain the value of a Double argument (f##).
     *
     * @param arg the name of the argument
     * @return the value of the argument's parameter, or 0 if no value could be found.
     */
    public double getDouble(char arg) {
        ArgumentMarshaller am = marshallers.get(arg);
        return am == null ? 0 : (Double) am.get();
    }

    /**
     * Print a help text that shows the argument schema to the user.
     */
    public String usage() {
        if (schema.length() > 0) {
            return "-[" + schema + "]";
        } else {
            return "";
        }
    }

    /**
     * Returns true if the given argument has been parsed by Args.
     *
     * @param argChar the name of the argument to check
     * @return true if the argument was found; false otherwise
     */
    public boolean has(char argChar) {
        return marshallers.containsKey(argChar);
    }

    private interface ArgumentMarshaller {


        void set(Iterator<String> currentArgument) throws ArgsException;

        Object get();

    }

    private static class BooleanArgumentMarshaller implements ArgumentMarshaller {

        private boolean booleanValue = false;

        @Override
        public void set(Iterator<String> currentArgument) {
            booleanValue = true;
        }

        @Override
        public Object get() {
            return booleanValue;
        }

    }

    private static class IntegerArgumentMarshaller implements ArgumentMarshaller {

        private int integerValue = 0;

        @Override
        public void set(Iterator<String> currentArgument) throws ArgsException {
            String parameter = null;
            try {
                parameter = currentArgument.next();
                integerValue = Integer.parseInt(parameter);
            } catch (NoSuchElementException e) {
                throw new ArgsException('\0', parameter, MISSING_INTEGER);
            } catch (NumberFormatException e) {
                throw new ArgsException('\0', parameter, INVALID_INTEGER);
            }
        }

        @Override
        public Object get() {
            return integerValue;
        }

    }

    private static class DoubleArgumentMarshaller implements ArgumentMarshaller {
        private double doubleValue = 0;

        @Override
        public void set(Iterator<String> currentArgument) throws ArgsException {
            String parameter = null;
            try {
                parameter = currentArgument.next();
                doubleValue = Double.parseDouble(parameter);
            } catch (NoSuchElementException e) {
                throw new ArgsException('\0', parameter, MISSING_DOUBLE);
            } catch (NumberFormatException e) {
                throw new ArgsException('\0', parameter, INVALID_DOUBLE);
            }
        }

        @Override
        public Object get() {
            return doubleValue;
        }
    }

    private static class StringArgumentMarshaller implements ArgumentMarshaller {

        private String stringValue = "";

        @Override
        public void set(Iterator<String> currentArgument) throws ArgsException {
            try {
                stringValue = currentArgument.next();
            } catch (NoSuchElementException e) {
                throw new ArgsException('\0', null, MISSING_STRING);
            }
        }

        @Override
        public Object get() {
            return stringValue == null ? "" : stringValue;
        }

    }
}
