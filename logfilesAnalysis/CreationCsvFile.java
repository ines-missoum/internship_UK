package logfilesAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CreationCsvFile {

	public static void main(String[] args) throws IOException {

		ArrayList<String> names = new ArrayList<String>();

		/* collect names of regEx */
		String regExNames = ";";
		String line;
		RegExp rg;
		BufferedReader br = new BufferedReader(new FileReader("regExpExamples.txt"));

		try {
			line = br.readLine();
			while (line != null) {

				if (line.length() != 0) {
					rg = new RegExp(line);
					regExNames += rg.getName() + ";";
					names.add(rg.getName());// keep the names to build the next elmts of the csv file
				}

				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			br.close();
			regExNames+="\n";
		}

		/* creation of the csv file */

		FileWriter file = new FileWriter("lattice.csv");
		BufferedReader br2 = new BufferedReader(new FileReader("syslog.txt"));
		String lineCSV;
		try {
			file.append(regExNames);

			line = br2.readLine(); /*for the first line of the logfile*/
			String[] parts = line.split("[:| ]");
			
			for( int i=0; i<parts.length;i++) {
				lineCSV=parts[i]+"; \n";
				file.append(lineCSV);
			}
			
				/*build the line*/
	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file.close();
			br2.close();
		}
	}
}
