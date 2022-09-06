/*
 * Copyright: Carlos F. Heuberger. All rights reserved.
 *
 */
package cfh.formula.expr;

import java.math.BigInteger;
import java.util.Objects;

/**
 * @author Carlos F. Heuberger, 2022-09-06
 *
 */
public sealed abstract class Value {

    public static Value of(String text) throws NumberFormatException {
        return new IntValue(text);
    }
    
    @Override
    public abstract String toString();
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final class IntValue extends Value {

        private final BigInteger value;
        
        IntValue(String text) throws NumberFormatException {
            value = new BigInteger(text);
        }
        
        @Override
        public int hashCode() { return value.hashCode(); }
        
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof IntValue other) 
                && Objects.equals(other.value, this.value);
        }
        
        @Override
        public String toString() { return value.toString(); }
    }
}
