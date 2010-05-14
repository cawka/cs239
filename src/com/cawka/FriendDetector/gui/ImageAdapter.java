package com.cawka.FriendDetector.gui;

import java.util.LinkedList;
import java.util.List;

import com.cawka.FriendDetector.R;
import com.cawka.FriendDetector.detector.eigenfaces.Eigenface.NamedFace;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter extends BaseAdapter
{
	LinkedList<NamedFace> _faces=new LinkedList<NamedFace>();
	Context 			  _context;
	
	Handler				  _handler=new Handler();
	
	public ImageAdapter( Context context )
	{
		_context=context;
	}
	
    public int getCount( ) 
    {
        return _faces.size( );
    }

    public View getView( int position, View convertView, ViewGroup parent ) 
    {
		LinearLayout v=(LinearLayout)convertView;

		if( convertView==null )
		{ 
			LayoutInflater vi=(LayoutInflater)_context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
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
	
	public void add( NamedFace face )
	{
		_faces.add( face );
		
		_handler.post( new UpdateUI() );
	}
	
	public void add( int position, NamedFace face )
	{
		_faces.add( position, face );
		
		_handler.post( new UpdateUI() );
	}
	
	public void clear( )
	{
		for( NamedFace face : _faces )
		{
			face.bitmap.recycle( );
		}
		_faces.clear( );
		
		_handler.post( new UpdateUI() ); ///there is a chance that it would case crashes
	}
	
	public void removeItem( int position )
	{
		_faces.remove( position );
		
		_handler.post( new UpdateUI() );
	}
	
	//////////////////////////////////////////////////////////////
	
	private class UpdateUI implements Runnable
	{
		public void run( )
		{
			notifyDataSetChanged( );
		}
	}
}
