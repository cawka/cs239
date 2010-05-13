package com.cawka.FriendDetector;

import java.util.LinkedList;
import java.util.List;

import com.cawka.FriendDetector.detector.FaceDetectorLocal;
import com.cawka.FriendDetector.detector.FaceDetectorRemote;
import com.cawka.FriendDetector.detector.iFaceLearner;
import com.cawka.FriendDetector.detector.eigenfaces.Eigenface.NamedFace;
import com.cawka.FriendDetector.gui.ImageAdapter;
import com.cawka.FriendDetector.gui.ListOfPeople;
import com.cawka.FriendDetector.settings.Server;
import com.cawka.FriendDetector.settings.Server.Config;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.GridView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TrainSetGallery extends Activity
{
	private static final String TAG="FriendDetector.Gallery";
	
	private GridView _grid;
	private ImageAdapter _adapter;
	
	private iFaceLearner _learner;
	
	public void onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.gallery );
        
        _grid=(GridView)findViewById( R.id.grid );
        
        registerForContextMenu( _grid );
        
        Config config=(Config)getIntent().getSerializableExtra( "config" );
        if( config==null || config.type==Server.LOCAL )
    	{
        	_learner=new FaceDetectorLocal( this );
    	}
        else if( config.type==Server.REMOTE )
    	{
    		try
    		{
    			_learner=new FaceDetectorRemote(
					config.hostname,
					Integer.toString(config.port),
					config.timeout
				);
    		}
    		catch( NoClassDefFoundError e )
    		{
    			Log.v( TAG, "Ice library wasn't enabled during the compilation. Terminating face gallery" );
    			finish( );
    		}
    	}
        
        _adapter=new ImageAdapter( getBaseContext() );
    	_grid.setAdapter( _adapter );
    	
    	new Thread( new TrainSetLoader() ).start( );
    }

	protected void onDestroy( )
	{
		_adapter.clear( );
		
		super.onDestroy();
	}
	
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo )
	{
		onCreateContextMenu( ((AdapterContextMenuInfo)menuInfo).position );
	}
	
	private void onCreateContextMenu( final int position )
	{
		AlertDialog.Builder alert = new AlertDialog.Builder( this );  
		  
		alert.setTitle( "Do you want to remove this face from the training set?" );  
	
		alert.setPositiveButton( "Yes", new DialogInterface.OnClickListener() 
			{   
				public void onClick( DialogInterface dialog, int whichButton )
				{  
					_learner.unLearn( Long.parseLong( ((NamedFace)_adapter.getItem( position )).id ) );
					_adapter.removeItem( position );
				}  
			} );  
		  
		alert.setNegativeButton( "No", new DialogInterface.OnClickListener() 
			{  
				public void onClick( DialogInterface dialog, int whichButton ) 
				{
					// just do nothing  
				}  
			} );  
		  
		alert.show();		
	}	
	
	private class TrainSetLoader implements Runnable
	{
		public void run( )
		{
	        if( _learner!=null ) 
	        {
	        	for( NamedFace face : _learner.getTrainSet( ) )
	        	{
	        		_adapter.add( face );
	        	}
	        }
		}
	}
}
