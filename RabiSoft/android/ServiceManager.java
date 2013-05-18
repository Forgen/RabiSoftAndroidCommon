package RabiSoft.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;

public class ServiceManager {

	static class MyConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName name, IBinder service) {
			// do nothing.
		}

		public void onServiceDisconnected(ComponentName name) {
			// do nothing.
		}
		
	};
	
	public static final String m_columnAction = "action";
	
	public static final String m_pathContent = "content://RabiSoft.ServiceManager.SettingsProvider";
	
	public static final String m_pathIntents = "/Intents";
	
	public static final String m_paramAction = "action";

	HashMap<String, ArrayList<MyConnection>> m_connections = new HashMap<String, ArrayList<MyConnection>>();

	public static Cursor getCursor(Context context, String action) {

		ContentResolver resolver = context.getContentResolver();
		String encodedAction = Uri.encode(action);
		String strUri = m_pathContent + m_pathIntents + "?" + m_paramAction + "=" + encodedAction;
		Uri uri = Uri.parse(strUri);
		Cursor cursor = resolver.query(uri, null, null, null, null);
		return cursor;

	}
	
	public void connect(Context context, String action_from) {
	
   		Cursor cursor = ServiceManager.getCursor(context, action_from);
   		if( cursor != null ) {
   			ArrayList<MyConnection> connections = new ArrayList<MyConnection>();
   			int index_action = cursor.getColumnIndex(ServiceManager.m_columnAction);
   			boolean loop = cursor.moveToFirst();
   			while( loop ) {
   				String action_to = cursor.getString(index_action);
   				Intent service = new Intent();
   				service.setAction(action_to);
   				MyConnection connection = new MyConnection();
   				boolean b = context.bindService(service, connection, Context.BIND_AUTO_CREATE);
   				if( b ) {
   					connections.add(connection);
   				}
   				loop = cursor.moveToNext();
   			}
   			m_connections.put(action_from, connections);
   	   		cursor.close();
   		}

	}
	
	public void disconnect(Context context, String action) {

		ArrayList<MyConnection> connections = m_connections.remove(action);
		if( connections != null ) {
			for( MyConnection connection : connections ) {
				context.unbindService(connection);
			}
		}
		
	}
	
}
