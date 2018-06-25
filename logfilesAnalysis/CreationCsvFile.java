package logfilesAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CreationCsvFile {

	/* syslog.txt */

	public static void main(String[] args) throws IOException {

		/* collect names of regEx */
		String regExNames = ";";
		RegExp rg;
		BufferedReader br = new BufferedReader(new FileReader("regExpExamples.txt"));
		try {

			String line = br.readLine();
			while (line != null) {

				if (line.length()!=0) {
					System.out.println(line);
					rg = new RegExp(line);
					regExNames += rg.getName() + ";";
				}
				line = br.readLine();
			}

		} finally {
			br.close();
		}

		/* creation of the csv file */

		FileWriter file = new FileWriter("lattice.csv");

		try {
			file.append(regExNames);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file.append(regExNames);
			file.close();
		}
	}
}
