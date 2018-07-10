package com.bt.context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Context
{
	private double[][] table; // values
	private Map<String, Integer> objects; // all the objects. quickly gives object -> index
	private Map<String, Integer> attributes; // all the attributes. quickly gives attribute -> index
	private String[] objectsArray; // all the attributes. quickly gives index -> attribute
	private String[] attributesArray; // all the objects. quickly gives index -> object

	/**
	 * reads the given csv and build the corresponding context
	 * O(n), n being the length of the file
	 * 
	 * @param csv
	 * @throws IOException
	 */
	public Context(File csv) throws IOException
	{
		read(csv);
	}

	/**
	 * returns the array containing the attributes names
	 * O(1)
	 * 
	 * @return
	 */
	public String[] getAttributes()
	{
		return  attributesArray;
	}

	/**
	 * returns the Set of all the attributes in this context
	 * O(1)
	 * 
	 * @return
	 */
	public Set<String> getAttributesSet()
	{
		return attributes.keySet();
	}

	/**
	 * returns the highest attribute value for the given object index
	 * O(attributesArray.lenght)
	 * 
	 * @param object
	 * @return
	 */
	public double getMaxAttributeValue(int object)
	{
		double max = 0;

		for(double v : table[object])
			if(v > max)
				max = v;

		return max;
	}

	/**
	 * returns the array containing the objects names
	 * O(1)
	 * 
	 * @return
	 */
	public String[] getObjects()
	{
		return objectsArray;
	}

	/**
	 * returns the Set of all the objects in this context
	 * O(1)
	 * 
	 * @return
	 */
	public Set<String> getObjectsSet()
	{
		return objects.keySet();
	}

	/**
	 * returns the value of the given attribute index for the given object index
	 * O(1)
	 * 
	 * @param object
	 * @param attribute
	 * @return
	 */
	public double getValue(int object, int attribute)
	{
		return table[object][attribute];
	}

	/**
	 * returns the value of the given attribute for the given object
	 * O(1)
	 * 
	 * @param object
	 * @param attribute
	 * @return
	 */
	public double getValue(String object, String attribute)
	{
		return table[objects.get(object)][attributes.get(attribute)];
	}

	/**
	 * returns the index of the given attribute
	 * O(1)
	 * 
	 * @param attribute
	 * @return
	 */
	public int indexOfAttribute(String attribute)
	{
		return attributes.get(attribute);
	}

	/**
	 * returns the index of the given object
	 * O(1)
	 * 
	 * @param object
	 * @return
	 */
	public int indexOfObject(String object)
	{
		return objects.get(object);
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();

		for(String o : getObjectsSet())
		{
			b.append(o).append(':');
			for(String a : getAttributesSet())
				b.append(' ').append(a).append('=').append(getValue(o, a));
			b.append('\n');
		}

		return b.toString();
	}

	private void read(File csv) throws IOException
	{
		String line;
		int currentLine = 0, attCount = 0, totalLineCount = getLineCount(csv) - 1;
		LineNumberReader ln = new LineNumberReader(new BufferedReader(new FileReader(csv)));

		// reads the line containing all the attributes
		StringTokenizer st = new StringTokenizer(ln.readLine(), ",");
		attributesArray = new String[st.countTokens()];
		while(st.hasMoreTokens())
			attributesArray[attCount++] = st.nextToken();
		// generates the corresponding hashMap
		attributes = new HashMap<String, Integer>(attCount);
		for(int i = 0; i < attributesArray.length; i++)
			attributes.put(attributesArray[i], i);

		//same for the objects
		objectsArray = new String[totalLineCount];
		objects = new HashMap<String, Integer>(totalLineCount);

		table = new double[totalLineCount][attCount];

		// for each line
		while((line = ln.readLine()) != null)
		{
//			System.out.println(			currentLine+"::"+line);
			String[] tokens = line.split(","); // splits the line
			objectsArray[currentLine] = tokens[0];
			objects.put(tokens[0], currentLine); // the first token is the object

			// others are values
			for(int i = 1; i < tokens.length; i++)
				// associates the current value to the corresponding attribute
				table[currentLine][i - 1] = Double.parseDouble(tokens[i]);

			currentLine++;
		}

		ln.close();
	}

	// counts the number of lines in this file. O(n)
	private static int getLineCount(File csv) throws IOException
	{
		LineNumberReader ln = new LineNumberReader(new FileReader(csv));

		while(ln.readLine() != null);

		ln.close();
		return ln.getLineNumber();
	}
}
