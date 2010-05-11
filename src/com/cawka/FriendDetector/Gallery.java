package com.cawka.FriendDetector;

import java.util.List;

import com.cawka.FriendDetector.detector.FaceDetectorLocal;
import com.cawka.FriendDetector.detector.FaceDetectorRemote;
import com.cawka.FriendDetector.detector.iFaceLearner;
import com.cawka.FriendDetector.detector.eigenfaces.Eigenface.NamedFace;
import com.cawka.FriendDetector.gui.ListOfPeople;
import com.cawka.FriendDetector.settings.Server;
import com.cawka.FriendDetector.settings.Server.Config;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView.ScaleType;

public class Gallery extends Activity
{
	private static final String TAG="FriendDetector.Gallery";
	
	private GridView _grid;
	
	iFaceLearner _learner;
	
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

        if( _learner!=null ) _grid.setAdapter( new ImageAdapter() );
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
					((ImageAdapter)_grid.getAdapter( )).removeItem( position );
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
	
	public class ImageAdapter extends BaseAdapter 
	{
		List<NamedFace> _faces;
		
		public ImageAdapter( )
		{
//			_faces=new DBHandleEigen( Gallery.this ).getAllFaces( );
			_faces=_learner.getTrainSet( );
		}
		
	    public int getCount( ) 
	    {
	        return _faces.size( );
	    }

	    public View getView( int position, View convertView, ViewGroup parent ) 
	    {
			LinearLayout v=(LinearLayout)convertView;

			if( convertView == null ) // if it's not recycled, initialize some attributes
			{ 
				LayoutInflater vi=(LayoutInflater)getBaseContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				v=(LinearLayout)vi.inflate( R.layout.face, null );
			}
			
			NamedFace person=_faces.get( position );

			ImageView face=(ImageView)v.findViewById( R.id.row_image );
			face.setImageBitmap( person.bitmap );
			face.setScaleType( ScaleType.CENTER_CROP );

			TextView name=(TextView)v.findViewById( R.id.row_text );
			name.setText( person.name );
			
			return v;
	    }

		public Object getItem( int position )
		{
			return _faces.get( position );
		}

		public long getItemId( int position )
		{
			return position;
		}
		
		public void removeItem( int position )
		{
			_learner.unLearn( _faces.get( position ).id );
//			new DBHandleEigen( Gallery.this ).delete( _faces.get( position ).id );
			_faces.remove( position );
			
			notifyDataSetChanged( );
		}
	}	
}
