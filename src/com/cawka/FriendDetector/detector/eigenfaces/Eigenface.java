/*******************************************************************************
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 * |                                                                         |
 *    faint - The Face Annotation Interface
 * |  Copyright (C) 2007  Malte Mathiszig                                    |
 * 
 * |  This program is free software: you can redistribute it and/or modify   |
 *    it under the terms of the GNU General Public License as published by
 * |  the Free Software Foundation, either version 3 of the License, or      |
 *    (at your option) any later version.                                     
 * |                                                                         |
 *    This program is distributed in the hope that it will be useful,
 * |  but WITHOUT ANY WARRANTY; without even the implied warranty of         |
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * |  GNU General Public License for more details.                           |
 * 
 * |  You should have received a copy of the GNU General Public License      |
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * |                                                                         |
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 *******************************************************************************/

package com.cawka.FriendDetector.detector.eigenfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.cawka.FriendDetector.Person;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.Log;

/**
 * @author maltech
 * 
 */
public class Eigenface
{
	private static final int MAX_EIGEN_FACES=40;
	private static final int VECTORLENGTH=Person.FACE_HEIGHT*Person.FACE_WIDTH;
	
	private static final String TAG="FriendDetector.Eigenface";
	
	private Context _context;
	
	byte[] 			_averageFace;
//	byte[][] 		_faceVectors;
	List<double[]> 	_eigenFaces;
	int 			_numTrainingImages;

	///////////////////////////////////////////////////////////////
	
	public Eigenface( Context context )
	{
		_context=context;
		
		update( ); //initialize
	}
	
	/**
	 * Calculates a new set of Eigenfaces and then replaces the old set.
	 * 
	 */
	public void update( )
	{
		DBHandleEigen db=new DBHandleEigen( _context );
		List<NamedFace> faces=db.getAllFaces( );
		_numTrainingImages=faces.size( );

		int lastNumTrainingImages=Math.max( 0,_numTrainingImages-1 );//eigenfaceRecognition.lastNumberOfTrainingImages;
		Integer step=null;//eigenfaceRecognition.rebuildFaceSpace;
		
		if( ( step == null && Math.abs( _numTrainingImages
				- lastNumTrainingImages ) > 0 )
				|| ( step != null && Math.abs( _numTrainingImages
						- lastNumTrainingImages ) > step ) )
		{
			if( _numTrainingImages > 0 )
			{
				double factor=1.0 / _numTrainingImages;
				_averageFace=new byte[VECTORLENGTH];
				double[] tempAverageFace=new double[VECTORLENGTH];
				byte[][] faceVectors=new byte[_numTrainingImages][];

				int r=0;
				for( NamedFace face : faces )
				{
					Log.v( TAG, "next face, id: "+Long.toString( face.id ) );
					
					byte[] intensityImage=Utilities.bufferedImageToIntensityArray( face.bitmap ) ;
					Log.v( TAG, "Face bitmap size: w:"+Integer.toString(face.bitmap.getWidth())+", h:"+Integer.toString(face.bitmap.getHeight( )));
					
					for( int i=0; i < tempAverageFace.length; i++ )
					{
						tempAverageFace[i]+=( (double)( intensityImage[i] & 0xFF ) ) * factor;
					}
					faceVectors[r]=intensityImage;
					face.bitmap.recycle( );
					
					r++;
				}

				// convert average face to byte array
				for( int i=0; i < tempAverageFace.length; i++ )
				{
					_averageFace[i]=(byte)( Math.round( tempAverageFace[i] ) );
				}

				// calculate distances of all intensity images to average face
				short[][] distances=new short[faceVectors.length][VECTORLENGTH];
				for( int i=0; i < faceVectors.length; i++ )
				{
					for( int j=0; j < VECTORLENGTH; j++ )
					{
						distances[i][j]=(short)( (short)( faceVectors[i][j] & 0xFF ) - (short)( _averageFace[j] & 0xFF ) );
					}
				}

				// build up covariance matrix for Eigenvector calculation
				CovarianceMatrix matrix=new CovarianceMatrix( distances );

				// calculate and store Eigenfaces
				_eigenFaces=new ArrayList<double[]>( );
				int numEigenfaces=Math.min( distances.length, MAX_EIGEN_FACES );
				
				for( int i=0; i < numEigenfaces; i++ )
				{
					_eigenFaces.add( matrix.getEigenVector( i ) );

					/*
					 * Umcomment this to view all eigenfaces in separate frames
					 * 
					 * byte [] ef =
					 * Utilities.spreadGreyValues(eigenFaces.get(i));
					 * BufferedImage recoFaceImage =
					 * Utilities.intensityArrayToBufferedImage(ef,
					 * Constants.FACE_THUMBNAIL_SIZE);
					 * Utilities.showImageInFrame(recoFaceImage,i+"");
					 */
				}

//				eigenfaceRecognition.updateIsRunning=false;
//				eigenfaceRecognition.updateData( averageFace, eigenFaces,
//						numTrainingImages );
//				eigenfaceRecognition.updateView( );
			}
		}
	}
	
	
	
