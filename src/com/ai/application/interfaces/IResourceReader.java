package com.ai.application.interfaces;
import java.io.InputStream;

public interface IResourceReader
{
   public InputStream readResource(String filename, IConfig config) throws java.io.IOException;
}