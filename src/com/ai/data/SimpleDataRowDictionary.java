package com.ai.data;

import java.util.List;

import com.ai.common.IDictionary;
/**
 * Make an IDataRow look like a dictionary object
 * @author Satya
 */
public class SimpleDataRowDictionary
implements IDictionary
{
	private IDataRow idr = null;
	public SimpleDataRowDictionary(IDataRow dataRow)
	{
		idr = dataRow;
	}
	@Override
	public Object get(Object key) 
	{
		try
		{
			return idr.getValue((String)key);
		}
		catch(FieldNameNotFoundException x)
		{
			throw new RuntimeException(x);
		}
	}

	@Override
	public void getKeys(List list) 
	{
		try
		{
			IIterator itr = idr.getColumnNamesIterator();
			for(itr.moveToFirst();itr.isAtTheEnd();itr.moveToNext())
			{
				list.add(itr.getCurrentElement());
			}
		}
		catch(DataException x)
		{
			throw new RuntimeException(x);
		}
	}

	@Override
	public void addChild(IDictionary childDictionary) 
	{
		throw new RuntimeException("Unsupported method");
	}

	@Override
	public void removeChild(IDictionary childDictionary) 
	{
		throw new RuntimeException("Unsupported method");
	}
}
