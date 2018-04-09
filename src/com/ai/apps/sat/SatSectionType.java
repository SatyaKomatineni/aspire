package com.ai.apps.sat;

import com.ai.application.interfaces.AValidator;
import com.ai.application.interfaces.ValidationException;
import com.ai.common.StringUtils;

public class SatSectionType extends AValidator
{
/*	
	exec sp_insert_SatSectionType 'Critical Reading 1', 'Critical Reading Section 1', '24 questions', 'CR1', 24, 'satya'
	exec sp_insert_SatSectionType 'Critical Reading 2', 'Critical Reading Section 2', '24 questions', 'CR2', 25, 'satya'
	exec sp_insert_SatSectionType 'Critical Reading 3', 'Critical Reading Section 3', '19 questions', 'CR3', 18, 'satya'

	exec sp_insert_SatSectionType 'Math 1', 'Math Section 1', '20 questions', 'M1', 20, 'satya'
	exec sp_insert_SatSectionType 'Math 2', 'Math Section 2', '8 multiple choice, 9 to 18 text', 'M2', -1, 'satya'
	exec sp_insert_SatSectionType 'Math 3', 'Math Section 3', '16 questions', 'M3', 16, 'satya'

	exec sp_insert_SatSectionType 'Writing 1', 'Writing Section 1 (Essay)', '24 questions', 'W1', 0,'satya'
	exec sp_insert_SatSectionType 'Writing 2', 'Writing Section 2', '35 questions', 'W2', 35, 'satya'
	exec sp_insert_SatSectionType 'Writing 3', 'Writing Section 3', '14 questions', 'W3', 14, 'satya'

	exec sp_insert_SatSectionType 'General', 'Critical Reading Section 1', 'Any number questions', 'G', -2, 'satya'
*/
	
	
	public enum SectionTypeEnum {
		CR1, CR2, CR3, M1, M2, M3, W1, W2, W3, G
	}
	
	public SatSectionType(){}
	public String f_SatSectionType_id;
	public int f_number_of_questions;
	public String f_sectiontype_name;
	public String f_sectiontype_abbreviation;

	//Comparing a string to its enum value
	public static boolean isM2(String type)
	{
		return (SectionTypeEnum.M2 == SectionTypeEnum.valueOf(type));
	}
	
	public void validateWithException()
	throws ValidationException
	{
		if (StringUtils.isEmpty(f_sectiontype_abbreviation))
		{
			throw new ValidationException("empty section type abbreviation");
		}
		if (StringUtils.isEmpty(f_sectiontype_name))
		{
			throw new ValidationException("empty section type name");
		}
		if (StringUtils.isEmpty(f_SatSectionType_id))
		{
			throw new ValidationException("empty section type id");
		}
	}
	
}
