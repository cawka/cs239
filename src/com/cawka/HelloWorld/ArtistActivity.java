package com.cawka.HelloWorld;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

public class ArtistActivity extends Activity {
	private static final int EDIT_ID = 2;
	private static final int DELETE_ID = 3;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.artist);
	}
	
	   
    public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) 
    {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, EDIT_ID, 0, "Edit");
		menu.add(0, DELETE_ID, 0, "Delete");
	}
}
