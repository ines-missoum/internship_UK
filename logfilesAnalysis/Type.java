package logfilesAnalysis;

import java.util.ArrayList;

public class Type {

	private String pattern;
	private int numberOfExamples;

	public String getPattern() {
		return pattern;
	}

	public int getNumberOfExamples() {
		return numberOfExamples;
	}

	public Type(String pattern) {
		super();
		this.pattern = pattern;
		this.numberOfExamples = 0;
	}

	public void addExample() {
		this.numberOfExamples++;

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
