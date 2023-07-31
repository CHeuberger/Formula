/*
 * Copyright: Carlos F. Heuberger. All rights reserved.
 *
 */
package cfh.formula.expr;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Carlos F. Heuberger, 2022-09-05
 *
 */
public class Interpreter {

    public static final String HELP = """
                       H E L P
                      =========
                       SOURCE
                      --------
        source: comment 
              | assignment 
              | expression
        comment: '#' text
        assignment: name ':=' value
        name: Alpha Alphanumeric...
        expression: additive
        additive: multiplicative 
                | additive + multiplicative 
                | additive - multiplicative
        multiplicative: unary
                      | multiplicative * unary
                      | multiplicative / unary
                      | multiplicative % unary
        unary: value 
             | name
        value: int
        """;
    
    private final Output output;
    
    public Interpreter(Output output) {
        this.output = requireNonNull(output, "output");
    }
    
    
    public boolean execute(String source) {
        final var assignment = Pattern.compile("(\\p{Alpha}\\p{Alnum}*+)\\h*+:=\\h*+(.++)").matcher("");
        final var variables = new HashMap<String, Value>();
        try (var runner = new AutoCloseable() {
            boolean success = true;
            int lineNumber = 0;
            String line;
            Scanner scanner = new Scanner(source);
            
            boolean start() {
                while (nextLine()) {
                    if (! (comment() || variable() || expression())) {
                        success = false;
                        error(lineNumber, line, "unrecognized statement, expected comment, assignment or expression");
                    }
                }
                return success;
            }
            
            private boolean comment() {
                return line.isBlank() || line.startsWith("#");
            }
            
            private boolean variable() {
                if (assignment.reset(line).matches()) {
                    var name = assignment.group(1);
                    var value = assignment.group(2);
                    if (variables.containsKey(name)) {
                        success = false;
                        error(lineNumber, line, "variable redefinition", "actual: %s", variables.get(name));
                    } else {
                        try {
                            variables.put(name, Value.of(value));
                        } catch (NumberFormatException ex) {
                            success = false;
                            error(lineNumber, line, "invalid value: " + ex.getMessage());
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
            
            private boolean expression() {
                
                return false;
            }
            
            private boolean nextLine() {
                if (scanner.hasNextLine()) {
                    lineNumber += 1;
                    line = scanner.nextLine();
                    return true;
                } else {
                    line = null;
                    return false;
                }
            }
            
            @Override
            public void close() {
                scanner.close();
            }
        }) {
            output.printf("executing...%n");
            var result = runner.start();
            output.printf("done!%n%n");
            return result;
        }
    }
    
    private void error(int lineNumber, String line, String message) {
        output.printf("""
            Error: %s
                   line %d: "%s"
            """, message, lineNumber, line);
    }
    
    private void error(int lineNumber, String line, String message, String format, Object... args) {
        error(lineNumber, line, message);
        var details = format.formatted(args).replaceAll("(?m)^", "       ");
        output.printf("%s%n", details);
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    public interface Output {
        public void printf(String format, Object... args);
    }
}
