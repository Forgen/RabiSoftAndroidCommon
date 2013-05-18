package RabiSoft.android;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class ServiceManager {

	public static final String m_keyColumn_Action = "action";
	
	public static final String m_pathContent = "content://RabiSoft.ServiceManager.SettingsProvider";
	
	public static final String m_pathIntents = "/Intents";
	
	public static final String m_paramAction = "action";

	public static Cursor getCursor(Context context, String action) {

		ContentResolver resolver = context.getContentResolver();
		String encodedAction = Uri.encode(action);
		String strUri = m_pathContent + m_pathIntents + "?" + m_paramAction + "=" + encodedAction;
		Uri uri = Uri.parse(strUri);
		Cursor cursor = resolver.query(uri, null, null, null, null);
		return cursor;

	}	

}
