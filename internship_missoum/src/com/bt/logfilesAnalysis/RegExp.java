package com.bt.logfilesAnalysis;

/**
 * 
 * @author Inès MISSOUM BENZIANE 
 *
 */

public class RegExp {

	/**name of the regEx**/
	private String name;
	/**regular expression**/
	private String expr;

	/**
	 * Create a RegExp (with a name and a regular expression) from a string parameter 
	 * @param regExp a string in the format : NAME EXPRESSION (ie: the name and the regular expression are separated by a space or a tab)
	 */
	public RegExp(String regExp) {

		String[] parts = regExp.split("[\t ]",2);
		this.name = parts[0];
		this.expr = parts[1];

	}

	/**
	 * 
	 * @return the name of the regExp
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return the regular expression of the regExp
	 */
	public String getExpr() {
		return expr;
	}

	
}

