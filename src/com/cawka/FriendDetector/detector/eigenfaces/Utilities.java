package com.cawka.FriendDetector.detector.eigenfaces;

import android.graphics.Bitmap;

public class Utilities
{
	/**
	 * Converts the given image into an array of length width * height
	 * containing the average of the RGB values for each pixel in the range 0 -
	 * 255 (the intensity).
	 * 
	 * @param input 
	 *            The image to convert
	 * @return The array of intensity values
	 */
	public static byte[] bufferedImageToIntensityArray( Bitmap input )
	{
		byte[] result=new byte[input.getHeight( ) * input.getWidth( )];
		int counter=0;
		for( int h=0; h < input.getHeight( ); h++ )
		{
			for( int w=0; w < input.getWidth( ); w++ )
			{

				int argb=input.getPixel( w, h );
				int red=( argb >> 16 ) & 0xff;
				int green=( argb >> 8 ) & 0xff;
				int blue=( argb ) & 0xff;

				result[counter++]=(byte)( Math
						.round( (double)( red + green + blue ) / 3 ) & 0xFF );
			}
		}
		return result;
	}
}
