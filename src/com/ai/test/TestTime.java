/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.test;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/*****************************************************************************
 Symbol   Meaning                 Presentation        Example
 ------   -------                 ------------        -------
 G        era designator          (Text)              AD
 y        year                    (Number)            1996
 M        month in year           (Text & Number)     July & 07
 d        day in month            (Number)            10
 h        hour in am/pm (1~12)    (Number)            12
 H        hour in day (0~23)      (Number)            0
 m        minute in hour          (Number)            30
 s        second in minute        (Number)            55
 S        millisecond             (Number)            978
 E        day in week             (Text)              Tuesday
 D        day in year             (Number)            189
 F        day of week in month    (Number)            2 (2nd Wed in July)
 w        week in year            (Number)            27
 W        week in month           (Number)            2
 a        am/pm marker            (Text)              PM
 k        hour in day (1~24)      (Number)            24
 K        hour in am/pm (0~11)    (Number)            0
 z        time zone               (Text)              Pacific Standard Time
 '        escape for text         (Delimiter)
 ''       single quote            (Literal)           '
The count of pattern letters determine the format. 
(Text): 4 or more pattern letters--use full form, < 4--use short or abbreviated form if one exists. 

(Number): the minimum number of digits. Shorter numbers are zero-padded to this amount. Year is handled specially; that is, if the count of 'y' is 2, the Year will be truncated to 2 digits. 

(Text & Number): 3 or over, use text, otherwise use number. 

Any characters in the pattern that are not in the ranges of ['a'..'z'] and ['A'..'Z'] will be treated as quoted text. For instance, characters like ':', '.', ' ', '#' and '@' will appear in the resulting time text even they are not embraced within single quotes. 

A pattern containing any invalid pattern letter will result in a thrown exception during formatting or parsing. 

Examples Using the US Locale: 

 Format Pattern                         Result
 --------------                         -------
 "yyyy.MM.dd G 'at' hh:mm:ss z"    ->>  1996.07.10 AD at 15:08:56 PDT
 "EEE, MMM d, ''yy"                ->>  Wed, July 10, '96
 "h:mm a"                          ->>  12:08 PM
 "hh 'o''clock' a, zzzz"           ->>  12 o'clock PM, Pacific Daylight Time
 "K:mm a, z"                       ->>  0:00 PM, PST
 "yyyyy.MMMMM.dd GGG hh:mm aaa"    ->>  1996.July.10 AD 12:08 PM
 
Code Sample: 
 
 SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, "PST");
 pdt.setStartRule(DateFields.APRIL, 1, DateFields.SUNDAY, 2*60*60*1000);
 pdt.setEndRule(DateFields.OCTOBER, -1, DateFields.SUNDAY, 2*60*60*1000);
 // Format the current time.
 SimpleDateFormat formatter
     = new SimpleDateFormat ("yyyy.mm.dd e 'at' hh:mm:ss a zzz");
 Date currentTime_1 = new Date();
 String dateString = formatter.format(currentTime_1);
 // Parse the previous string back into a Date.
 ParsePosition pos = new ParsePosition(0);
 Date currentTime_2 = formatter.parse(dateString, pos);
 
In the example, the time value currentTime_2 obtained from parsing will be equal to currentTime_1. However, they may not be equal if the am/pm marker 'a' is left out from the format pattern while the "hour in am/pm" pattern symbol is used. This information loss can happen when formatting the time in PM. 
When parsing a date string using the abbreviated year pattern, SimpleDateFormat must interpret the abbreviated year relative to some century. It does this by adjusting dates to be within 80 years before and 20 years after the time the SimpleDateFormat instance is created. For example, using a pattern of MM/dd/yy and a SimpleDateFormat instance created on Jan 1, 1997, the string "01/11/12" would be interpreted as Jan 11, 2012 while the string "05/04/64" would be interpreted as May 4, 1964. 

For time zones that have no names, use strings GMT+hours:minutes or GMT-hours:minutes. 

The calendar defines what is the first day of the week, the first week of the year, whether hours are zero based or not (0 vs 12 or 24), and the time zone. There is one common decimal format to handle all the numbers; the digit count is handled programmatically according to the pattern. 
*****************************************************************************/
public class TestTime
{

  public TestTime()
  {
  }
 public  static void main(String args[])
  {
      TestTime o = new TestTime();
      o.test();
  } 
  
  public void test()
  {
      Date today = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat();

      System.out.println("Output from the default SimpleDateFormat class");
      System.out.println(sdf.format(today));
      //expected output
      // 10/1/99 1:44 PM
      
      System.out.println("Output from the toGMTString class ");
      System.out.println(today.toGMTString());
      // expected output
      // 1 Oct 1999 17:44 GMT
      SimpleDateFormat dbFormat = new SimpleDateFormat("d-MMM-yy");
      System.out.println("d-MMM-YY format");
      System.out.println(dbFormat.format(today));      
      
      // YYYYMMD_HH_MM
      SimpleDateFormat milFormat = new SimpleDateFormat("yyyyMMd_H_m");
      System.out.println("yyyymm_H_m format");
      System.out.println(milFormat.format(today));      
      
      // YYYYMMDD_HH_MM
      SimpleDateFormat milFormat1 = new SimpleDateFormat("yyyyMMddHHmm");
      System.out.println("yyyyMMdd_HH_mm format");
      System.out.println(milFormat1.format(today));      
  }
} 