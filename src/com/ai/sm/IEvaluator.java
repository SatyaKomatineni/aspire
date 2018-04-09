package com.ai.sm;

public interface IEvaluator 
{
	public void entered(State state, IBingo token, State previousState);
	public void exited(State state, IBingo token);
	String evaluate(State state);
}
