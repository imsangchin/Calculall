package com.trutech.calculall;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Contains miscellaneous static methods that provide utility.
 *
 * @version 0.3.0
 */
public class Utility {

    //ID of the Buttons (nowhere else to put them)
    public static final int MC = 0, MR = 1, MS = 2, M_ADD = 3, M_REMOVE = 4, BACKSPACE = 5, CE = 6, C = 7, ZERO = 8, ONE = 9, TWO = 10, THREE = 11, FOUR = 12, FIVE = 13, SIX = 14, SEVEN = 15, EIGHT = 16, NINE = 17, PLUS = 18, MINUS = 19, MULTIPLY = 20, DIV = 21, RECIPROC = 22, DECIMAL_SEP = 23, SIGN = 24, SQRT = 25, PERCENT = 26, CALCULATE = 27, DUMMY = 28;


    /**
     *
     * @param expression The expression to print
     * @return The string representation of the given expression
     */
    public static String printExpression(ArrayList<Token> expression) {
        String s = "";
        for (Token token : expression) {
            if (token instanceof Number) {
                if (((Number) token).getValue() % 1 != 0) {
                    s += ((Number) token).getValue();
                } else {
                    s += (((Number) token).getValue());
                }
            } else if (token instanceof Operator) {
                switch (((Operator) token).getType()) {
                    case Operator.FRACTION:
                        s += "/";
                        break;
                    case Operator.MULTIPLY:
                        s += "*";
                        break;
                    case Operator.EXPONENT:
                        s += "^";
                        break;
                    case Operator.SUBTRACT:
                        s += "-";
                        break;
                    default:
                        s += token.getSymbol();
                }
            } else if (token instanceof Variable) {
                switch (((Variable) token).getType()) {
                    case Variable.PI:
                        s += "Pi";
                        break;
                    case Variable.X:
                        s += "x";
                        break;
                    default:
                        s += token.getSymbol();
                        break;
                }
            } else if (token instanceof Function) {
                switch (((Function) token).getType()) {
                    case Function.SIN:
                        s += "Sin";
                        break;
                    case Function.COS:
                        s += "Cos";
                        break;
                    case Function.TAN:
                        s += "Tan";
                        break;
                    case Function.ARCSIN:
                        s += "ArcSin";
                        break;
                    case Function.ARCCOS:
                        s += "ArcCos";
                        break;
                    case Function.ARCTAN:
                        s += "ArcTan";
                        break;
                    case Function.LN:
                        s += "Ln";
                        break;
                    default:
                        throw new IllegalArgumentException("NOT SUPPORTED YET");
                        //TODO: IMPLEMENT OTHERS
                }
            } else if (token instanceof Bracket) {
                switch (((Bracket) token).getType()) {
                    case Bracket.DENOM_OPEN:
                        s += "(";
                        break;
                    case Bracket.NUM_OPEN:
                        s += "(";
                        break;
                    case Bracket.SUPERSCRIPT_OPEN:
                        s += "(";
                        break;
                    case Bracket.OPEN:
                        s += "(";
                        break;
                    case Bracket.SUPERSCRIPT_CLOSE:
                        s += ")";
                        break;
                    case Bracket.DENOM_CLOSE:
                        s += ")";
                        break;
                    case Bracket.NUM_CLOSE:
                        s += ")";
                        break;

                    case Bracket.CLOSE:
                        s += ")";
                        break;
                    default:
                        throw new IllegalArgumentException(); //This should not happen
                }
            } else {
                s += token.getSymbol();
            }
        }
        return s;
    }

    /**
     * Returns the numerical value of a given set of digits.
     *
     * @param digits A list of digits to find the value of
     * @return The value of the given digits
     */
    public static double valueOf(List<Digit> digits) {
        double value = 0;
        int indexOfDecimal = -1;
        boolean negative = false;
        //Does negatives first
        try {
            while (digits.get(0).getValue() == DigitFactory.NEGATIVE) { //Only accepts negatives at the beginning
                digits.remove(0);
                negative = negative ? false : true; //Allows for multiple negatives
            }
        } catch (IndexOutOfBoundsException e) { //The digits only contains negatives (occurs during adding a neg to variables)
            return negative ? -1 : 1;
        }
        //Finds what index the decimal is in
        for (int i = 0; i < digits.size(); i++) {
            if (digits.get(i).getValue() == -1) {
                indexOfDecimal = i;
            }
        }

        //Sets the starting power based on where the decimal place was found, if any
        int power = indexOfDecimal == -1 ? digits.size() - 1 : indexOfDecimal - 1;
        for (Digit digit : digits) {
            if (digit.getValue() == -1) { //Decimal place
            } else if (digit.getValue() == DigitFactory.NEGATIVE) {
                throw new IllegalArgumentException("Invalid placement of negatives in Digits");
            } else {
                value += digit.getValue() * Math.pow(10, power);
                power--;
            }
        }
        ;

        return negative ? value * -1 : value;
    }


