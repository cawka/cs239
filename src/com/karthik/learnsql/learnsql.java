/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karthik.learnsql;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class learnsql extends ListActivity  {

	private SQLiteDatabase db;
	private DBHandle handler;

	private int DB_CHANGE_PERFORMED = 1;
	private static final int DB_ADD_PERFORMED = 2;

	private static final int ADD_SERVER = 1;
	
	private static final String Server = "_server";
	private static final String Port = "_port";
	private static final String Enabled = "_enabled";
	private static final String LocalEnabled = "_localEnabled";
	private static final String Timeout = "_timeout";
	private static final String Table = "detectors";
	private static final String RemoteEnabled = "_RemoteEnabled";
	
	public static final String Create =  "create table detectors(_id integer primary key autoincrement, _server VARCHAR[20] not null, "
        + "_port int not null, _enabled boolean not null,"
        +	"_localEnabled boolean not null, _RemoteEnabled boolean not null, _timeout int not null);";
	
	private static final int DELETE_ID = 0;
	private static final int EDIT_ID = 1;
	private static final int USE_SERVER = 2;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notepad_list);
        handler = new DBHandle(this, Create);
        db = handler.getWritableDatabase();
        DisplayAllServers();
        registerForContextMenu(getListView());
        
    }
    
    private Cursor GetAllServers(int id)
    {
    	Cursor c = fetchServersByID(id);
    	Log.v("Karthik","Cursor " + c.getCount());
    	startManagingCursor(c);
		
    	String from[] = new String[] {Server, Port};
    	int[] to = new int[] {R.id.text1};
    	
    	Log.v("Karthik" , "Count " + c.getColumnIndex(Server));
    	//Display(c, from, to);
    	return c;
    }
    
    private void DisplayAllServers()
    {
    	Cursor c = fetchAllServers();
    	Log.v("Karthik","Cursor " + c.getCount());
    	startManagingCursor(c);
		
    	String from[] = new String[] {Server, Port};
    	int[] to = new int[] {R.id.text1};
    	
    	Log.v("Karthik" , "Count " + c.getColumnIndex(Server));
    	Display(c, from, to);
    }
    
    private void GetAllServers(String server, int port)
    {
    	Cursor c = fetchServersByNamePort(server, port);
    	Log.v("Karthik","Cursor " + c.getCount());
    	startManagingCursor(c);
		
    	String from[] = new String[] {Server, Port};
    	int[] to = new int[] {R.id.text1};
    	
    	Log.v("Karthik" , "Count " + c.getColumnIndex(Server));
    	Display(c, from, to);
    
    }
    
    private void Display(Cursor c, String from[], int[] to)
    {
    	SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.notes_row, c, from, to);
    	setListAdapter(notes);
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	handler.close();
    }
    
    public long createServer(String Server, int port, boolean Enabled, boolean LocalEnabled, boolean RemoteEnabled, int Timeout) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(this.Server, Server);
        initialValues.put(this.Port, port);
        initialValues.put(this.Enabled, Enabled);
        initialValues.put(this.LocalEnabled, LocalEnabled);
        initialValues.put(this.RemoteEnabled, RemoteEnabled);
        initialValues.put(this.Timeout, Timeout);
        return db.insert(Table, null, initialValues);
    }
    
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	menu.add(0,ADD_SERVER ,0,"Add Server");
    	return true;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, "Delete Server");
        menu.add(0, EDIT_ID, 0, "Edit Server");
        menu.add(0, USE_SERVER, 0, "Use Server");
        
    }
    
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	Log.v("Karthik","Printing " + item.getMenuInfo());
    	switch (item.getItemId()) {
		case ADD_SERVER:
			Log.v("karthik:", "Server Settings page");
			Intent i = new Intent();
			i.setAction("com.karthik.learnsql.Settings");
			startActivityForResult(i, DB_ADD_PERFORMED );
			return true;
		default:
			break;
		}
    	return false;
    }
    
    public boolean onContextItemSelected(MenuItem item) {
    	Cursor c = GetAllServers((int) ((AdapterContextMenuInfo)item.getMenuInfo()).id);
    	c.moveToFirst();
    	Log.v("Karthik","Printing " + c.getCount());
    	if(item.getItemId() == DELETE_ID)
    	{
    		Log.v("Karthik : Final ", "Delete ID" + (int) ((AdapterContextMenuInfo)item.getMenuInfo()).id);
    		if(DeleteServer((int) ((AdapterContextMenuInfo)item.getMenuInfo()).id) == 1)
    			Log.v("Karthik : Final", "Row deleted");
    		DisplayAllServers();
    	}
    	else if(item.getItemId() == EDIT_ID)
    	{
    		Log.v("Karthik", "Edit Performed");
    		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences( getBaseContext() );
    		SharedPreferences.Editor edit = prefs.edit();
    		
    		edit.putString(Settings.KEY_HOSTNAME, c.getString(c.getColumnIndex(Server)));
    		edit.putString(Settings.KEY_PORT, c.getString(c.getColumnIndex(Port)));
    		edit.putString("_id", c.getString(c.getColumnIndex("_id")));
    		edit.putString(Settings.KEY_TIMEOUT, c.getString(c.getColumnIndex(Timeout)));
    	
    		if(Integer.parseInt(c.getString(c.getColumnIndex(Enabled))) == 1)
    			edit.putBoolean(Settings.Enabled, true);
    		else
    			edit.putBoolean(Settings.Enabled, false);
    		
    		if(Integer.parseInt(c.getString(c.getColumnIndex(LocalEnabled))) == 1)
    			edit.putBoolean(Settings.KEY_LOCAL_ENABLED, true);
    		else
    			edit.putBoolean(Settings.KEY_LOCAL_ENABLED, false);
    		
    		if(Integer.parseInt(c.getString(c.getColumnIndex(RemoteEnabled))) == 1)
    			edit.putBoolean(Settings.KEY_REMOTE_ENABLED, true);
    		else
    			edit.putBoolean(Settings.KEY_REMOTE_ENABLED, false);
    		
    		edit.commit();
    		
    		Intent i = new Intent();
			i.setAction("com.karthik.learnsql.Settings");
			startActivityForResult(i, DB_CHANGE_PERFORMED );
//    		Log.v("Karthik :", prefs.getString(Settings.KEY_HOSTNAME, "Default"));
    	}
    	else if(item.getItemId() == USE_SERVER)
    	{
    		getIntent().putExtra("USE_Server", true);
    		getIntent().putExtra("Server_ID", (int) ((AdapterContextMenuInfo)item.getMenuInfo()).id);
    	}
        return super.onContextItemSelected(item);
    }

    private int DeleteServer(int id) {
		// TODO Auto-generated method stub
    	return db.delete(Table, "_id = "+id, null);
		
	}

	/**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllServers() {

        return db.query(Table, new String[] {"_id", Server, Port,
                Enabled, LocalEnabled, RemoteEnabled, Timeout}, null, null, null, null, null);
    }
    
    public Cursor fetchServersByID(int id) {

        return db.query(Table, new String[] {"_id", Server, Port,
                Enabled, LocalEnabled, RemoteEnabled, Timeout}, "_id = " + id, null, null, null, null);
    }
    
    public Cursor fetchServersByNamePort(String name, int port) {

        return db.query(Table, new String[] {"_id", Server, Port,
                Enabled, LocalEnabled, RemoteEnabled, Timeout}, Server + "='" + name + "' and " + Port + "=" + port, null, null, null, null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	Log.v("Karthik","Result code " + resultCode + " Req " + requestCode);
    	if(requestCode == DB_ADD_PERFORMED)
    	{
    		Log.v("karthik","Add record to DB");
    		AddToDB();
    		DisplayAllServers();
    	}
    	else if(requestCode == DB_CHANGE_PERFORMED)
    	{
    		updateDB();
    		DisplayAllServers();
    	}
    }

	private void AddToDB() {
		// TODO Auto-generated method stub
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences( getBaseContext() );
		Cursor c = fetchServersByNamePort(prefs.getString(Settings.KEY_HOSTNAME, "Default"), Integer.parseInt(prefs.getString(Settings.KEY_PORT, "Default")));
		if(c.getCount() > 0)
		{
			new Toast(this).makeText(this, "Entry already exists", Toast.LENGTH_SHORT).show();
			Log.v("K Final : ", "Add: entry Exists");
		}
		else
		{
			createServer(prefs.getString(Settings.KEY_HOSTNAME, "Default"), Integer.parseInt(prefs.getString(Settings.KEY_PORT, "9000")),
					prefs.getBoolean(Settings.Enabled, false), prefs.getBoolean(Settings.KEY_LOCAL_ENABLED, false), 
					prefs.getBoolean(Settings.KEY_REMOTE_ENABLED, false), Integer.parseInt(prefs.getString(Settings.KEY_TIMEOUT, "2000")));
			Log.v("K Final : ", "ADD: entry Added");
		}
		
	}

	private void updateDB() {
		// TODO Auto-generated method stub
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences( getBaseContext() );
		Log.v("Karthik :", prefs.getString(Settings.KEY_HOSTNAME, "Default") +  " " + (prefs.getString(Settings.KEY_PORT, "Deafult")));
	
		Cursor c = fetchServersByNamePort(prefs.getString(Settings.KEY_HOSTNAME, "Default"), Integer.parseInt(prefs.getString(Settings.KEY_PORT, "Default")));
		c.moveToFirst();
		Log.v("Karthik","count " + c.getCount());
		
		if(c.getCount()==1 && !c.getString(c.getColumnIndex("_id")).equals(prefs.getString("_id", "Default")))
		{
			new Toast(this).makeText(this, "Entry already exists", Toast.LENGTH_SHORT).show();
			
		}
		else
		{
			ContentValues newValues = new ContentValues();
	        newValues.put(this.Server, prefs.getString(Settings.KEY_HOSTNAME, "Default"));
	        newValues.put(this.Port, Integer.parseInt(prefs.getString(Settings.KEY_PORT, "2000")));
	        newValues.put(this.Enabled, prefs.getBoolean(Settings.Enabled, false));
	        newValues.put(this.LocalEnabled, prefs.getBoolean(Settings.KEY_LOCAL_ENABLED, false));
	        newValues.put(this.RemoteEnabled, prefs.getBoolean(Settings.KEY_REMOTE_ENABLED, false));
	        newValues.put(this.Timeout, Integer.parseInt(prefs.getString(Settings.KEY_TIMEOUT, "2000")));
	        db.update(Table, newValues, "_id = " + Integer.parseInt(prefs.getString("_id", "Default")), null );
	        Log.v ("Karthik Final","Update Completed");
	        DisplayAllServers();
		}
		
	}
    
}
    