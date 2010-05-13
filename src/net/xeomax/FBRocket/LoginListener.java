package net.xeomax.FBRocket;

public abstract interface LoginListener
{
	public abstract void onLoginSuccess( Facebook paramFacebook );

	public abstract void onLoginFail( );
}
