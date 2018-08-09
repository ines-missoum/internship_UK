package com.bt.logfilesAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author Inès MISSOUM BENZIANE
 *
 */
public class CreationCsvFile {

	/** list of all the regEx of the file of regular expressions (line
	 * format : NAME regEx cf. RegExp class) **/
	private ArrayList<String> regExps;
	/** list of all the types recorded **/
	private ArrayList<Type> types;
	/** log file (separators allowed : ' '  ',' ';' '|' )**/
	private static String logfile = "C:\\Users\\Inès MISSOUM\\Desktop\\internship_missoum\\syslog.txt"; // TO CHANGE
	/** regular expressions file (all the elements are
	 * separated with a space)**/
	private static String RegExpFile = "C:\\Users\\Inès MISSOUM\\Desktop\\internship_missoum\\expandedREDefs.txt"; // TO
																													// CHANGE

	public CreationCsvFile() {
		super();
		this.regExps = new ArrayList<String>();
		this.types = new ArrayList<Type>();
	}

	public ArrayList<String> getRegExps() {
		return regExps;
	}

	public ArrayList<Type> getTypes() {
		return types;
	}

	public static String getLogfile() {
		return logfile;
	}

	public static String getRegexpfile() {
		return RegExpFile;
	}

	/**
	 * build the csv pattern (cf. Type class) of the field parameter (ex:
	 * 1,1,0,0,1,0) 
	 * 
	 * @param field
	 *            a field of the log file
	 * @param regExps
	 *            list of regular expressions (from the regular expressions file) used to create the csv pattern
	 * @return a String pattern : a list of 0 and 1, 1 if it matches , 0 else (ex: 1,1,0,0,1,0)
	 */
	public static String getPattern(String field, ArrayList<String> regExps) {

		String pattern = "";
		for (String s : regExps) {

			if (field.matches(s))
				pattern += " ,1";
			else
				pattern += " ,0";
		}
		return pattern;
	}

	/**
	 * takes an expanded file of regular expressions and a log file, and output a CSV file for lattice production (each
	 * element is separated with a comma so we can use this csv file in the
	 * creationLattice package)
	 * 
	 * @throws IOException FileNotFoundException
	 */
	public void run() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(RegExpFile));/* file of regular expressions */
		BufferedReader br2 = new BufferedReader(new FileReader(logfile));/* logfile */

		/* collect names of regEx */

		String regExNames = "";
		String line;
		RegExp rg;

		try {
			line = br.readLine();
			while (line != null) {

				if (line.length() != 0) {
					rg = new RegExp(line);
					regExNames += "," + rg.getName();// each element of the csv file is separated with a comma
					regExps.add(rg.getExpr());// keep the regExp to build the next elements of the csv file
				}

				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			br.close();
			regExNames += "\n";
		}

		/* creation of the csv file */

		FileWriter file = new FileWriter("lattice.csv");
		String lineCSV;
		String line_logfile;
		String pattern = "";

		try {
			file.append(regExNames);/* creation of the first line of the csv file (names of regExp) */

			line_logfile = br2.readLine();
			while (line_logfile != null) {

				String[] parts = line_logfile.split("[, ;|]");// separators allowed
				Type type;

				for (int i = 0; i < parts.length; i++) {
					/* We analyze each part of the logfile: */
					pattern = getPattern(parts[i], regExps);

					lineCSV = parts[i] + pattern;

					/*
					 * we only create a line per type in the csv file so we check if this type was
					 * already found
					 */
					type = Type.getType(pattern, types);

					if (type.getExamples().size() == 0) {/* if new type */
						types.add(type);
						type.setReference(parts[i]);
						lineCSV += " \n";
						file.append(lineCSV);
						// System.out.println(parts[i]);
					}
					type.addExample(parts[i]);/* permit to count the number of examples */
				}
				line_logfile = br2.readLine();
			}
			System.out.println("csv file created");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file.close();
			br2.close();
		}

	}

	/**creates the lattice**/
	public static void main(String[] args) throws IOException {

		CreationCsvFile csvFile = new CreationCsvFile();
		csvFile.run();

	}
}
