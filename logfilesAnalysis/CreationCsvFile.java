package logfilesAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CreationCsvFile {

	public static void main(String[] args) throws IOException {

		/* collect names of regEx */
		String regExNames="";
		
		BufferedReader br = new BufferedReader(new FileReader("regExpExamples.txt"));
		try {

			String line = br.readLine();

			while (line != null) {
	
				line = br.readLine();
			}

		} finally {
			br.close();
		}
		
		/*creation of the csv file*/
		
		FileWriter file = new FileWriter("lattice.csv");
		try {
			file.append(regExNames);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			file.close();
		}
	}
}
