package com.ai.sm;

public class LookAheadBuffer 
{
	private char[] cbuffer;
	private IReceiver ofReceiver;
	
	int nextPos=0;
	int curLen = 0;
	int maxLen = 0;
	public LookAheadBuffer(int inLen, IReceiver overflow)
	{
		cbuffer = new char[inLen];
		ofReceiver = overflow;
		maxLen = inLen;
	}
	public void addChar(char c)
	{
		if (curLen == maxLen)
		{
			//we are at the end
			//bump the beginig
			nextPos = nextPos % maxLen;
			bumpBegin();
			addCharAt(c, nextPos);
			nextPos++;
			return;
		}
		//curLen < maxLen
		//bgnPos stays
		addCharAt(c, nextPos);
		nextPos++;
		curLen++;
		return;
	}
	private void bumpBegin()
	{
		char c = cbuffer[nextPos];
		if (this.ofReceiver != null)
		{
			this.ofReceiver.accept(c);
		}
		//System.out.println(c);
	}
	public void addCharAt(char c, int inNextPos)
	{
		cbuffer[inNextPos] = c; 
	}
	public String toString()
	{
		StringBuffer sbuf = new StringBuffer();
		for(int i=0;i<curLen;i++)
		{
			char c = this.cbuffer[i];
			sbuf.append(c);
		}
		sbuf.append(":" + curLen);
		return sbuf.toString();
	}
	
	public static void main(String[] args)
	{
		LookAheadBuffer labf = new LookAheadBuffer(4,null);
		labf.addChar('a');
		labf.addChar('b');
		labf.addChar('c');
		labf.addChar('d');
		labf.addChar('e');
		labf.addChar('f');
		System.out.println(labf.toString());
	}
}//eof-class
