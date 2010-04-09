package com.cawka.HelloWorld;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.TabHost;

public class HelloWorld extends TabActivity 
{
    private static final int MENU_NEW_GAME = 1;
	private static final int MENU_QUIT = 2;
	private static final int MENU_SELECT = 3;
	private static final int MENU_GROUP_TEST = 4;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate( savedInstanceState );
//        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        intent = new Intent().setClass( this, ArtistActivity.class );

        spec = tabHost.newTabSpec("artists").setIndicator("Artists",
                          res.getDrawable(R.drawable.ic_tab_artists))
                      .setContent(intent);
        tabHost.addTab( spec );
        tabHost.addTab( spec );
        tabHost.addTab( spec );

        tabHost.setCurrentTab( 1 );
    }
    
    public boolean onCreateOptionsMenu( Menu menu )
    {
    	menu.add(0, MENU_NEW_GAME, 0, "New Game")
    		.setIcon( R.drawable.ic_tab_artists );
        menu.add(0, MENU_SELECT, 0, "Select")
        	.setIcon( android.R.drawable.ic_menu_search );

		SubMenu x=menu.addSubMenu( 0, MENU_GROUP_TEST, 0, "Test" );
		x.add( 0, MENU_QUIT, 0, "Quit again" );

    	return true;
    }
    
    public boolean onOptionsItemSelected( MenuItem item )
    {
    	switch( item.getItemId() ) 
    	{
	        case MENU_NEW_GAME:
	        	
	        	showDialog( 0 );
//	            newGame();
	            return true;
	        case MENU_SELECT:
	        	Intent i=new Intent( Intent.ACTION_PICK );
	        	i.setType( "image/*" );

	        	this.startActivityForResult( i, 1 );
	            return true;
	        case MENU_QUIT:
	        	finish( );
	        	return true;
        }

    	return false;
    }
 
    protected Dialog onCreateDialog( int id )
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Are you sure you want to exit?")
    	       .setCancelable(false)
    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                HelloWorld.this.finish();
    	           }
    	       })
    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	
    	return alert;
    }
    
}
