package com.ai.application.interfaces;

public interface IValidation 
{
	//Validate self and return true/false
	//No exception is expected back
	public boolean validate();
	public void validateWithException()
	throws ValidationException;

}
