package com.bt.lattice;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.bt.context.Context;

public class Lattice
{
	private List<Concept> concepts;
	private String[] objects;
	private String[] attributes;

	/**
	 * generates the lattice from the given context
	 * 
	 * @param c
	 */
	public Lattice(Context c)
	{
		objects = c.getObjects();
		attributes = c.getAttributes();
		generate(c);
	}

	/**
	 * loads the lattice from the given file
	 * O(f.length())
	 * 
	 * @param f
	 * @throws IOException
	 */
	public Lattice(File f) throws IOException
	{
		load(f);
	}

	public String[] getAttributes()
	{
		return attributes;
	}

	public List<Concept> getConcepts()
	{
		return concepts;
	}

	public Concept getTopConcept()
	{
		return concepts.get(0);
	}

	public String[] getObjects()
	{
		return objects;
	}
	
	public Concept getConceptByIntent(Intent theIntent)
	{
		Concept ret = null;
		for(int i=0; (i < concepts.size()) && (ret == null); i++)
		{
			Concept c = concepts.get(i);
			if(c.getIntent().size() == theIntent.size())
				if(c.getIntent().getElements().containsAll(theIntent.getElements()))
					ret = c;
		}
		return ret;
	}


	/**
	 * saves the lattice in the given file
	 * 
	 * @param f
	 * @throws IOException
	 */
	public void save(File f) throws IOException
	{
		DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));

		stream.writeInt(attributes.length); //write the number of attributes
		for(String a : attributes)
		{
			stream.writeBytes(a); //write the attribute
			stream.writeByte(0); //followed by 0
		}

		//same for objects
		stream.writeInt(objects.length);
		for(String o : objects)
		{
			stream.writeBytes(o);
			stream.writeByte(0);
		}

		stream.writeInt(concepts.size());
		for(Concept concept : concepts)
		{
			// write extent
			stream.writeInt(concept.getExtent().size());
			for(Element e : concept.getExtent().getElements())
			{
				stream.writeInt(e.getIndex());
				stream.writeDouble(e.getMu());
			}
			
			//write intent
			stream.writeInt(concept.getIntent().size());
			for(int i : concept.getIntent().getElements())
				stream.writeInt(i);
		}

		stream.close();
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();

		for(Concept c : concepts)
			b.append(c.getExtent()).append('\n');

		return b.toString();
	}

	public double costToEmpty()		// returns cost of converting this lattice to all empty sets
	{
		double cost=0;

		for(Concept c : concepts)
			if (c != concepts.get(0))			// hack to avoid counting top concept
				cost+= c.getExtent().cardinality();

		return cost;
	}

	/**
	 * returns the set of all the objects that have a value > 0 for the given attribute index
	 * O(extent.size())
	 * 
	 * @param extent
	 * @param attribute
	 * @return
	 */
	private Extent downArrow(Context context, Extent extent, int attribute)
	{
		List<Element> temp = new ArrayList<Element>();

		// for each object
		for(Element e : extent.getElements())
		{
			// if the value of attribute for the current object is > 0, it is added to temp
			double value = context.getValue(e.getIndex(), attribute);
			if(value > 0)
				temp.add(new Element(this, e.getIndex(), Math.min(value, e.getMu())));
		}

		return new Extent(temp);
	}

	private void generate(Context c)
	{
		// extents = new LinkedHashSet<Extent>();
		concepts = new ArrayList<Concept>();
		Concept top = topConcept(c);
		concepts.add(0,top);		// ensure top concept is always added at position zero
		generate(c, top, 0);
	}

	private void generate(Context context, Concept c, int beggining)
	{
		String[] attributes = context.getAttributes();
		// if the intent of the current concept contains all the attributes or if there is no attributes left, we are done
		if(c.getIntent().size() == attributes.length || beggining >= attributes.length)
			return;

		// for each remaining attribute
		for(int i = beggining; i < attributes.length; i++)
			// if the intent of c doesn't contains att
			if(!c.getIntent().contains(i))
			{
				// we find all objects that have att
				Extent downArrow = downArrow(context, c.getExtent(), i);
				// we find the intent corresponding to this new extent
				Intent upArrow = intentOf(context, downArrow);
				// we then find all the attributes of the intent of c that have already been processed
				List<Integer> leftSide = leftSide(c.getIntent(), i);
				// and all the attributes of the new intent that have already been processed
				List<Integer> rightSide = leftSide(upArrow, i);

				// if they are the same
				if(leftSide.equals(rightSide))
				{
					// attempt so free some space
					leftSide = null;
					rightSide = null;

					// we add the downArrow to the extents
					Concept newConcept = new Concept(downArrow, upArrow);
					concepts.add(newConcept);
					// we do the same for the concept compound of the new extent and intent, and we remove att
//					System.out.println("generate : " + beggining + " :: "+ i+1);
					generate(context, newConcept, i + 1);
				}
			}
	}

	/**
	 * return the intent corresponding to the given extent. It contains all the attributes that have a higher value that the mu of every element of the extent
	 * O(extent.size() * attributes.length)
	 * 
	 * @param extent
	 * @return
	 */
	private Intent intentOf(Context context, Extent extent)
	{
		// if extent is empty
		if(extent.isEmpty())
		{
			// returns the set of all the attributes
			List<Integer> r = new ArrayList<Integer>();
			for(int i = 0; i < attributes.length; i++)
				r.add(i);
			return new Intent(r, this);
		}
		else
		{
			boolean allowedAttributes[] = new boolean[attributes.length];
			for(int a = 0; a < attributes.length; a++)
				allowedAttributes[a] = true;

			for(Element e : extent.getElements())
				for(int a = 0; a < allowedAttributes.length; a++)
					if(allowedAttributes[a])
						allowedAttributes[a] = (context.getValue(e.getIndex(), a) >= e.getMu());

			List<Integer> temp = new ArrayList<Integer>();
			for(int a = 0; a < attributes.length; a++)
				if(allowedAttributes[a])
					temp.add(a);

			return new Intent(temp, this);
		}
	}

	// O(intent.size())
	private List<Integer> leftSide(Intent intent, int end)
	{
		List<Integer> r = new ArrayList<Integer>();
		List<Integer> elems = intent.getElements();
		int i = 0;

		while(i < elems.size() && elems.get(i) < end)
			r.add(elems.get(i++));

		return r;
	}

	// O(n)
	private void load(File f) throws IOException
	{
		DataInputStream stream = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));

		loadAttributes(stream);
		loadObjects(stream);
		loadConcepts(stream);

		stream.close();
	}

	// O(n)
	private void loadAttributes(DataInputStream stream) throws IOException
	{
		int size = stream.readInt();
		attributes = new String[size];

		for(int i = 0; i < attributes.length; i++)
			attributes[i] = readString(stream);
	}

	// O(n)
	private Concept loadConcept(DataInputStream stream) throws IOException
	{
		return new Concept(loadExtent(stream), loadIntent(stream));
	}

	// O(n)
	private Extent loadExtent(DataInputStream stream) throws IOException
	{
		int elementCount = stream.readInt();
		// Set<Element> extent = new LinkedHashSet<Element>(elementCount);
		List<Element> extent = new ArrayList<Element>(elementCount);

		for(int i = 0; i < elementCount; i++)
			extent.add(new Element(this, stream.readInt(), stream.readDouble()));
		
		return new Extent(extent);
	}

	// O(n)
	private Intent loadIntent(DataInputStream stream) throws IOException
	{
		int elementCount = stream.readInt();
		// Set<Element> extent = new LinkedHashSet<Element>(elementCount);
		List<Integer> intent = new ArrayList<Integer>(elementCount);

		for(int i = 0; i < elementCount; i++)
			intent.add(stream.readInt());
		
		return new Intent(intent, this);
	}

	// O(n)
	private void loadConcepts(DataInputStream stream) throws IOException
	{
		int conceptCount = stream.readInt();

		// extents = new LinkedHashSet<Extent>(extentCount);
		concepts = new ArrayList<Concept>(conceptCount);
		for(int i = 0; i < conceptCount; i++)
			concepts.add(loadConcept(stream));
	}

	// O(n)
	private void loadObjects(DataInputStream stream) throws IOException
	{
		int size = stream.readInt();
		objects = new String[size];

		for(int i = 0; i < objects.length; i++)
			objects[i] = readString(stream);
	}

	// O(n)
	private String readString(DataInputStream stream) throws IOException
	{
		StringBuilder b = new StringBuilder();
		Byte c;

		while((c = stream.readByte()) != 0)
			b.append((char) (c & 0xff));

		return b.toString();
	}

	// O(objects().length * attributes().length)
	private Concept topConcept(Context context)
	{
		// Set<Element> extent = new LinkedHashSet<Element>();
		List<Element> extent = new ArrayList<Element>(context.getObjects().length);
		double largest;

		// for each object
		for(int i = 0; i < context.getObjects().length; i++)
		{
			// we find the attribute that has the largest value
			
	/* 
	 * **** changed 1-11-12 TPM  *****
	 * 
	 * Top concept should always have univ with memb 1
	 * 
	  		largest = context.getMaxAttributeValue(i);
	  
			// and we had it the extent
			if(largest == 0)
				largest = 1;
			// if(largest > 0)

	 */
			largest = 1;
			extent.add(new Element(this, i, largest));
		}

		return new Concept(new Extent(extent), intentOf(context, new Extent(extent)));
	}
}
