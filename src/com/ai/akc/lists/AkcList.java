package com.ai.akc.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ai.application.utils.AppObjects;
import com.ai.common.IDictionary;
import com.ai.common.Tokenizer;

/**
 * So what is a list?
 * ********************
 * 1. It is a definition for rows of custom attributes (called Fields)
 * 2. The attributes can be of different types
 * 3. the attributes can be stored across multiple tables
 * 4. In the list definition the attributes are stored in a field repository
 * 5. Given an attribute name, I can locate the filed and tell
 *    lot of things about that field such as type, storage, validation etc.
 * 6. Also see the table definition of a list. The url is below
 * 7. http://satyakomatineni.com/item/4093
 *     
 * How do clients use this AkcList?
 * ********************************
 * 1. Create the list with a name
 * 2. Indicate who the owner of this list is
 * 3. Give it a set of set of fields it holds
 * 
 * 4. use the list to insert a row
 * 5. use the list to select rows 
 *
 * List ownership types
 * ********************
 * 1. Global - an independent list
 * 2. Entity List - owned by an entity
 * 		like: a document, or a folder
 *
 * Table Definition
 * ********************************************
CREATE TABLE [dbo].[t_list_definition](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[f_list_key_name] [varchar](50) NULL,
	[f_list_display_name] [varchar](128) NOT NULL,
	[f_listdescription] [varchar](max) NULL,
	[f_listcomments] [varchar](max) NULL,
	[f_list_for_entity_type] [varchar](50) NULL,
	[f_list_for_entity_id] [int] NULL,
	[f_main_list_indicator] [char](1) NOT NULL,
	[f_filed_names] [varchar](256) NOT NULL,
	[f_def_sort_by_fieldnames] [varchar](128) NULL,
	[f_owner_user_id] [varchar](50) NOT NULL,
	[f_created_on] [datetime] NOT NULL,
	[f_last_updated_by] [varchar](50) NOT NULL,
	[f_last_updated_on] [datetime] NOT NULL
) ON [PRIMARY]

 * 
 * List hierarchy
 * ******************
 * A list may be more like an HDS
 * tbd.
 *
 */
public class AkcList 
{
	public String listName;
	public int listDefinitionId;
	public String ownerUserId;
	public Map<String,Field> fieldRepositoryMap 
	 	= new HashMap<String,Field>();
	
	public List<Field> orderedFieldRepository 
 	= new ArrayList<Field>();

	//Directly populate from akc pipeline
	public AkcList(IDictionary dict)
	{
		readSelfFrom(dict);
	}
	
	/*
	 * Take the input post
	 * the keys are user defined fields
	 * the keys should be mapped to their storage keys
	 * use InsertStatement as a conduit to get this done
	 */
	public Collection<InsertStatement>
	getInsertStatements(Map<String,String> submittedListRow)
	{
		//Map<tablename,insertstatement>
		Map<String,InsertStatement> insertStatements 
		= new HashMap<String,InsertStatement>();
		for(String key: submittedListRow.keySet())
		{
			Field f = fieldRepositoryMap.get(key);
			InsertStatement ins = 
				insertStatements.get(f.tablename);
			if (ins == null)
			{
				//there is no insert statement yet
				//Crate an insert statement for this table
				AppObjects.trace(this,
						"Creating a new insert statement for %1s", 
						f.tablename);
				ins = new InsertStatement(f.tablename);
				insertStatements.put(f.tablename, ins);
			}
			//we have an insert statement
			ins.addAttribute(f.columnname, 
					submittedListRow.get(key),
					f.validationType);
		}
		return insertStatements.values();
	}
	/**
	 * for now implement for 1 table.
	 * In the future see how to join multiple tables.
	 * 
	 * Single table structure
	 * ***********************
	 * select f1 as f1_alias, f2 as f2_alias
	 * from t_list_populace
	 * where f_list_definition_id = list_definition_id
	 * 
	 */
	public String 
	getSelectStatement()
	{
		List<String> selectFieldList = new ArrayList<String>();
		for(Field f: this.orderedFieldRepository)
		{
			//get the field
			String userFieldName = f.name;
			String storageFieldName = f.columnname;
			String selectField = storageFieldName + " as " + userFieldName;
			selectFieldList.add(selectField);
		}
		//I got all the fields
		//construct the select columns
		boolean firstone = true;
		StringBuffer selectColumnsBuffer = new StringBuffer();
		for(String selectField: selectFieldList)
		{
			if (firstone)
			{
				selectColumnsBuffer.append(selectField);
				firstone = false;
			}
			else
			{
				selectColumnsBuffer.append(",");
				selectColumnsBuffer.append(selectField);
			}
		}
		//Construct the final select
		String lastUpdatedBy = ", f_last_updated_on as submit_date";
		String selectStatement = "select "
			+ selectColumnsBuffer.toString()
			+ lastUpdatedBy
			+ " from t_list_populace where f_list_definition_id=" + listDefinitionId
			+ " order by submit_date desc";
			
		return selectStatement;
	}
	
	private void readSelfFrom(IDictionary d)
	{
		listName = (String)d.get("f_list_key_name");
		ownerUserId = (String)d.get("f_owner_user_id");
		String sListDefinitionId = (String)d.get("f_list_definition_id");
		listDefinitionId = Integer.parseInt(sListDefinitionId);
		String fieldmap = (String)d.get("f_field_names");
		addFields(fieldmap);
	}
	private void addFields(String fieldmap)
	{
		List<String> fieldSpecs = Tokenizer.tokenize(fieldmap, "|");
		for(String fieldSpec :fieldSpecs)
		{
			Field f = new Field(fieldSpec);
			//fieldRepository.put(f.name,f);
			addField(f);
		}
	}
	private void addField(Field f)
	{
		fieldRepositoryMap.put(f.name,f);
		this.orderedFieldRepository.add(f);
	}
}//eof-class