    /**
     * Transforms all the digits into numbers as well as replacing Variables with numbers.
     *
     * @param tokens The expression to condense digits
     * @return The expression with the digits condensed
     */
    public static ArrayList<Token> condenseDigits(ArrayList<Token> tokens) {
        ArrayList<Token> newTokens = new ArrayList<Token>();
        ArrayList<Digit> digits = new ArrayList<Digit>();
        boolean atDigits = false; //Tracks if it's currently tracking digits
        for (Token token : tokens) {
            if (atDigits) { //Going through digits
                if (token instanceof Digit) { //Number keeps going
                    digits.add((Digit) token);
                } else { //Number ended
                    atDigits = false;
                    newTokens.add(new Number(Utility.valueOf(digits))); //Adds the sum of all the digits
                    digits.clear();
                    newTokens.add(token);
                }
            } else { //Not going through digits
                if (token instanceof Digit) { //Start of a number
                    atDigits = true;
                    digits.add((Digit) token);
                } else { //Not a digit; adds to the new list
                    newTokens.add(token);
                }
            }
        }
        if (!digits.isEmpty() && atDigits) { //Digits left
            newTokens.add(new Number(Utility.valueOf(digits)));
        }
        return newTokens;
    }

    /**
     * Sets up the given expression to be processed and allows established
     * conventions (such as (a + b)(c + d) implying (a + b) * (c + d)).
     *
     * @param toSetup The expression to set up
     * @return The expression with the added Tokens to make the
     */
    public static ArrayList<Token> setupExpression(ArrayList<Token> toSetup) {
        ArrayList<Token> newExpression = new ArrayList<Token>();
        for (Token t : toSetup) {
            Token last = newExpression.isEmpty() ? null : newExpression.get(newExpression.size() - 1); //Last token in the new expression
            if (t instanceof Bracket) {
                Bracket b = (Bracket) t;
                if (b.getType() == Bracket.OPEN && last instanceof Bracket && ((Bracket) last).getType() == Bracket.CLOSE) { //Ex. (2 + 1)(3 + 4)
                    newExpression.add(OperatorFactory.makeMultiply()); //Implies multiplication between the two expressions in the brackets
                } else if (last instanceof Number && b.getType() == Bracket.OPEN) { //Ex. 3(2 + 1)
                    newExpression.add(OperatorFactory.makeMultiply());
                }
            } else if (t instanceof Number || t instanceof Variable) { //So it works with Function mode too
                if (last instanceof Number) { //Ex. 5A
                    newExpression.add(OperatorFactory.makeMultiply());
                }
            } else if (t instanceof Function) {
                if (last instanceof Number || last instanceof Function
                        || (last instanceof Bracket && ((Bracket) last).getType() == Bracket.CLOSE)) { //Ex. 2f(x) or f(x)g(x) or (1 + 2)f(x)
                    newExpression.add(OperatorFactory.makeMultiply());
                }
            }
            newExpression.add(t);
        }
        return newExpression;
    }


    /**
     * Uses the shunting yard algorithm to change the expression from infix to reverse polish.
     *
     * @param infix The infix expression
     * @return The expression in reverse polish
     * @throws java.lang.IllegalArgumentException The infix notation is invalid
     */
    public static ArrayList<Token> convertToReversePolish(ArrayList<Token> infix) {
        ArrayList<Token> reversePolish = new ArrayList<Token>();
        Stack<Token> stack = new Stack<Token>();
        for (Token token : infix) {
            if (token instanceof Number || token instanceof Variable) { //Adds directly to the queue if it's a token
                reversePolish.add(token);
            } else if (token instanceof Function) { //Adds to the stack if it's a function
                stack.push(token);
            } else if (token instanceof Operator) {
                if (!stack.empty()) { //Make sure it's not empty to prevent bugs
                    Token top = stack.lastElement();
                    while (top != null && ((top instanceof Operator && ((Operator) token).isLeftAssociative()
                            && ((Operator) top).getPrecedence() >= ((Operator) token).getPrecedence()) || top instanceof Function)) { //Operator is left associative and higher precendence / is a function
                        reversePolish.add(stack.pop()); //Pops top element to the queue
                        top = stack.isEmpty() ? null : stack.lastElement(); //Assigns the top element of the stack if it exists
                    }
                }
                stack.push(token);
            } else if (token instanceof Bracket) {
                Bracket bracket = (Bracket) token;
                if (bracket.getType() == Bracket.OPEN || bracket.getType() == Bracket.SUPERSCRIPT_OPEN
                        || bracket.getType() == Bracket.NUM_OPEN || bracket.getType() == Bracket.DENOM_OPEN) { //Pushes the bracket to the stack if it's open
                    stack.push(bracket);
                } else if (bracket.getType() == Bracket.CLOSE || bracket.getType() == Bracket.SUPERSCRIPT_CLOSE
                        || bracket.getType() == Bracket.NUM_CLOSE || bracket.getType() == Bracket.DENOM_CLOSE) { //For close brackets, pop operators onto the list until a open bracket is found
                    Token top = stack.lastElement();
                    while (!(top instanceof Bracket)) { //While it has not found an open bracket
                        reversePolish.add(stack.pop()); //Pops the top element
                        if (stack.isEmpty()) { //Mismatched brackets
                            throw new IllegalArgumentException();
                        }
                        top = stack.lastElement();
                    }
                    stack.pop(); //Removes the bracket
                }
            }
        }
        //All tokens read at this point
        while (!stack.isEmpty()) { //Puts the remaining tokens in the stack to the queue
            reversePolish.add(stack.pop());
        }
        return reversePolish;
    }

