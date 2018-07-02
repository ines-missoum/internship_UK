package logfilesAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CreationCsvFile {

	public static void main(String[] args) throws IOException {

		ArrayList<String> regExps = new ArrayList<String>();
		ArrayList<Type> types = new ArrayList<Type>();

		/* collect names of regEx */
		String regExNames = ";";
		String line;
		RegExp rg;
		BufferedReader br = new BufferedReader(new FileReader("expandedREDefs.txt"));

		try {
			line = br.readLine();
			while (line != null) {

				if (line.length() != 0) {
					rg = new RegExp(line);
					regExNames += rg.getName() + ";";
					regExps.add(rg.getExpr());// keep the regExp to build the next elmts of the csv file
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
		BufferedReader br2 = new BufferedReader(new FileReader("syslog.txt"));/* logfile */
		String lineCSV;
		String line_logfile;
		String pattern="";

		try {
			file.append(regExNames);/* creation of the first line of the csv file (names of regExp) */

			/* for all the lines of the logfiles */

			line_logfile = br2.readLine();
			while (line_logfile != null) {
				line_logfile = br2.readLine(); /* for the first line of the logfile */
				
//				 /******** just to choose the line ==> to erase *************/
//				 for (int j = 0; j <= 1; j++) {
				// System.out.println(line_logfile);
				 // line_logfile = br2.readLine();
				// }
				// System.out.println(line_logfile);
				// /*************************************************************/

				String[] parts = line_logfile.split("[, ]");
				Type type;
				for (int i = 0; i < parts.length; i++) {
					/* For each part of the logfile line we build a csv line : */
					pattern="";
					//System.out.println(parts[i]);
					for (String s : regExps) {

						if (parts[i].matches(s)) {
							pattern += " 1;";
						} else {
							pattern += " 0;";
						}
					}
					
					lineCSV = parts[i] + ";"+pattern;

					/* a line per type */
					type = Type.getType(pattern, types);

					if (type.getNumberOfExamples() == 0) {/* new type */
						types.add(type);
						lineCSV += " \n";
						file.append(lineCSV);
						System.out.println(parts[i]);
					}
					type.addExample();
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
