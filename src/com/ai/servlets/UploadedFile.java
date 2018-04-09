/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
import java.io.*;

// A class to hold information about an uploaded file.
//
public class UploadedFile {

  private String dir;
  private String filename;
  private String type;
  private String m_origFilename;

  public UploadedFile(String dir, String filename, String type, String origFilename) {
    this.dir = dir;
    this.filename = filename;
    this.type = type;
    m_origFilename = origFilename;
  }

  public String getContentType() {
    return type;
  }

  public String getOrigFilename()
  {
   return m_origFilename;
  }
  public String getFilesystemName() {
    return filename;
  }

  public File getFile() {
    if (dir == null || filename == null) {
      return null;
    }
    else {
      return new File(dir + File.separator + filename);
    }
  }
  public String toString()
  {
     return "dir: " + dir 
            + "\n filename: " + filename
            + "\n type " +   type
            + "\n origFilename " + m_origFilename;
  }
}
