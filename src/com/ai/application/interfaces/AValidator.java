package com.ai.application.interfaces;

import com.ai.application.utils.AppObjects;

public abstract class AValidator 
implements IValidation {
	public boolean validate()
	{
		try
		{
			validateWithException();
			return true;
		}
		catch(ValidationException x)
		{
			AppObjects.log(AppObjects.LOG_ERROR, x);
			return false;
		}
	}
	public abstract void validateWithException()
	throws ValidationException;

}
