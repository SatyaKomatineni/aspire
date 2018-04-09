package com.ai.typefaces;

import com.ai.application.interfaces.AValidator;
import com.ai.reflection.ReflectionException;

public abstract class ATypeFaceValidator 
extends AValidator
implements IInitializableTypeFace
{
	public void initialize()
	throws ReflectionException
	{
	}
}
