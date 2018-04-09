package com.ai.aspire.authentication.pls;

import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.ai.aspire.authentication.DigestAuthenticationWithPersistentLoginMethod;
import com.ai.parts.RemovePersistentLoginPart;

/**
 * 6/24/17
 * To manage cookie code better in one place
 * @author satya
 * 
 * @see RemovePersistentLoginPart
 * @see DigestAuthenticationWithPersistentLoginMethod
 *
 */
public class PLSUtils 
{
	private static final String RLK_COOKIE_NAME = "rlk";
	
    private static String createRLKForUser(String username)
    {
        Random rand = new Random();
        Long l = rand.nextLong();
        String sl = l.toString();
        return username + ":" + sl;
    }
	public static Cookie createPersistentPLSCookie(String value)
	{
		Cookie pc = new Cookie(RLK_COOKIE_NAME, value);
		pc.setPath("/");
		return pc;
	}
	
	public static Cookie createPersistentPLSCookieForUser(String username)
	{
		//Create the randome key cookie value based on the user 
		String rlk = createRLKForUser(username);
		//path will be set correctly
		Cookie pc = createPersistentPLSCookie(rlk);
		return pc;
	}
	public static Cookie createPersistentPLSCancelCookie()
	{
		//path is set
		Cookie pc = createPersistentPLSCookie("");
		//set cancellation
		pc.setMaxAge(0);
		return pc;
	}
	public static void removePLSCookieFromBrowser(HttpServletResponse response)
	{
		Cookie pc = createPersistentPLSCancelCookie();
		response.addCookie(pc);
	}
}//eof-class
