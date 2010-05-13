package net.xeomax.FBRocket;

public class ServerErrorException extends Exception
{
	private static final long	serialVersionUID	=-2868130651500796509L;
	private String				errCode;
	private String				errMessage;

	public ServerErrorException( String errCode, String errMessage )
	{
		super( errMessage );
		this.errCode=errCode;
		this.errMessage=errMessage;
	}

	public String getErrCode( )
	{
		return this.errCode;
	}

	public String getErrMessage( )
	{
		return this.errMessage;
	}

	public boolean notLoggedIn( )
	{
		return this.errCode.equals( "102" );
	}

	public String toString( )
	{
		return "Facebook Server Error + " + this.errCode + " - "
				+ this.errMessage;
	}
}
