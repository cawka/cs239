package com.cawka.FriendDetector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListOfPeople extends ListView 
{
	private PeopleAdapter _adapter;
	private ImageWithFaces _picture;
	
	public ListOfPeople( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		
		init( );
	}

	public ListOfPeople( Context context, AttributeSet attrs ) 
	{
		super(context, attrs);
		
		init( );
	}

	public ListOfPeople( Context context ) 
	{
		super( context );
		
		init( );
	}

	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	private void init( )
	{
		_adapter=new PeopleAdapter( this.getContext(), R.layout.row );
		setAdapter( _adapter );
	}
	
	public void clear( )
	{
		_adapter.clear( );
		_adapter.notifyDataSetChanged( );
	}
	
	public void add( Person person )
	{
		_adapter.add( person );
		_adapter.notifyDataSetChanged( );
		_picture.invalidate( );
	}
	
	public void setImageWithFaces( ImageWithFaces picture )
	{
		_picture=picture;
	}
	
	public void setAdapter( ListAdapter adapter )
	{
		super.setAdapter( adapter );
		_adapter=(PeopleAdapter)adapter;
	}
	
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////

	private class PeopleAdapter extends ArrayAdapter<Person> 
    {
		public PeopleAdapter( Context context, int textViewResourceId )
		{
			super( context, textViewResourceId );
		}

		public View getView( int position, View convertView, ViewGroup parent )
		{
			View v=convertView;
			if( v==null ) 
			{
				LayoutInflater vi=(LayoutInflater)getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				v=vi.inflate( R.layout.row, null );
			}
			
			TextView tt=(TextView)v.findViewById( R.id.row_text );
			tt.setText( this.getItem(position).getName() );
			return v;
		}
	}
}
