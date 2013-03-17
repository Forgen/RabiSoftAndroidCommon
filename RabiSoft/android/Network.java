package RabiSoft.android;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Network {

	public static enum TypeNetwork {

		Default(ConnectionService.m_actionDefaultService),
		Mobile(ConnectionService.m_actionMobileService),
		Wifi(ConnectionService.m_actionWifiService),
		Bluetooth(ConnectionService.m_actionBluetoothService),
		Nfc(ConnectionService.m_actionNfcService),
		None(null);

		public String m_action;

		TypeNetwork(String action) {
			m_action = action;
		}

	}

	public static enum TypeConnection {
		Manual, Auto
	}

	public static final String m_actionUpdatedConnectionWifi = "RabiSoft.CommonSettings.intent.action.UPDATED_CONNECTION_WIFI";
	public static final String m_actionUpdatedConnectionBluetooth = "RabiSoft.CommonSettings.intent.action.UPDATED_CONNECTION_BLUETOOTH";
	public static final String m_actionUpdatedConnectionMobile = "RabiSoft.CommonSettings.intent.action.UPDATED_CONNECTION_MOBILE";
	public static final String m_actionUpdatedConnectionNfc = "RabiSoft.CommonSettings.intent.action.UPDATED_CONNECTION_NFC";
	public static final String m_actionUpdatedNetwork = "RabiSoft.CommonSettings.intent.action.UPDATED_NETWORK";
	
	public static final String m_keyColumn_Connection = "connection";
	
	public static final String m_pathContent = "content://RabiSoft.NetworkManager.SettingsProvider";
	
	public static final String m_pathConnectionDefault = "/ConnectionDefault";
	public static final String m_pathConnectionMobile = "/ConnectionMobile";
	public static final String m_pathConnectionWifi = "/ConnectionWifi";
	public static final String m_pathConnectionBluetooth = "/ConnectionBluetooth";
	public static final String m_pathConnectionNfc = "/ConnectionNfc";

	public static TypeConnection getConnection(Context context, TypeNetwork type) {
		switch (type) {
		case Mobile:
			return getMobileConnection(context);
		case Wifi:
			return getWifiConnection(context);
		case Bluetooth:
			return getBluetoothConnection(context);
		case Nfc:
			return getNfcConnection(context);
		default:
			throw new IllegalArgumentException();
		}
	}

	public static TypeConnection getDefaultConnection(Context context) {

		String path = m_pathContent + m_pathConnectionDefault;
		TypeConnection connection = getConnection(context, path);
		return connection;

	}
	
	public static TypeConnection getMobileConnection(Context context) {

		String path = m_pathContent + m_pathConnectionMobile;
		TypeConnection connection = getConnection(context, path);
		return connection;

	}

	public static TypeConnection getBluetoothConnection(Context context) {

		String path = m_pathContent + m_pathConnectionBluetooth;
		TypeConnection connection = getConnection(context, path);
		return connection;

	}

	public static TypeConnection getWifiConnection(Context context) {

		String path = m_pathContent + m_pathConnectionWifi;
		TypeConnection connection = getConnection(context, path);
		return connection;

	}

	public static TypeConnection getNfcConnection(Context context) {

		String path = m_pathContent + m_pathConnectionNfc;
		TypeConnection connection = getConnection(context, path);
		return connection;

	}
	
	private static TypeConnection getConnection(Context context, String strUri) {

		TypeConnection connection = TypeConnection.Manual;

		{
			ContentResolver resolver = context.getContentResolver();
			Uri uri = Uri.parse(strUri);
			Cursor cursor = resolver.query(uri, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				int index = cursor.getColumnIndexOrThrow(m_keyColumn_Connection);
				String value = cursor.getString(index);
				connection = TypeConnection.valueOf(value);
				cursor.close();
			}
		}

		return connection;

	}

}