    /**
     * Evaluates a given expression in reverse polish notation and returns the resulting value.
     *
     * @param tokens The expression in reverse polish
     * @return The value of the expression
     * @throws java.lang.IllegalArgumentException The user has inputted an invalid expression
     */
    public static double evaluateExpression(ArrayList<Token> tokens) {
        Stack<Number> stack = new Stack<Number>();
        for (Token token : tokens) {
            if (token instanceof Number) { //Adds all numbers directly to the stack
                stack.push((Number) token);
            } else if (token instanceof Operator) {
                //Operates the first and second top operators
                Number right = stack.pop();
                Number left = stack.pop();
                stack.push(new Number(((Operator) token).operate(left.getValue(), right.getValue()))); //Adds the result back to the stack
            } else if (token instanceof Function) { //Function uses the top number on the stack
                Number top = stack.pop(); //Function performs on the first number
                stack.push(new Number(((Function) token).perform(top.getValue()))); //Adds the result back to the stack
            } else { //This should never bee reached
                throw new IllegalArgumentException();
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Stack size is empty"); //There should only be 1 token left on the stack
        } else {
            return stack.pop().getValue();
        }
    }

    /**
     * Simplifies and Rationalizes the given expression.
     *
     * @param expression The un-simplified expression
     * @return The simplified expression
     */
    public static ArrayList<Token> simplifyExpression(ArrayList<Token> expression) {
        ArrayList<Token> num = new ArrayList<Token>();
        ArrayList<Token> den = new ArrayList<Token>();
        int intNum = 0;
        int intDen = 0;
        int divisionIndex;
        for (Token token : expression) {
            if (token instanceof Operator) {
                if ((((Operator) (token)).getType() == 4)) {
                    divisionIndex = expression.indexOf(token);
                    for (int i = 0; i < divisionIndex; i++) {
                        num.add(expression.get(i));
                    }
                    for (int i = divisionIndex + 1; i < expression.size(); i++) {
                        den.add(expression.get(i));
                    }
                }
            }
        }
        if (num.size() == 1) {
            intNum = ((Digit) (num.get(0))).getValue();
        }
        if (den.size() == 1) {
            intDen = ((Digit) (den.get(0))).getValue();
        }

        return null;
    }

    public static ArrayList<Token> simplifyVector(ArrayList<Token> expression) {
        return VRuleSet.reduce(expression);
    }

    public static ArrayList<Token> convertDoublesToVector(double[] vector) {
        ArrayList<Token> newVector = new ArrayList<Token>();
        newVector.add(BracketFactory.makeOpenSquareBracket());
        newVector.add(new Number(vector[0]));
        newVector.add(new Token(",") {
        });
        newVector.add(new Number(vector[1]));
        ;
        if (vector.length > 2) {
            newVector.add(new Token(",") {
            });
            newVector.add(new Number(vector[2]));
        }
        newVector.add(BracketFactory.makeCloseSquareBracket());
        return newVector;
    }

    /**
     * Converts an expression of Tokens into an expression represented by a String
     *
     * @param expression Expression being converted to a string
     * @return String String representation of the expression
     */
    public static String convertTokensToString(ArrayList<Token> expression) {
        String stringExpression = new String("");
        for (int i = 0; i < expression.size(); i++) {
            if (expression.get(i) instanceof Number) {
                String s = String.valueOf(((Number) expression.get(i)).getValue());
                s = s.indexOf(".") < 0 ? s : (s.indexOf("E") > 0 ? s.substring(0, s.indexOf("E")).replaceAll("0*$", "")
                        .replaceAll("\\.$", "").concat(s.substring(s.indexOf("E"))) : s.replaceAll("0*$", "")
                        .replaceAll("\\.$", "")); //Removes trailing zeroes
                stringExpression = stringExpression + s;
            } else {
                stringExpression = stringExpression + (expression.get(i)).getSymbol();
            }
        }
        return stringExpression;
    }

    /**
     * Gets the tokens of all stored vectors from variables and creates a new expression for calculations
     *
     * @param expression Expression of Tokens
     * @return ArrayList<Token> New expression of Tokens
     */
    public static ArrayList<Token> convertVariablesToTokens(ArrayList<Token> expression) {
        ArrayList<Token> tempExpression = new ArrayList<Token>();
        for (Token t : expression) {
            if (t instanceof Vector) {
                ArrayList<Token> tempVector = ((Vector) t).getVector();
                tempExpression.addAll(tempVector);
            } else {
                tempExpression.add(t);
            }
        }
        return tempExpression;

    }

    public static double[] calculateAddOrSubtract(double[] vectorLeft, double[] vectorRight, int operator) {
        if (vectorLeft.length == vectorRight.length) {
            int dimensions = vectorLeft.length;
            if (operator == VRuleSet.ADD) {
                if (dimensions == 2) {
                    double[] expression = new double[2];
                    expression[0] = Utility.round(vectorLeft[0] + vectorRight[0], 3);
                    expression[1] = Utility.round(vectorLeft[1] + vectorRight[1], 3);
                    return expression;
                } else if (dimensions == 3) {
                    double[] expression = new double[3];
                    expression[0] = Utility.round(vectorLeft[0] + vectorRight[0], 3);
                    expression[1] = Utility.round(vectorLeft[1] + vectorRight[1], 3);
                    expression[2] = Utility.round(vectorLeft[2] + vectorRight[2], 3);
                    return expression;
                }
            } else if (operator == VRuleSet.SUBTRACT) {
                if (dimensions == 2) {
                    double[] expression = new double[2];
                    expression[0] = Utility.round(vectorLeft[0] - vectorRight[0], 3);
                    expression[1] = Utility.round(vectorLeft[1] - vectorRight[1], 3);
                    return expression;
                } else if (dimensions == 3) {
                    double[] expression = new double[3];
                    expression[0] = Utility.round(vectorLeft[0] - vectorRight[0], 3);
                    expression[1] = Utility.round(vectorLeft[1] - vectorRight[1], 3);
                    expression[2] = Utility.round(vectorLeft[2] - vectorRight[2], 3);
                    return expression;
                }
            }
        }
        return null;
    }


    /**
     * The parameter vectors should be set up so that each vector is in it's own column
     * for example if the vectors are 2D vectors the first vector's x co-ordinate should be stored in vectors[0][0]
     * the y co-ordinate should be stored in vectors[0][1]
     * for the second vector the x co-ordinate should be stored in vectors[1][0]
     * the y co-ordinate for the second vector should be stored in vectors[1][1]
     *
     * @param vectorLeft  is a 2D array that holds the 1 vector that we are trying to find the dot product of
     * @param vectorRight is a 2D array that holds the 2 vectors that we are trying to find the dot product of
     * @return will return the answer as a double or if it can't calculate it returns null
     */
    public static double calculateDotProduct(double[] vectorLeft, double[] vectorRight) {

/*        //first solution
        //can handle 1D, 2D and 3D vectors
        //for 2D vectors
        if (vectorLeft.length == 2){
            double dotProduct = vectorLeft[0] * vectorRight[0] + vectorLeft[1] * vectorRight[1];
            return dotProduct;
        }
        //for 3D vectors
        else if (vectorLeft.length == 3) {
            double dotProduct = vectorLeft[0] * vectorRight[0] + vectorLeft[1] * vectorRight[1] + vectorLeft[2] * vectorRight[2];
            return dotProduct;
        }
        //for 1D vectors basically just multiplication
        else if (vectorLeft.length == 1) {
            double dotProduct = vectorLeft[0] * vectorRight[0];
            return dotProduct;
        }
        //if the vectors[0].length is greater than 3 it would mean dealing with vectors that are 4D or higher
        else{
            return -1;
        }*/


        //second solution
        // can handle vectors no matter how many dimensions the vector has
        // this if is to make sure both vectors will be the same type basically to make sure you are finding the dot product between a 2D vector and another
        // 2D vector not between a 2D vector and a 3D vector
        if (vectorLeft.length == vectorRight.length) {
            //are we dealing with 2D vectors?, 3D vectors?, 4D vectors? so on
            int dimensions = vectorLeft.length;
            //holds the answer
            double dotProduct = 0;
            //this for loop wll be able to do dot products no matter how many dimensions there are
            //loop as many times as here are vectors
            for (int i = 0; i < dimensions; i++) {
                //first time it goes through it will multiply the x co-ordinate aka first dimension of each vector with each other
                //the product is added to the variable dot product
                //second run it will multiply the y co-ordinate aka second dimension of each vector with each other
                //this second product will be added to the dot product
                //for 2D vectors this would be the dot product and the loop would end if the vectors are 3D or higher it would keep looping
                dotProduct = dotProduct + vectorLeft[i] * vectorRight[i];
            }
            //return the answer
            return dotProduct;
        } else {
            return 0;
        }

    }


    /**
     * Cleans up the given expression to render it more human-readable.
     * @param expression The expression the clean up
     * @return The human readable version (note: not machine readable)
     */
    public static ArrayList<Token> cleanupExpressionForReading(ArrayList<Token> expression) {
        ArrayList<Token> newExpression = new ArrayList<Token>();
        for (Token t : expression) {
            newExpression.add(t);
        }
        return newExpression;
    }

    /**
     * the parameter vectors should be set up the same way as it is for dot product
     * each of the two vectors is in it's own column
     * the co-ordinates for the first vector should be as such x is in vectors[0][0]
     * y is in vectors[0][1]  z is in vectors[0][2]
     * for the second vector x is in vectors[1][0]  y is in vectors[1][1]
     * z is in vectors[1][2]
     *
     * @param vectorLeft  is an array that holds 1 vector that we are trying to find the dot product of
     * @param vectorRight is an array that holds 1 vector that we are trying to find the dot product of
     * @return returns the answer as a 1D array of doubles or if it can't calculate it will return null
     */
    public static double[] calculateCrossProduct(double[] vectorLeft, double[] vectorRight) {
        if (vectorRight.length == 3 && vectorRight.length == 3) {
            double[] crossProduct = new double[3];
            crossProduct[0] = vectorLeft[1] * vectorRight[2] - vectorLeft[2] * vectorRight[1];
            crossProduct[1] = vectorLeft[2] * vectorRight[0] - vectorLeft[0] * vectorRight[2];
            crossProduct[2] = vectorLeft[0] * vectorRight[1] - vectorLeft[1] * vectorRight[0];
            return crossProduct;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns the magnitude of a vector given a vector represented as an array of doubles
     *
     * @param vector Vector as an array of doubles
     * @return double Magnitude of the vector
     */
    public static double calculateMagnitude(double[] vector) {
        if (vector.length == 2) {
            return Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
        } else if (vector.length == 3) {
            return Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
        } else {
            throw new IllegalArgumentException("Error: This calculator only supports 2D and 3D vectors.");
        }
    }

    /**
     * STILL NEEDS TO BE TESTED
     * Calculates the scalar equation of a line in vector form in 2D and outputs it to the user
     *
     * @ param point The point on the line
     * @ param direction The direction vector of the line
     * @ return ArrayList<Token> The scalar equation to be output on the screen
     */
    public static ArrayList<Token> calculateScalarEquation(double[] point, double[] direction) {
        ArrayList<Token> output = new ArrayList<Token>();
        //line is in the form [a,b] + t[c,d] where [a,b] is the point and [c,d] is the direction vector
        double a = point[0];
        double b = point[1];
        double c = direction[0];
        double d = direction[1];
        double z = -1 * c * b + d * a;

        if (c == 0 && d == 0) {
            throw new IllegalArgumentException("Error: Not a line!");
        }

        //special case if c = 0
        if (c == 0) {
            output.add(VariableFactory.makeX());
            output.add(new Token("="){});
            output.add(new Number(Utility.round(a, 3)));
            return output;
        }

        //special case if d = 0
        if (d == 0) {
            output.add(VariableFactory.makeY());
            output.add(new Token("="){});
            output.add(new Number(Utility.round(b, 3)));
            return output;
        }

        //Scalar equation is in the form cy - dx + z = 0 , where z = -cb + da

        //for first term
        if (c !=0){
            output.add(new Number(Utility.round(c, 3)));
            output.add(VariableFactory.makeY());
        }

        //for second term
        if (d > 0) {
            output.add(OperatorFactory.makeSubtract());
            output.add(new Number(Utility.round(Math.abs(d), 3)));
        } else if (d < 0) {
            if (c != 0) {
                output.add(OperatorFactory.makeAdd());
            }
            output.add(new Number(Utility.round(Math.abs(d), 3)));
        }
        if (d != 0) {
            output.add(VariableFactory.makeX());
        }

        //for third term
        if (z > 0) {
            output.add(OperatorFactory.makeAdd());
        }
        if (z < 0) {
            output.add(OperatorFactory.makeSubtract());
        }

        if (z != 0) {
            output.add(new Number(Utility.round(Math.abs(z), 3)));
        }

        // = 0
        output.add(new Token("=") {
        });
        output.add(new Number(0));
        return output;

    }

    /**
          * Determines the unit vector of a given vector
          *
          * @param vector The vector.
          * @return ArrayList<Token></Token> The unit vector.
          */
         public static ArrayList<Token> findUnitVector (double[] vector){
             double magnitude = calculateMagnitude(vector);
             if (vector.length == 2){
                 double[] unitVector = new double[2];
                 unitVector[0] = Utility.round(vector[0] / magnitude, 3);
                 unitVector[1] = Utility.round(vector[1] / magnitude, 3);
                 return Utility.convertDoublesToVector(unitVector);
             } else if (vector.length == 3) {
                 double[] unitVector = new double[3];
                 unitVector[0] = Utility.round(vector[0] / magnitude, 3);
                 unitVector[1] = Utility.round(vector[1] / magnitude, 3);
                 unitVector[2] = Utility.round(vector[2] / magnitude, 3);
                 return Utility.convertDoublesToVector(unitVector);
             } else {
                 throw new IllegalArgumentException("Error: This calculator only supports 2D and 3D vectors.");
             }

         }

    /**
     * Returns the argument of a vector. (angle to the X axis)
     *
     * @param vector The vector.
     * @return argument The angle of the vector to the X axis.
     */

    public static double calculateArgument(double[] vector) {
        if (vector.length != 2) {
            throw new IllegalArgumentException("Error: This feature is only usable with 2D vectors.");
        }
        double x = vector[0];
        double y = vector[1];

        if (x == 0) {
            return 90;
        }
        double argument = Utility.round(Math.abs(Math.toDegrees(Math.atan(y / x))), 3);
        return argument;
    }

    /**
     * Finds the value of the function at the given x value.
     *
     * @param function The function to evaluate
     * @param x        The x value to find the function at
     * @return The y value, or -1 if non-existant
     */
    public static double valueAt(ArrayList<Token> function, double x) {
        ArrayList<Token> expression = new ArrayList<Token>();
        //Substitutes all variables with the given x value
        for (Token token : function) {
            if (token instanceof Variable && ((Variable) token).getType() == Variable.X) {
                expression.add(new Number(x));
            } else {
                expression.add(token);
            }
        }
        try {
            double y = Utility.evaluateExpression(Utility.convertToReversePolish(expression));
            return y;
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Calculates the quadrant that the vector is in. Only works in 2D.
     *
     * @param vector The vector.
     * @return int The quadrant.
     */
    public static int calculateQuadrant(double[] vector) {
        if (vector.length != 2) {
            throw new IllegalArgumentException("Error: This feature is only usable with 2D vectors.");
        }
        double x = vector[0];
        double y = vector[1];

        //Quadrant 1
        if (x > 0d && y > 0d) {
            return 1;
        }
        //Quadrant 2
        if (x < 0 && y > 0) {
            return 2;
        }
        //Quadrant 3
        if (x < 0 && y < 0) {
            return 3;
        }
        //Quadrant 4
        if (x > 0 && y < 0){
            return 4;
        }
        //vector lies on positive y axis
        if (x == 0 && y > 0) {
            return -1;
        }
        //vector lies on positive x axis
        if (x > 0 && y == 0) {
            return -2;
        }
        //vector lies on negative y axis
        if (x == 0 && y < 0) {
            return -3;
        }
        //vector lies on negative x axis
        if (x < 0 && y == 0) {
            return -4;
        }
        return -100;
    }

    /**
     * Calculates the direction of the vector using true bearings. Only works in 2D.
     *
     * @param vector The vector.
     * @return double The direction in true bearings
     */
    public static double calculateTrueBearing(double[] vector) {
        if (vector.length != 2) {
            throw new IllegalArgumentException("Error: This feature is only usable with 2D vectors.");
        }

        int quadrant = calculateQuadrant(vector);
        double trueBearing = -1;
        //Returns angles when lying on an axis
        if (quadrant == -1) { //positive y axis
            trueBearing = 0;
        } else if (quadrant == -2) { //positive x axis
            trueBearing = 90;
        } else if (quadrant == -3) { //negative y axis
            trueBearing = 180;
        } else if (quadrant == -4) { //negative x axis
            trueBearing = 270;
        }

        //Returns angles that do not lie on an axis
        if (quadrant == 1) {
            trueBearing = 90 - calculateArgument(vector);
        }
        if (quadrant == 2) {
            trueBearing = 270 + calculateArgument(vector);
        }
        if (quadrant == 3) {
            trueBearing = 180 + (90 - calculateArgument(vector));
        }
        if (quadrant == 4) {
            trueBearing = 90 + calculateArgument(vector);
        }
        return trueBearing;
    }

    /**
     * Returns the direction of a vector in bearing form. Only works with 2D vectors.
     *
     * @param vector The vector.
     * @return ArrayList<Token> The direction.
     */
    public static ArrayList<Token> calculateBearing(double[] vector) {
        double angle = calculateArgument(vector);
        int quadrant = calculateQuadrant(vector);
        ArrayList<Token> output = new ArrayList<Token>();

        //returns vectors that lie on an axis
        if (quadrant == -1) { //positive y axis
            output.add(new Token("N") {
            });
            output.add(new Number(0));
            output.add(new Token ("°"){});
            output.add(new Token("E"){});
        }
        if (quadrant == -2) { //positive x axis
            output.add(new Token("E") {
            });
            output.add(new Number(0));
            output.add(new Token ("°"){});
            output.add(new Token("N"){});
        }
        if (quadrant == -3) { //negative y axis
            output.add(new Token("S") {
            });
            output.add(new Number(0));
            output.add(new Token ("°"){});
            output.add(new Token("W"){});
        }
        if (quadrant == -4) { //negative x axis
            output.add(new Token("W") {
            });
            output.add(new Number(0));
            output.add(new Token ("°"){});
            output.add(new Token("S"){});
        }

        //returns vectors that do not lie on an axis
        if (quadrant == 1){
            output.add(new Token("E"){});
            output.add(new Number(Utility.round(angle, 3)));
            output.add(new Token ("°"){});
            output.add(new Token("N"){});
        }
        if (quadrant == 2){
            output.add(new Token("W"){});
            output.add(new Number(Utility.round(angle, 3)));
            output.add(new Token ("°"){});
            output.add(new Token("N"){});
        }
        if (quadrant == 3){
            output.add(new Token("W"){});
            output.add(new Number(Utility.round(angle, 3)));
            output.add(new Token ("°"){});
            output.add(new Token("S"){});
        }
        if (quadrant == 4){
            output.add(new Token("E"){});
            output.add(new Number(Utility.round(angle, 3)));
            output.add(new Token ("°"){});
            output.add(new Token("S"){});
        }
        return output;
    }

    /**
     * Returns a new vector represented as an array of doubles by multipliying the old vector by a
     * multiplier
     *
     * @param multiplier Multiplier to be multiplied into a vector
     * @param vector     Vector represented as an array of doubles
     * @return double[] New vector
     */
    public static double[] multiplyVector(double multiplier, double[] vector) {
        int dimensions = 0;
        if (vector.length == 2) {
            dimensions = 2;
        } else if (vector.length == 3) {
            dimensions = 3;
        }
        double[] newVector = new double [dimensions];
        newVector[0] = Utility.round(multiplier * vector[0], 3);
        newVector[1] = Utility.round(multiplier * vector[1], 3);
        if (dimensions > 2) {
            newVector[2] = Utility.round(multiplier * vector[2], 3);
        }
        return newVector;
    }

/*    *//**
     *
     * @param vector Vector in an array of doubles who's unit vector is being found
     * @return ArrayList<Token> The unit vector
     *//*
    public static ArrayList<Token> findUnitVector (double[] vector) {
        ArrayList<Token> tempExpression = new ArrayList<Token>();
        tempExpression.add(new Number (1/Utility.calculateMagnitude(vector)));
        tempExpression.addAll(Utility.convertDoublesToVector(vector));
        return tempExpression;
    }*/

    /**
     * Finds the angle between 2 vectors represent
     *
     * @param leftVector
     * @param rightVector
     * @return double Angle between 2 vectors
     */
    public static double findAngleBetweenVector(double[] leftVector, double[] rightVector) {
        return (Math.acos((Utility.calculateDotProduct(leftVector, rightVector))
                / (Utility.calculateMagnitude(leftVector) * Utility.calculateMagnitude(rightVector)))) * (180 / Math.PI);

    }

    /**
     *
     * @param leftVector
     * @param rightVector
     * @return ArrayList<Token>
     */
    public static ArrayList<Token> findProjection(double[] leftVector, double[] rightVector) {
        ArrayList<Token> tempTokens = new ArrayList<Token>();
        ArrayList<Token> tempVector = new ArrayList<Token>();
        tempTokens.add(new Number(Utility.round((calculateDotProduct(rightVector, leftVector) / Math.pow(calculateMagnitude(leftVector), 2)), 3)));
        tempTokens.addAll(convertDoublesToVector(leftVector));
        return tempTokens;
    }

    /**
     * Finds the roots of any given function, if any
     *
     * @param function The function to finds the roots of
     * @return A list of roots for the function
     */
    public static ArrayList<Double> findRoots(ArrayList<Token> function) {
        return null;
    }

    /**
     * Finds the factorial of the given integer
     *
     * @param n The base of the factorial
     * @return The value of the factorial
     */

    public static int factorial(int n) {
        if (n == 1) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }


    /**
     * Rounds the given double to the given amount of significant digits.
     *
     * @param unrounded The unrounded value
     * @param sigDigs   The amount of significant digits to round to
     * @return The rounded value
     */
    public static double round(double unrounded, int sigDigs) {
        BigDecimal rounded = new BigDecimal(unrounded);
        return rounded.round(new MathContext(sigDigs)).doubleValue();
    }


    /**
     * Adds any missing end brackets to the expression.
     *
     * @param expression The expression that may have missing brackets
     * @return The expression with all the missing brackets added to the end
     */
    public static ArrayList<Token> addMissingBrackets(ArrayList<Token> expression) {
        int bracketCount = 0;
        ArrayList<Token> newExpression = new ArrayList<Token>();
        //Counts brackets
        for (Token t : expression) {
            newExpression.add(t);
            if (t instanceof Bracket) {
                Bracket b = (Bracket) t;
                if (b.getType() == Bracket.OPEN) {
                    bracketCount++;
                } else if (b.getType() == Bracket.CLOSE) {
                    bracketCount--;
                }
            }
        }
        //Adds missing brackets
        for (int i = bracketCount; i > 0; i--) {
            newExpression.add(BracketFactory.makeCloseBracket());
        }
        return newExpression;
    }

    /**
     * Finds all the REAL roots of a quadratic function
     *
     * @param a the coefficient of the 2nd degree x value of the cubic function
     * @param b the coefficient of the 1st degree x value of the cubic function
     * @param c the constant value of the cubic function
     * @return An ArrayList (of Double objects) containing the real roots of the function
     */
    public static ArrayList<Double> solveQuadratic(double a, double b, double c) {
        ArrayList<Double> roots = new ArrayList<Double>();
        if ((b * b - 4 * a * c) < 0) {
        } else if ((b * b - 4 * a * c) == 0) {
            roots.add(-b / (2 * a));
        } else {
            roots.add((-b + Math.sqrt((b * b - 4 * a * c))) / (2 * a));
            roots.add((-b - Math.sqrt((b * b - 4 * a * c))) / (2 * a));
        }
        return roots;
    }

    /**
     * Finds all the roots of a quadratic function
     *
     * @param a the coefficient of the 2nd degree x value of the cubic function
     * @param b the coefficient of the 1st degree x value of the cubic function
     * @param c the constant value of the cubic function
     * @return An ArrayList (of Complex objects) containing the roots of the function
     */
    public static ArrayList<Complex> solveQuadraticC(double a, double b, double c) {
        ArrayList<Complex> roots = new ArrayList<Complex>();
        if ((b * b - 4 * a * c) < 0) {
            roots.add((Complex.sqrt((b * b - 4 * a * c))).add(-b).times(1 / (2 * a)));
            roots.add(new Complex(-b, 0).subtract(Complex.sqrt((b * b - 4 * a * c))).times(1 / (2 * a)));
        } else if ((b * b - 4 * a * c) == 0) {
            roots.add(new Complex((-b / (2 * a)), 0));
        } else {
            roots.add(new Complex((-b + Math.sqrt((b * b - 4 * a * c))) / (2 * a), 0));
            roots.add(new Complex((-b - Math.sqrt((b * b - 4 * a * c))) / (2 * a), 0));
        }
        return roots;
    }

    /**
     * Finds all the REAL roots of a cubic function
     * using the method found here: http://www.1728.org/cubic2.htm
     *
     * @param a the coefficient of the 3rd degree x value of the cubic function
     * @param b the coefficient of the 2nd degree x value of the cubic function
     * @param c the coefficient of the 1st degree x value of the cubic function
     * @param d the constant value of the cubic function
     * @return An ArrayList (of Double objects) containing the real roots of the function
     */
    public static ArrayList<Double> solveCubic(double a, double b, double c, double d) {
        double f = ((3 * c / a) - ((b * b) / (a * a))) / 3;
        double g = ((2 * Math.pow(b, 3) / Math.pow(a, 3)) - (9 * b * c / (a * a)) + (27 * d / a)) / 27;
        double h = ((g * g / 4) + (f * f * f / 27));
        ArrayList<Double> roots = new ArrayList<Double>();
        if (h > 0) { //only one real root exists
            double s = Math.cbrt((-g / 2) + Math.sqrt(h));
            double u = Math.cbrt((-g / 2) - Math.sqrt(h));
            roots.add((s + u) - (b / (3 * a)));
        } else if (f == 0 && g == 0 && h == 0) {//all 3 roots are real and equal
            roots.add((-1) * Math.cbrt(d / a));
        } else if (h <= 0) {//all 3 roots are real
            double i = Math.sqrt((g * g / 4) - h);
            double j = Math.cbrt(i);
            double k = Math.acos(-1 * (g / (2 * i)));
            double m = Math.cos(k / 3);
            double n = Math.sqrt(3) * Math.sin(k / 3);
            double p = (b / (3 * a)) * (-1);
            roots.add((2 * j * m) + p);
            roots.add(((-1) * j * (m + n)) + p);
            roots.add(((-1) * j * (m - n)) + p);
        }

        return roots;
    }

    /**
     * Finds the derivative of a given function
     *
     * @param function The function that will be differentiated
     * @return The differentiated function
     */
    public ArrayList<Token> differentiate(ArrayList<Token> function) {
        for (int i = 0; i < function.size(); i++) {

        }
        return null;
    }

    /**
     * Finds the integral of a given function
     *
     * @param function The function that will be integrated
     * @return The integrated function
     */
    public ArrayList<Token> integrate(ArrayList<Token> function) {
        for (int i = 0; i < function.size(); i++) {

        }
        return null;
    }

}
