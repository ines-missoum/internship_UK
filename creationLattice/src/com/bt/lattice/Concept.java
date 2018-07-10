package com.bt.lattice;

public class Concept
{
	private Extent extent;
	private Intent intent;

	public Concept(Extent e, Intent i)
	{
		extent = e;
		intent = i;
	}

	public Extent getExtent()
	{
		return extent;
	}

	public Intent getIntent()
	{
		return intent;
	}

	@Override
	public int hashCode()
	{
	/* changed TPM june 2014 because example fcaent5.csv caused a clash of hash 
	 * 	int hash = 7;
	
		hash = 31 * hash + extent.hashCode();
		hash = 31 * hash + intent.hashCode();
		return hash;
		 */
		return extent.hashCode();
	}
	
	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();

		b.append("extent:[" + extent.size()+"::"+extent.cardAsString()+ "]= " );
		b.append(extent);
		b.append('\n');
		b.append("intent: ");
		b.append(intent);

		return b.toString();
	}
}
