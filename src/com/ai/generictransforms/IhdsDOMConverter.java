package com.ai.generictransforms;

import com.ai.common.TransformException;
// xml imports
import org.w3c.dom.*;
import com.ai.htmlgen.*;

public interface IhdsDOMConverter
{
    public Document convert(ihds data) throws TransformException;
    public Document convert(IFormHandler data) throws TransformException;
}