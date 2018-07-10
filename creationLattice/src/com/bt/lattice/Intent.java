package com.bt.lattice;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.bt.graphml.LatticeElement;

public class Intent
{
	private List<Integer> intent; //indexes of attributes composing this intent
	private Lattice lattice;
	private String DQUOTE="\"";

	public Intent(List<Integer> intent, Lattice l)
	{
		this.intent = intent;
		lattice = l;
	}

	/**
	 * O(log n)
	 * @param i
	 * @return true if this contains i
	 */
	public boolean contains(int i)
	{
		return dichoFind(intent, i) > -1;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == this)
			return true;
		else if((o == null) || (o.getClass() != getClass()))
			return false;
		else
			return ((Intent) o).intent.equals(intent);
	}

	public List<Integer> getElements()
	{
		return intent;
	}

	@Override
	public int hashCode()
	{
		return intent.hashCode();
	}

	public boolean isEmpty()
	{
		return intent.isEmpty();
	}

	public int size()
	{
		return intent.size();
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();

		b.append('[');
		if(!intent.isEmpty())
		{
			Iterator<Integer> it = intent.iterator();
			b.append(lattice.getAttributes()[it.next()]);
			while(it.hasNext())
				b.append(", ").append(lattice.getAttributes()[it.next()]);
		}
		b.append(']');

		return b.toString();
	}
	public String toQuotedString()
	{
		StringBuilder b = new StringBuilder();

		b.append('[');
		if(!intent.isEmpty())
		{
			Iterator<Integer> it = intent.iterator();
			b.append(DQUOTE+lattice.getAttributes()[it.next()]+DQUOTE);
			while(it.hasNext())
				b.append(", ").append(DQUOTE+lattice.getAttributes()[it.next()]+DQUOTE);
		}
		b.append(']');

		return b.toString();
	}

	private static int dichoFind(List<Integer> intent, int i)
	{
		int beg = 0, end = intent.size() - 1, mid = (end - beg) / 2;

		while(end >= beg && intent.get(mid = (beg + (end - beg) / 2)) != i)
			if(intent.get(mid) > i)
				end = mid - 1;
			else
				beg = mid + 1;

		return (mid < intent.size() && intent.get(mid) == i) ? mid : -1;
	}

	public Set<String> getReducedIntent(Set<LatticeElement> ancSet) 
	{
		Set<String> ret = new HashSet<String>();
		Iterator<Integer> it = intent.iterator();
		while(it.hasNext())
		{
			Integer attr = it.next();
			Iterator<LatticeElement> anc=ancSet.iterator();
			boolean isMaxConcept = true;
			while(isMaxConcept && anc.hasNext())
			{
				isMaxConcept = !anc.next().getConcept().getIntent().getElements().contains(attr);
				
			}
			if(isMaxConcept)
			{
				
				ret.add(lattice.getAttributes()[attr]);
			}
		}

				return ret;
			
		}
	public String reducedIntentString(Set<LatticeElement> ancSet) 
	{
		StringBuffer ret = new StringBuffer();
		Iterator<Integer> it = intent.iterator();
		while(it.hasNext())
		{
			Integer attr = it.next();
			Iterator<LatticeElement> anc=ancSet.iterator();
			boolean isMaxConcept = true;
			while(isMaxConcept && anc.hasNext())
			{
				isMaxConcept = !anc.next().getConcept().getIntent().getElements().contains(attr);
				
			}
			if(isMaxConcept)
			{
				if(ret.length()>0)
					ret.append(", ");
				ret.append("\""+lattice.getAttributes()[attr]+"\"");
			}
		}

				return ret.toString();
			
		}
	
}
