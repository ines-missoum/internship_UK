package logfilesAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CreationCsvFile {

	public static void main(String[] args) throws IOException {
		/**
		 * Java software that take: - an expanded file of regular expressions (line
		 * format : NAME regEx => cf. RegExp class) - a logfile (all the elements are separated with a
		 * space) and output a CSV file for lattice production (each element is
		 * separated with a comma so we can use this csv file in the creationLattice
		 * package)
		 **/
		BufferedReader br = new BufferedReader(
				new FileReader("petit_test.txt"));/* file of regular expressions ( expandedREDefs.txt) */
		BufferedReader br2 = new BufferedReader(new FileReader("petit_test_log.txt"));/* logfile */

		/** collect names of regEx **/

		String regExNames = "";
		String line;
		RegExp rg;
		ArrayList<String> regExps = new ArrayList<String>();// list of all the regEx of the file of regular expressions

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

		/** creation of the csv file **/

		FileWriter file = new FileWriter("lattice.csv");
		ArrayList<Type> types = new ArrayList<Type>();//list of all the types recorded
		String lineCSV;
		String line_logfile;
		String pattern = "";

		try {
			file.append(regExNames);/* creation of the first line of the csv file (names of regExp) */

			line_logfile = br2.readLine();
			while (line_logfile != null) {
				
				String[] parts = line_logfile.split("[, ]");// all the elements of the logfile are separated with a
															// space
				Type type;

				for (int i = 0; i < parts.length; i++) {
					/* We analyze each part of the logfile: */
					pattern = "";

					for (String s : regExps) {

						if (parts[i].matches(s)) {
							pattern += " ,1";
						} else {
							pattern += " ,0";
						}
					}

					lineCSV = parts[i] + pattern;

					/*
					 * we only create a line per type in the csv file so we check if this type was
					 * already found
					 */
					type = Type.getType(pattern, types);

					if (type.getNumberOfExamples() == 0) {/* if new type */
						types.add(type);
						lineCSV += " \n";
						file.append(lineCSV);
						System.out.println(parts[i]);
					}
					type.addExample();/* permit to count the number of examples */
				}
				line_logfile = br2.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file.close();
			br2.close();
		}
	}
}
