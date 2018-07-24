package com.bt.logfilesAnalysis;

import java.util.ArrayList;

public class Type {

	private String pattern;
	private String reference;//one of the examples
	private ArrayList<String> examples; // list of the (different) examples that have the same pattern

	

	public Type(String pattern) {
		super();
		this.pattern = pattern;
		this.examples = new ArrayList<String>();
	}
	
	
	public String getReference() {
		return reference;
	}


	public void setReference(String reference) {
		this.reference = reference;
	}


	public String getPattern() {
		return pattern;
	}

	public ArrayList<String> getExamples() {
		return examples;
	}

	public void addExample(String newExample) {

		if (!examples.contains(newExample)) {
			examples.add(newExample);
		}

	}

	public static Type getType(String pattern, ArrayList<Type> listType) {
		/*
		 * It researches a type (from a pattern String) in the list. It returns the type
		 * found or a new type (if not found).
		 */
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
	

}
