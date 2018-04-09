/*
 * Name: DBLoopMultiFieldRequestExecutorPart
 * 
 * Goal
 * *****
 * 1. Follow the DBLoopRequestExecutor rules
 * 2. Call multiple requests based on a plural field name
 * 3. Allow for multiple fields in each request separated by commas
 * 4. ex: updateAnswersRequest?changedAnswers=2,2|1,2|44,4&userid=john.doe
 * 
 * Configuration
 * *************
 * individualRequestname (ex: upsertAnswerRequest)
 * pluralFieldname (ex: changedAnswers)
 * individualFieldnames(ex: questiond,answerid)
 * 
 * Structure of fields
 * ***********************
 * pluralfieldname=changedAnswers
 * (Ex Coming on the request: changedAnswers=2,2|1,2|44,4 )
 * individualFieldnames=questionid,answerid
 * individualRequestname=upsertAnswer
 * 
 * that results in
 * 
 * upsertAnswer(questionid=2,answerid=2)
 * upsertAnswer(questionid=1,answerid=2)
 * upsertAnswer(questionid=44,answerid=2)
 * 
 * Related classes/parts
 * *************************
 * 1. Based on DBLoopRequestExecutor
 * 2. Also based on CollectionWorkSplitterObjectPart
 * 
 * Instance Information
 * **********************
 * @See its base class DBProcedureObject
 * @see also its efficient cousin DBProcedure
 * Being a DBProcedureObject it can manage local variables
 * 
 */
package com.ai.apps.sat;

import java.util.Hashtable;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.interfaces.RequestExecutorResponse;
import com.ai.application.utils.AppObjects;
import com.ai.data.DataException;
import com.ai.data.DataUtils;
import com.ai.db.DBException;
import com.ai.parts.DBProcedureObject;
import com.ai.reflection.ReflectionException;

/*
 * What is it for?
 * ********************
 * given inputs: testid, sectionid, sectiontype id
 * 
 * 1. set the section type id for this secton
 * 2. do that by inserting into t_SatTestSections
 * 3. Read the sectiontype object
 * 4. See how many questions need to be added
 * 5. add those questions in a loop
 * 6. each question is  inserted into the t_SatQuestions table
 * 7. In the end return true
 * 
 * Input argument expectations
 * ********************************
 * testid
 * sectionid
 * sectiontypeid
 * 
 * questionSequence
 * questionName
 * questionType
 * numberOfOptions
 * correctOption
 * answerExplanation
 *
 * Config expectations
 * *********************
 * request.xyz.classname=com.ai.apps.sat.SetTestSectionTypeAndGenerateQuestionsDBPart
 * upsertSectionTypeRequestName (testid, sectionid, sectiontypeid, profile_user)
 * deleteQuestionsRequestName (testid, sectionid, profile_user)
 * insertQuestionRequestName (testid, sectionid, profile_user, +question values)
 * getSectionTypeDetailsRequestName(sectiontypeid)
 * 
 */
