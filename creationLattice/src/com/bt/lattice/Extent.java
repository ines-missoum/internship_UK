package com.bt.lattice;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.bt.graphml.LatticeElement;

public class Extent
{
	private List<Element> extent;
	private String DQUOTE="\"";
	private int BACKSLASHCHAR='\\';

	public Extent(List<Element> elems)
	{
		extent = elems;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == this)
			return true;
		else if((o == null) || (o.getClass() != getClass()))
			return false;
		else
			// return extent.equals(((Extent) o).extent);
			return equals(extent, ((Extent) o).extent);
	}

	public List<Element> getElements()
	{
		return extent;
	}

	@Override
	public int hashCode()
	{
		return extent.hashCode();
	}
	public double cardinality()
	{
		double card=0;
		for(Element e :extent)
		{
			card += e.getMu();
		}
		return card;
	}
	public boolean isEmpty()
	{
		return extent.isEmpty();
	}

	public int size()
	{
		return extent.size();
	}
	public String cardAsString()
	{
		DecimalFormat df = new DecimalFormat("#.#");
		String ret = df.format(cardinality());
		return ret;
	}
	
	public String cardPropAsString(int maxCard)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		String ret = df.format(cardinality()/maxCard);
		return ret;
	}
	public String abbrevExtent(int len) // string with max length
	{
		StringBuffer theStr = new StringBuffer();

		String extra = " ... CARD="+cardAsString()+"]";
		if(extent.isEmpty())
			theStr.append("[ ]");
		else
		{
			Iterator<Element> it = extent.iterator();
			theStr.append("["+it.next()) ;      
			while(it.hasNext() && (theStr.length() < (len-extra.length())))
			{
				theStr.append(", "+it.next()) ;
			}
			if(it.hasNext())
				theStr.append(extra);
			else
				theStr.append("]");
		}
		return theStr.toString();
	}

	@Override
	public String toString()
	{
		return extent.toString();
	}

	//O(n)
	private static boolean equals(List<Element> a, List<Element> b)
	{
		if(a == b)
			return true;
		if(a.size() != b.size())
			return false;

		int i = 0;

		while(i < a.size() && a.get(i) == b.get(i))
			i++;

		return i >= a.size();
	}

	public void unionWith(List<Element> elems)  /// WARNING - doesn't work !!!! *************  
	{
		for(Element e : elems)
		{
			int index = extent.indexOf(e.getName());
			if(index >= 0)
			{
				Element other = extent.get(index);
				if(e.getMu() > other.getMu() )
					other.setMu(e.getMu());
			}
			else
				extent.add(e);
		}
	}
	public int intersectionCard(Extent elems)  /// WARNING - only works for crisp !!!! *************  
	{
		int card=0;
		for(Element e : elems.getElements())
		{
			if(extent.contains(e) )
			{
				card++;

			}

		}
		return card;
	}
	public Set<Element> getReducedExtent(Set<LatticeElement> descSet) 
	{
		Set<Element> ret = new HashSet<Element>();
	Iterator<Element> it = extent.iterator();
	while(it.hasNext())
	{
		Element el = it.next();
		Iterator<LatticeElement> desc=descSet.iterator();
		boolean isMinConcept = true;
		while(isMinConcept && desc.hasNext())
		{
			isMinConcept = !desc.next().getConcept().getExtent().getElements().contains(el);
			
		}
		if(isMinConcept)
		{
			ret.add(el);
		}
	}

			return ret;
		
	}
	public String toQuotedString()
	{
		StringBuilder b = new StringBuilder();
		

		b.append('[');
		if(!extent.isEmpty())
		{
			Iterator<Element> it = extent.iterator();
			b.append(DQUOTE+ escapeQuotes(it.next().toString())+DQUOTE);
			while(it.hasNext())
				b.append(", ").append(DQUOTE+ escapeQuotes(it.next().toString()) +DQUOTE);
		}
		b.append(']');

		return b.toString();
	}
	
	public String reducedExtentString(Set<LatticeElement> descSet) 
	{
	StringBuffer ret = new StringBuffer();
	Iterator<Element> it = extent.iterator();
	while(it.hasNext())
	{
		Element el = it.next();
		Iterator<LatticeElement> desc=descSet.iterator();
		boolean isMinConcept = true;
		while(isMinConcept && desc.hasNext())
		{
			isMinConcept = !desc.next().getConcept().getExtent().getElements().contains(el);
			
		}
		if(isMinConcept)
		{
			if(ret.length()>0) 
				ret.append(", ");
			ret.append("\""+el.toString()+"\"");
		}
	}

			return ret.toString();
		
	}
public String escapeQuotes(String theStr)
{
	StringBuffer theEscString;
	String ret = theStr;
	if( theStr.contains(DQUOTE))
	{
		theEscString=new StringBuffer(theStr);
		
		for(int start=theEscString.indexOf(DQUOTE ,0); start>=0;)
		{
			
			if(start >=0 )
			{
				theEscString.insert(start, '\\');
				start=theEscString.indexOf(DQUOTE ,start+2) ;	// get past \ and "
			}
		}
		ret = theEscString.toString();
		
	}
	return ret;
}
}
