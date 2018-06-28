package logfilesAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CreationCsvFile {

	public static void main(String[] args) throws IOException {

		ArrayList<String> regExps = new ArrayList<String>();

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
			regExNames+="\n";
		}

		/* creation of the csv file */

		FileWriter file = new FileWriter("lattice.csv");
		BufferedReader br2 = new BufferedReader(new FileReader("syslog.txt"));
		String lineCSV;
		String line_logfile;
		try {
			file.append(regExNames);

			line_logfile = br2.readLine(); /*for the first line of the logfile*/
			
			for(int j=0; j<=1; j++) {
				System.out.println(line_logfile);
				line_logfile = br2.readLine();
			}
			System.out.println(line_logfile);
			String[] parts = line_logfile.split("[, ]");//"[:, ]"
					
			for( int i=0; i<parts.length;i++) {
				/*we build the line : */
				lineCSV=parts[i]+";";
				System.out.println(parts[i]);
				for(String s : regExps) {
					
					if (parts[i].matches(s)) {
						lineCSV+=" 1;";
					}else {
						lineCSV+=" 0;";
					}
				}
				lineCSV+=" \n";
				file.append(lineCSV);
			}
	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file.close();
			br2.close();
		}
	}
}
