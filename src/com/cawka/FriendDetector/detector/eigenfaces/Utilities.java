package com.cawka.FriendDetector.detector.eigenfaces;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Bitmap.Config;
import android.util.Log;

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
		for( int w=0; w < input.getWidth( ); w++ )
		{
			for( int h=0; h < input.getHeight( ); h++ )
			{
				int argb=input.getPixel( w, h );
				
				int red=( argb >> 16 ) & 0xff;
				int green=( argb >> 8 ) & 0xff;
				int blue=( argb ) & 0xff;

				result[counter]=(byte)( Math
						.round( (double)( red + green + blue ) / 3 ) & 0xFF );
				counter++;
			}
		}
		return result;
	}
	
	public static Bitmap intensityArraytoBitmap( byte [] buff, int width, int height )
	{
		Bitmap ret=Bitmap.createBitmap( width, height, Config.RGB_565 );
		
		if( buff==null || buff.length!=width*height )
		{
			if( buff==null ) Log.v( "test", "buff is null" );
			if( buff!=null && buff.length!=width*height ) Log.v( "test", "buff.length!=width*height" );
			return ret;
		}
		
		int abs_index=0;
		for( int i=0; i<width; i++ )
			for( int j=0; j<height; j++ )
			{
				int color=0xFF&( (int)buff[abs_index] );
				ret.setPixel( i,j, Color.rgb(color,color,color) );
				abs_index++;
			}
		
		return ret;
	}
}
