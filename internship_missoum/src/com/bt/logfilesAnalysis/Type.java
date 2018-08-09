package com.bt.logfilesAnalysis;

import java.util.ArrayList;

/**
 * 
 * @author Inès MISSOUM BENZIANE
 *
 */
public class Type {

	/** A pattern built as a list of 1, if it matches the regEx, and 0, if it doesn't (ex : 1,0,0,1,1,1)**/ 
	private String pattern;
	/**list of the (different) examples of the pattern**/
	private ArrayList<String> examples; 
	/** one of the examples, the one that we take to create the lattice**/
	private String reference;
	

	/**
	 * Create a new type from a pattern with no examples and no reference 
	 * @param pattern (ex : 1,0,0,1,1,1)
	 */
	public Type(String pattern) {
		this.pattern = pattern;
		this.examples = new ArrayList<String>();
	}

	/**
	 * 
	 * @return the reference of the Type
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * 
	 * @param reference one of the examples, the one that we take to create the lattice
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * 
	 * @return the pattern string of the type (a list of 1, if it matches the regEx, and 0, if it doesn't (ex : 1,0,0,1,1,1)) 
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * 
	 * @return list of the (different) examples of the pattern 
	 */
	public ArrayList<String> getExamples() {
		return examples;
	}

	/**
	 * add the newExample in the pattern's examples list if it's not already in it, else do nothing.
	 * @param newExample example to add in the pattern's examples list
	 */
	public void addExample(String newExample) {

		if (!examples.contains(newExample)) {
			examples.add(newExample);
		}

	}

	/**
	 * It researches a type (from a pattern String) in the list. It returns the type
	 * found or a new type (if not found).
	 * @param pattern pattern String researched in listType
	 * @param listType
	 * @return the type found or a new type (if not found).
	 */
	public static Type getType(String pattern, ArrayList<Type> listType) {

		boolean found = false;
		int i = 0;
		Type type = new Type(pattern);
		if (listType.size() != 0) {

			while (!found & i < listType.size()) {
				if (listType.get(i).getPattern().equals(pattern)) {
					found = true;
					type = listType.get(i);
				}
				i++;
			}
		}
		return type;

	}

	/**
	 * finds the reference of the field
	 * @param field an example of the logfile (String)
	 * @param types a list of Types (among which the research is made)
	 * @return the reference of the type that contains the field in its list of examples or an empty String (if a such type doesn't exist)
	 */
	public static String findReference(String field, ArrayList<Type> types) {

		boolean found = false;
		int i = 0;
		String ref = "";

		while (!found && i < types.size()) {

			found = field.equals(types.get(i).reference);// if the field is a reference of a type
			if (!found)
				found = types.get(i).getExamples().contains(field);// else
			i++;
		}

		if (found)
			ref = types.get(i - 1).reference;

		return ref;
	}

}