	public synchronized String recognize( Bitmap face )
	{
		// Prepare first set of Eigenfaces (in same thread)
		if( _averageFace == null || _eigenFaces == null )
		{
			return "";
		}

		byte[]   unknownFace      =Utilities.bufferedImageToIntensityArray( face );
		double[] unknownFaceWeight=getWeightForImage( unknownFace );

		// Mirrored region may increase recognition performance
//		double[] unknownMirroredFaceWeight=null;
//		if( mirrorFaces )
//		{
//			byte[] mirroredFace=new byte[unknownFace.length];
//			for( int i=0; i < Constants.FACE_THUMBNAIL_SIZE.height; i++ )
//			{
//				for( int j=0; j < Constants.FACE_THUMBNAIL_SIZE.width; j++ )
//				{
//					int elem=i * Constants.FACE_THUMBNAIL_SIZE.width + j;
//					mirroredFace[elem]=unknownFace[( i + 1 )
//							* Constants.FACE_THUMBNAIL_SIZE.width - j - 1];
//				}
//			}
//			unknownMirroredFaceWeight=this.getWeightForImage( mirroredFace );
//		}

		DBHandleEigen db=new DBHandleEigen( _context );
		
		String name="";
		
		Integer i=0;

		double bestScore=Double.MAX_VALUE;
		
		List<String> names=db.getNames( );
		for( String candidate : names )
		{
			List<NamedFace> faces=db.getFacesByName( candidate );
			double minDist=Double.MAX_VALUE;

			for( NamedFace knownFacePlatform : faces )
			{
				byte[]   knownFace      =Utilities.bufferedImageToIntensityArray( knownFacePlatform.bitmap );
				double[] knownFaceWeight=getWeightForImage( knownFace );
	
				double distance=getDistanceBetweenWeights( knownFaceWeight, unknownFaceWeight );
	
	//			if( unknownMirroredFaceWeight != null )
	//			{
	//				distance=Math.min( distance,
	//						getDistanceBetweenWeights( knownFaceWeight,
	//								unknownMirroredFaceWeight ) );
	//			}
	
				if( distance < minDist )
				{
					minDist=distance;
//					name=knownFacePlatform.name;
				}
				
				Log.v( TAG, "image:"+i+", name:"+candidate+", raw score:"+Double.toString(distance) );
				
				knownFacePlatform.bitmap.recycle( );
				i++;
			}
			
			// Map distance to interval [0, 100]
			Integer points=(int)Math.max( 0, 100 - Math.round( minDist * 0.2f ) );

			Log.v( TAG, "name:"+candidate+", raw score:"+Double.toString(minDist)+", scaled score:"+points );
			
			if( bestScore>minDist )
			{
				bestScore=minDist;
				name=candidate;
			}
		}

		return name;
	}
	
	protected double[] getWeightForImage( byte[] image )
	{
		short[] distanceFromAverageFace=new short[VECTORLENGTH];
		for( int i=0; i < distanceFromAverageFace.length; i++ )
		{
			distanceFromAverageFace[i]=(short)( ( (short)image[i] & 0xFF ) - ( (short)_averageFace[i] & 0xFF ) );
		}

		double[] result=new double[_eigenFaces.size( )];
		for( int i=0; i < result.length; i++ )
		{
			result[i]=0;
			for( int j=0; j < _eigenFaces.get( i ).length; j++ )
			{
				result[i]+=_eigenFaces.get( i )[j] * distanceFromAverageFace[j];
			}
		}
		return result;
	}


    /**
     * Returns the average of the differences of all the values in the two given arrays.
     *
     * @param weightA The first array
     * @param weightB The second array
     * @return The average of the differences of the arrays.
     */
	protected double getDistanceBetweenWeights( double[] weightA, double[] weightB )
	{
		double result=0;
		for( int i=0; i < weightA.length; i++ )
		{
			double diff=weightA[i] - weightB[i];
//			result+=diff*diff;
			result+=Math.abs( weightA[i] - weightB[i] );
		}
//		int n=weightA.length;
//		int n_1=Math.max( n-1, 1 );
//		return Math.sqrt(result / (n*n_1) );
		return result/weightA.length;
	}

	protected byte[] getFaceReconstruction( double[] weight )
	{
		double[] temp=new double[VECTORLENGTH];

		for( int i=0; i < weight.length; i++ )
		{
			for( int j=0; j < temp.length; j++ )
				temp[j]+=weight[i] * _eigenFaces.get( i )[j];
		}

		for( int j=0; j < temp.length; j++ )
		{
			temp[j]+=((int)_averageFace[j]) & 0xff;
		}

		byte[] image=new byte[VECTORLENGTH];
		for( int i=0; i < image.length; i++ )
		{
			int value=(int)Math.max( 0, Math.min( Math.round( temp[i] ), 255 ) );
			image[i]=(byte)( value & 0xff );
		}
		return image;
	}	
	
	public Bitmap getAverageFace( )
	{
		return Utilities.intensityArraytoBitmap( _averageFace, Person.FACE_WIDTH, Person.FACE_HEIGHT );
	}
	
	//
	
	public static class NamedFace
	{
		public long   id;
		public Bitmap bitmap;
		public String name;
		
		public NamedFace( long _id, Bitmap _bitmap, String _name )
		{
			id=_id;
			bitmap=_bitmap;
			name=_name;
		}
	}
}
