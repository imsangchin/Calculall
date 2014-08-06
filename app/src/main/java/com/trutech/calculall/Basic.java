package com.trutech.calculall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

/**
 * The activity for the basic calculator mode. The basic mode will only be able to
 * perform the four operations (add, subtract, multiply and divide) including brackets.
 *
 * @version 0.3.0
 */
public class Basic extends Activity {

	public static final int DEGREE = 1, RADIAN = 2, GRADIAN = 3; //Modes for angles
	protected ArrayList<Token> tokens = new ArrayList<Token>(); //Tokens shown on screen
	protected boolean clearOnClick = false;

	//GridView mKeypadGrid;
	//KeypadAdapter mKeypadAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_basic);
	}

	/**
	 * Processes the expression and returns the result using the Shunting Yard Algorithm to convert
	 * the expression into reverse polish and then evaluating it.
	 *
	 * @return The numerical value of the expression
	 * @throws IllegalArgumentException If the user has input a invalid expression
	 */
	protected double process (){
		double unrounded = Utility.evaluateExpression(Utility.convertToReversePolish(Utility.setupExpression(condenseDigits())));
		BigDecimal rounded = new BigDecimal(unrounded);
		return rounded.round(new MathContext(9)).doubleValue();
	} 



	/**
	 * Transforms all the digits into numbers as well as replacing Variables with numbers. 
	 * 
	 * @return The tokens with the digits condensed and variables substituted
	 */
	private ArrayList<Token> condenseDigits(){
		ArrayList<Token> newTokens = new ArrayList<Token>();
		ArrayList<Digit> digits = new ArrayList<Digit>();
		boolean atDigits = false; //Tracks if it's currently tracking digits
		for (Token token : tokens){
			if (atDigits){ //Going through digits
				if (token instanceof Digit){ //Number keeps going
					digits.add((Digit) token);
				}else { //Number ended
					atDigits = false;
					newTokens.add(new Number(Utility.valueOf(digits))); //Adds the sum of all the digits
					digits.clear();
					if (token instanceof Variable){ //Substitutes Variables
						newTokens.add(new Number(((Variable)token).getValue()));
					}else {
						newTokens.add(token);
					}
				}
			}else{ //Not going through digits
				if (token instanceof Digit) { //Start of a number
					atDigits = true;
					digits.add((Digit) token);
				} else if (token instanceof Variable){ //Substitutes Variables
					newTokens.add(new Number(((Variable)token).getValue()));
				} else{ //Not a digit; adds to the new list
					newTokens.add(token);
				}
			}
		}
		if (!digits.isEmpty() && atDigits){ //Digits left
			newTokens.add(new Number (Utility.valueOf(digits)));
		}
		return newTokens;
	}

	/**
	 * When the user presses the 1 Button.
	 * 
	 * @param v Not Used
	 */
	public void clickOne(View v){
		tokens.add(DigitFactory.makeOne());
		updateInput();
	}

	/**
	 * When the user presses the 2 Button.
	 * 
	 * @param v Not Used
	 */
	public void clickTwo(View v){
		tokens.add(DigitFactory.makeTwo());
		updateInput();
	}

	/**
	 * When the user presses the 3 Button.
	 * 
	 * @param v Not Used
	 */
	public void clickThree(View v){
		tokens.add(DigitFactory.makeThree());
		updateInput();
	}

	/**
	 * When the user presses the 4 Button.
	 * 
	 * @param v Not Used
	 */
	public void clickFour(View v){
		tokens.add(DigitFactory.makeFour());
		updateInput();
	}

	/**
	 * When the user presses the 5 Button.
	 * 
	 * @param v Not Used
	 */
	public void clickFive(View v){
		tokens.add(DigitFactory.makeFive());
		updateInput();
	}

	/**
	 * When the user presses the 6 Button.
	 * 
	 * @param v Not Used
	 */
	public void clickSix(View v){
		tokens.add(DigitFactory.makeSix());
		updateInput();
	}

	/**
	 * When the user presses the 7 Button.
	 * 
	 * @param v Not Used
	 */
	public void clickSeven(View v){
		tokens.add(DigitFactory.makeSeven());
		updateInput();
	}

	/**
	 * When the user presses the 8 Button.
	 * 
	 * @param v Not Used
	 */
	public void clickEight(View v){
		tokens.add(DigitFactory.makeEight());
		updateInput();
	}

	/**
	 * When the user presses the 9 Button.
	 * 
	 * @param v Not Used
	 */
	public void clickNine(View v){
		tokens.add(DigitFactory.makeNine());
		updateInput();
	}

	/**
	 * When the user presses the 0 Button.
	 * 
	 * @param v Not Used
	 */
	public void clickZero(View v){
		tokens.add(DigitFactory.makeZero());
		updateInput();
	}

	/**
	 * When the user presses the . Button.
	 * 
	 * @param v Not Used
	 */
	public void clickDecimal(View v){
		tokens.add(DigitFactory.makeDecimal());
		updateInput();
	}

	/**
	 * When the user presses the x Button.
	 * 
	 * @param v Not Used
	 */
	public void clickMultiply(View v){
		tokens.add(OperatorFactory.makeMultiply());
		updateInput();
	}

	/**
	 * When the user presses the / Button.
	 * 
	 * @param v Not Used
	 */
	public void clickDivide(View v){
		tokens.add(OperatorFactory.makeDivide());
		updateInput();
	}

	/**
	 * When the user presses the + Button.
	 * 
	 * @param v Not Used
	 */
	public void clickAdd(View v){
		tokens.add(OperatorFactory.makeAdd());
		updateInput();
	}

	/**
	 * When the user presses the - Button.
	 * 
	 * @param v Not Used
	 */
	public void clickSubtract(View v){
		tokens.add(OperatorFactory.makeSubtract());
		updateInput();
	}

	/**
	 * When the user presses the sqrt Button.
	 * 
	 * @param v Not Used
	 */
	public void clickSqrt(View v){
		tokens.add(FunctionFactory.makeSqrt());
		updateInput();
	}


	/**
	 * When the user presses the clear Button.
	 * 
	 * @param v Not Used
	 */
	public void clickClear(View v){
		tokens.clear();
		updateInput();
		TextView output = (TextView) findViewById(R.id.txtStack);
		output.setText(""); //Clears stack
	}

	/**
	 * When the user presses the back Button.
	 * 
	 * @param v Not Used
	 */
	public void clickBack(View v){
		if (tokens.isEmpty()){
			return; //Prevents a bug
		}
		tokens.remove(tokens.size() - 1); //Removes last token
		updateInput();
	}

	/**
	 * When the user presses the negative Button.
	 * 
	 * @param v Not Used
	 */
	public void clickNegative(View v){
		tokens.add(DigitFactory.makeNegative());
		updateInput();
	}

	/**
	 * When the user presses the equals Button.
	 * 
	 * @param v Not Used
	 */
	public void clickEquals(View v){
		TextView output = (TextView) findViewById(R.id.txtStack);
		try{
			String s = Double.toString(process());
			s = s.indexOf(".") < 0  ? s : (s.indexOf("E")>0 ? s.substring(0,s.indexOf("E")).replaceAll("0*$", "")
					.replaceAll("\\.$", "").concat(s.substring(s.indexOf("E"))) : s.replaceAll("0*$", "")
					.replaceAll("\\.$", "")); //Removes trailing zeroes
			output.setText(s);
		}catch (Exception e){ //User did a mistake
			Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
		}
		ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
		scrollView.pageScroll(ScrollView.FOCUS_DOWN);
	}

	/**
	 * When the user wants to change to Basic Mode.
	 * 
	 * @param v Not Used
	 */
	public void clickBasic(View v){
		//Goes to the Basic activity
		Intent intent = new Intent(this, Basic.class);
		startActivity(intent);
	}

	/**
	 * When the user wants to change to Advanced Mode.
	 * 
	 * @param v Not Used
	 */
	public void clickAdvanced(View v){
		//Goes to the Advanced activity
		Intent intent = new Intent(this, Advanced.class);
		startActivity(intent);
	}

	/**
	 * When the user wants to change to Function Mode.
	 * 
	 * @param v Not Used
	 */
	public void clickFunction(View v){
		//Goes to the FunctionMode activity
		Intent intent = new Intent(this, FunctionMode.class);
		startActivity(intent);
	}

	/**
	 * When the user wants to change to Vector Mode.
	 * 
	 * @param v Not Used
	 */
	public void clickVector(View v){
	}

	/**
	 * Updates the text on the input screen.
	 */
	protected void updateInput(){
		String inputText = "";
		TextView input = (TextView) findViewById(R.id.txtInput);
		for (Token token : tokens){
			inputText += token.getSymbol();
		}
		input.setText(inputText);
		//Shows bottom
        if (this instanceof Advanced) {
            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
            scrollView.pageScroll(ScrollView.FOCUS_DOWN);
        }
	}


}
