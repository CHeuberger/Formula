/*
 * Copyright: Carlos F. Heuberger. All rights reserved.
 *
 */
package cfh.formula.expr;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Scanner;
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
        output.printf("executing...%n");
        var success = true;
        var variables = new HashMap<String, Value>();
        var assignment = Pattern.compile("(\\p{Alpha}\\p{Alnum}*+)\\h*+:=\\h*+(.++)").matcher("");
        var lineNumber = 0;
        try (var scanner = new Scanner(source)) {
            while (scanner.hasNextLine()) {
                lineNumber += 1;
                var line = scanner.nextLine();
                if (line.isBlank() || line.startsWith("#")) {
                    // ignored
                    
                } else if (assignment.reset(line).matches()) {
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
                    
                } else {
                    // TODO
                }
                
                success = false;
                error(lineNumber, line, "unrecognized statement, expected assignment or expression");
            }
        }
        
        output.printf("done!%n%n");
        return success;
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
