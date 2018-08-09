package com.bt.lattice.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;

import com.bt.context.Context;
//import com.bt.lattice.Extent;
import com.bt.lattice.Concept;
import com.bt.lattice.Lattice;

public class ContextToLattice
{
	private String csv;
	private String rootName;
	private final String CSVSUFFIX = ".csv";
	private final String LATSUFFIX = ".lat";
	private long readingTime;
	private long creationTime;
	private long savingTime;
	private long fileTime;
	private static long totalTime;
	private final boolean silentMode=false;

	private  boolean printExtents=false;
	private  boolean printIntents=false;

	public ContextToLattice(String csv)
	{
		if(csv.endsWith(CSVSUFFIX))
		{
			this.csv = csv;
			rootName = csv.substring(0,csv.length() - CSVSUFFIX.length());
		}
		else
		{
			this.csv = csv+CSVSUFFIX;
			rootName = csv;
		}
			
	}

	public ContextToLattice(String csv, boolean printEx, boolean printIn)
	{
		
		printExtents = printEx;
		printIntents = printIn;
		if(csv.endsWith(CSVSUFFIX))
		{
			this.csv = csv;
			rootName = csv.substring(0,csv.length() - CSVSUFFIX.length());
		}
		else
		{
			this.csv = csv+CSVSUFFIX;
			rootName = csv;
		}
			
	}

	
	public int run()
	{
		fileTime = System.currentTimeMillis();

		if(!silentMode)
			System.out.print("reading context...");
		readingTime = System.currentTimeMillis();
		Context c;
		try
		{
			c = new Context(new File(csv));
		}
		catch(FileNotFoundException e)
		{
			return 1;
		}
		catch(IOException e)
		{
			return 2;
		}

		readingTime = System.currentTimeMillis() - readingTime;
		if(!silentMode)
			System.out.println("done (" + c.getObjects().length + " objects and " + c.getAttributes().length + " attributes in " + readingTime / 1000.0 + "s).");

		if(!silentMode)
			System.out.print("creating lattice...");
		creationTime = System.currentTimeMillis();
		Lattice l = new Lattice(c);
		creationTime = System.currentTimeMillis() - creationTime;
		if(!silentMode)
			System.out.println("done (" + l.getConcepts().size() + " concepts in " + creationTime / 1000.0 + "s).");

		if(!silentMode)
			System.out.print("saving...");
		savingTime = System.currentTimeMillis();
		try
		{
			l.save(new File(rootName + LATSUFFIX));
		}
		catch(IOException e)
		{
			return 3;
		}
		savingTime = System.currentTimeMillis() - savingTime;
		if(!silentMode)
			System.out.println("done (" + sizeToString(new File(rootName + LATSUFFIX).length()) + " in " + savingTime / 1000.0 + "s).");

		fileTime = System.currentTimeMillis() - fileTime;
		if(!silentMode)
			System.out.println(csv + " done in " + fileTime / 1000.0 + "s");

		if(printExtents || printIntents)
		{
		 for(Concept e : l.getConcepts())
		 {
			 if(printIntents)
				 System.out.println(e.getIntent());
			 if(printExtents)
				 System.out.println(e.getExtent());
			if(printExtents && printIntents)
				System.out.println("----");
		 }
	}
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		  String theDir =  "/Users/Inès MISSOUM/Desktop/creationLattice/";    

		boolean addDir = false;
		String[] INFILES = {"lattice"}; /* no suffix needed  */
		String[] fileList;
		boolean printIn=true;
		boolean printEx=true;

		if (args.length >0)
		{
			ArrayList<String> fList=new ArrayList<String>();
			for(int i = 0; i < args.length; i++)
			{
				if(args[i].equals("-D") && (++i < args.length))
				{
					addDir = true;
					theDir = args[i];
				}
				else if(args[i].equals("-PrintExtents") )
					printEx = true;
				else if(args[i].equals("-PrintIntents") )
					printIn = true;
				
			
				else

					fList.add(args[i]);
			}		
			fileList = fList.toArray(new String[fList.size()]);
			}
		
		else
		{
			fileList = INFILES;
			addDir = true;
		}	
		totalTime = System.currentTimeMillis();
		for(String a : fileList)
		{
			String f;
			if(addDir) f = theDir+a;
			else
				f=a;
			System.out.println("\nprocessing " + a + "...");
			ContextToLattice c = new ContextToLattice(f+".csv", printEx, printIn);
			switch(c.run())
			{
				case 1:
					System.err.println("file not found.\n");
					break;
				case 2:
					System.err.println("error while loading. Skipping...\n");
					break;
				case 3:
					System.err.println("error while saving.\n");
					break;
				default:
					System.out.println("completed processing of " + a);

			}
		}

		totalTime = System.currentTimeMillis() - totalTime;

		if(fileList.length > 1)
			System.out.println("all files processed in " + totalTime / 1000.0 + "s.");
	}

	private static String sizeToString(long size)
	{
		StringWriter s = new StringWriter();
		PrintWriter p = new PrintWriter(s);

		if(size < 2048)
			p.print(size + " B");
		else if(size < 2048 * 1024)
			p.printf("%.2f kB", size / 1024.0);
		else
			p.printf("%.2f MB", size / (1024.0 * 1024.0));

		return s.toString();
	}
}