public class SetTestSectionTypeAndGenerateQuestionsDBPart 
extends DBProcedureObject
{
	//You can get these using reflection as well if neeed
	 private String testid; //mandatory
	 private String sectionid; //mandatory
	 private String sectiontypeid; //mandatory
	 
	 //config argumnets
	 private String upsertSectionTypeRequestName;
	 private String deleteQuestionsRequestName;
	 private String insertQuestionRequestName;
	 private String getSectionTypeDetailsRequestName;
	 
	 public static String SATAPP_CONTEXT="Sat";
	 public static String UPSERT_TEST_SECTION_TYPE_REQUEST = "SatUpsertTestSectionTypeRequest";
	 //args testid, sectonid, sectiontypeid, profile_user
	 //all these variable are already there in the args
	 
	 private Hashtable m_localArgs;

	 private void populateInputArguments() throws DBException, ConfigException 
	 {
		 testid = this.readMandatoryInputStringArgument("testid");
		 sectionid = this.readMandatoryInputStringArgument("sectionid");
		 sectiontypeid = this.readMandatoryInputStringArgument("sectiontypeid");
		 
		 upsertSectionTypeRequestName = this.readConfigArgument("upsertSectionTypeRequestName");
		 deleteQuestionsRequestName = this.readConfigArgument("deleteQuestionsRequestName");
		 insertQuestionRequestName = this.readConfigArgument("insertQuestionRequestName");
		 getSectionTypeDetailsRequestName = this.readConfigArgument("getSectionTypeDetailsRequestName");
	 }
	@Override
	protected Object executeDBProcedure(String requestName, Hashtable arguments)
	throws DBException 
	{
		  try
		  {
			  this.populateInputArguments();
			  m_localArgs = arguments;
			  AppObjects.info(this,"Setting section type for:%s,%s,%s",testid, sectionid, sectiontypeid);
	
			  //1. set the section type id for this secton
			  upsertSatTestSectionType(arguments);
			  
			  SatSectionType sst = getSectionType(arguments);
			  
			  //delete the questions for this test
			  deleteQuestionsForTest(arguments);
			  
			  //Add the necessary number of questions
			  this.insertQuestions(sst.f_number_of_questions, arguments,sst.f_sectiontype_abbreviation);
			  
			  //Finally return the response
		      return new RequestExecutorResponse(true);
		  }
		  catch(Exception x)
		  {
			  throw new DBException("Problem setting sectiontype and generating questions",x);
		  }
	}//eof-function executeDBProcedure
	
	//Coded
	private void upsertSatTestSectionType(Hashtable args) 
	throws RequestExecutionException
	{
		AppObjects.getObject(this.upsertSectionTypeRequestName, args);
	}
	
	//Coded
	//Uses reflection to covert database rows to java objects
	private SatSectionType getSectionType(Hashtable args) 
	throws RequestExecutionException, ReflectionException, DataException
	{
		//read the collection and return a SatSectionType
		SatSectionType o = 
		  (SatSectionType) 
			DataUtils.getObjectFromSingleRow(SatSectionType.class, this.getSectionTypeDetailsRequestName, args);
		if (o == null)
		{
			AppObjects.error(this, "No sectiontype object found for sectiontype id: %s",this.sectiontypeid);
			return null;
		}
		AppObjects.info(this,"Number of questions for this section %s are %s"
				,o.f_sectiontype_name,o.f_number_of_questions);
		return o;
		
	}
	//coded
	private void deleteQuestionsForTest(Hashtable args) throws RequestExecutionException
	{
		AppObjects.trace(this,"Deleting questions for test %s and section %s"
			,this.testid
			,this.sectionid);
		//execute the delete request
		AppObjects.getObject(this.deleteQuestionsRequestName, args);
	}
	
	//coded
	private void insertQuestions(int number, Hashtable args, String sectionTypeAbbreviation) 
	throws RequestExecutionException
	{
		AppObjects.trace(this,"Inserting questions for test %s and section %s, type:%s: %s questions"
				,this.testid
				,this.sectionid
				,sectionTypeAbbreviation
				,number);
		
		//replace this with 
		//if (number == -1 )
	    if (SatSectionType.isM2(sectionTypeAbbreviation))
		{
			//special case: 8 choice, 9 to 18 text
			number = 9;
			insertTextQuestionsForASpecialSection(10,18);
		}
		else if (number == -2)
		{
			//special case: read an argument in the future
			//for now set to 20
			number = 20;
		}
			
		for (int i=0;i<number;i++)
		{
			//insert a questions
			//testid, sectionid, questionnumber(i),questiontext,questiontype
			AppObjects.info(this,"Inserting question %s", i);
			this.insertOptionQuestion(i+1,1);
		}
	}
	
	private void insertTextQuestionsForASpecialSection(int startnumber, int lastnumber) 
	throws RequestExecutionException
	{
		for (int i=startnumber;i<=lastnumber;i++)
		{
			//insert a questions
			//testid, sectionid, questionnumber(i),questiontext,questiontype
			AppObjects.info(this,"Inserting question %s", i);
			insertOptionQuestion(i,2);
		}
	}
	
	/*
		CREATE PROCEDURE dbo.sp_insert_SatQuestion
	    @testid int,
	    @sectionid int,
	    @questionSequence int,
	    @questionName varchar(50),
	    @questionType int,
	    @numberOfOptions int,
	    @correctOption int,
	    @answerExplanation varchar(256),
	    @ownerid varchar(50)
		as begin
		
 		args: testid, sectionid, questionid, question text, options, cor coption, explanation, owner 
		exec sp_insert_SatQuestion 1,1,1,'Question 1', 5, 2, 'test 1, section1, question 1, explanation 1','satya'
		exec sp_insert_SatQuestion 1,1,2,'Question 2', 5, 3, 'test 1, section1, question 2, explanation 1','satya'
		exec sp_insert_SatQuestion 1,1,3,'Question 3', 5, 1, 'test 1, section1, question 3, explanation 1','satya'
		exec sp_insert_SatQuestion 1,1,4,'Question 4', 5, 5, 'test 1, section1, question 3, explanation 1','satya'
		exec sp_insert_SatQuestion 1,1,5,'Question 5', 5, 4, 'test 1, section1, question 3, explanation 1','satya'
		exec sp_insert_SatQuestion 1,1,6,'Question 6', 5, 2, 'test 1, section1, question 3, explanation 1','satya'
		exec sp_insert_SatQuestion 1,1,7,'Question 7', 5, 2, 'test 1, section1, question 3, explanation 1','satya'
		
	*/
	private void insertOptionQuestion(int questionNumber, int inQuestionType) 
	throws RequestExecutionException
	{
		//test
		String questionSequence = Integer.toString(questionNumber);
		String questionName = "Question " + questionSequence;
		String questionType = Integer.toString(inQuestionType);
		String numberOfOptions = Integer.toString(5);
		String correctOption = Integer.toString(0);
		String answerExplanation = "None provided";

		addArgument("questionSequence", questionSequence);
		addArgument("questionName", questionName);
		addArgument("questionType", questionType);
		addArgument("numberOfOptions", numberOfOptions);
		addArgument("correctOption", correctOption);
		addArgument("answerExplanation", answerExplanation);
		
		AppObjects.getObject(this.insertQuestionRequestName, m_localArgs);
		
	}
	//coded
	private void addArgument(String name, String value)
	{
		m_localArgs.put(name.toLowerCase(), value);
	}
	
	//Pending
	private void insertRealNumberOrFractionQuestion(int questionNumber)
	{
		
	}

}//eof-main-class
