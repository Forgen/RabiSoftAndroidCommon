package RabiSoft.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class Common {

	public enum Network {

		Default(ConnectionService.m_actionDefaultService),
		Mobile(ConnectionService.m_actionMobileService),
		Wifi(ConnectionService.m_actionWifiService),
		Bluetooth(ConnectionService.m_actionBluetoothService),
		None(null);

		public String m_action;

		Network(String action) {
			m_action = action;
		}

	}

	public enum SensorSleep {
		sleep, wake
	}

	public enum SensorWakeLock {

		none(0),
		partial(PowerManager.PARTIAL_WAKE_LOCK),
		screen_dim(PowerManager.SCREEN_DIM_WAKE_LOCK);

		public int m_flag;

		SensorWakeLock(int flag) {
			m_flag = flag;
		}

	}
	
	public enum Connection {
		Manual, Auto
	}

	public static final String m_actionUpdatedLocationProvider = "RabiSoft.CommonSettings.intent.action.UPDATED_LOCATION_PROVIDER";
	public static final String m_actionUpdatedSensorSleep = "RabiSoft.CommonSettings.intent.action.UPDATED_SENSOR_SLEEP";
	public static final String m_actionUpdatedSensorWakeLock = "RabiSoft.CommonSettings.intent.action.UPDATED_SENSOR_WAKE_LOCK";
	public static final String m_actionUpdatedConnectionWifi = "RabiSoft.CommonSettings.intent.action.UPDATED_CONNECTION_WIFI";
	public static final String m_actionUpdatedConnectionBluetooth = "RabiSoft.CommonSettings.intent.action.UPDATED_CONNECTION_BLUETOOTH";
	public static final String m_actionUpdatedConnectionMobile = "RabiSoft.CommonSettings.intent.action.UPDATED_CONNECTION_MOBILE";
	public static final String m_actionUpdatedNetwork = "RabiSoft.CommonSettings.intent.action.UPDATED_NETWORK";
	public static final String m_actionBootCompeted = "RabiSoft.BootCompletedManager.intent.action.BOOT_COMPLETED";
	
	public static final String m_keyColumn_Provider = "provider";
	public static final String m_keyColumn_SensorSleep = "sensor_sleep";
	public static final String m_keyColumn_SensorWakeLock = "sensor_wakelock";
	public static final String m_keyColumn_Connection = "connection";
	public static final String m_keyColumn_Package = "package";
	
	static final String m_pathContentCommon = "content://RabiSoft.CommonSettings.SettingsProvider";
	static final String m_pathContentNetwork = "content://RabiSoft.NetworkManager.SettingsProvider";
	static final String m_pathContentBoot = "content://RabiSoft.BootCompletedManager.SettingsProvider";
	
	public static final String m_pathLocationProvider = "/LocationProvider";
	public static final String m_pathSensorSleep = "/SensorSleep";
	public static final String m_pathSensorWakeLock = "/SensorWakeLock";
	public static final String m_pathConnectionDefault = "/ConnectionDefault";
	public static final String m_pathConnectionMobile = "/ConnectionMobile";
	public static final String m_pathConnectionWifi = "/ConnectionWifi";
	public static final String m_pathConnectionBluetooth = "/ConnectionBluetooth";
	public static final String m_pathPackages = "/Packages";

	private static Boolean m_bDebugAble = null;
	
	public static boolean isDebugAble(Context context) {
		
		if( m_bDebugAble == null ) {

			ApplicationInfo info;
	
			{
				PackageManager manager = context.getPackageManager();
				String namePackage = context.getPackageName();
				try {
					info = manager.getApplicationInfo(namePackage, 0);
				} catch (NameNotFoundException e) {
					throw new RuntimeException();
				}
			}
			
			int flagDebug = info.flags & ApplicationInfo.FLAG_DEBUGGABLE;
			m_bDebugAble = ( flagDebug != 0 );
		}		
		
		return m_bDebugAble;
	}

	static class Tablet {
		
		@SuppressLint("NewApi")
		static class Lv14_ {
			static void getMetrics(Display display, DisplayMetrics metrics) {
		    	display.getRealMetrics(metrics);
			}
		}

		static class Lv3_10 {
			static void getMetrics(Display display, DisplayMetrics metrics) {
		    	display.getMetrics(metrics);
			}
		}

	}
	
	private static Boolean m_bTablet = null;
	
    public static boolean isTablet(Activity activity) {
    	
    	if( m_bTablet == null ) {
	    	m_bTablet = isTabletSub(activity);
    	}
    	
    	return m_bTablet;
    	
    }

    private static boolean isTabletSub(Activity activity) {
    	
    	DisplayMetrics metrics = new DisplayMetrics();
    	{
	    	WindowManager manager = activity.getWindowManager();
	    	Display display = manager.getDefaultDisplay();
	    	{
	    		int lv = Integer.valueOf(VERSION.SDK);
	    		if( 14 <= lv ) {
			    	Tablet.Lv14_.getMetrics(display, metrics);
	    		} else if( 11 <= lv ) {
	    			return true;
	    		} else {
			    	Tablet.Lv3_10.getMetrics(display, metrics);
	    		}
	    	}
    	}
    	float height = metrics.heightPixels / metrics.density;
    	float width = metrics.widthPixels / metrics.density;
    	float smallest_width = Math.min(height, width);
    	
    	boolean b = ( 600.0f <= smallest_width );
    	return b;
    	
    }

	public static String getLocationProvider(Context context) {

		String provider = null;

		{
			ContentResolver resolver = context.getContentResolver();
			String path = m_pathContentCommon + m_pathLocationProvider;
			Uri uri = Uri.parse(path);
			Cursor cursor = resolver.query(uri, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				int index = cursor.getColumnIndexOrThrow(m_keyColumn_Provider);
				provider = cursor.getString(index);
				cursor.close();
			}
		}

		if (provider == null) {
			LocationManager manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				provider = LocationManager.GPS_PROVIDER;
			} else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				provider = LocationManager.NETWORK_PROVIDER;
			}
		}

		return provider;

	}

	public static SensorSleep getSensorSleep(Context context) {

		SensorSleep sleep = SensorSleep.wake;

		{
			ContentResolver resolver = context.getContentResolver();
			String path = m_pathContentCommon + m_pathSensorSleep;
			Uri uri = Uri.parse(path);
			Cursor cursor = resolver.query(uri, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				int index = cursor.getColumnIndexOrThrow(m_keyColumn_SensorSleep);
				String value = cursor.getString(index);
				sleep = SensorSleep.valueOf(value);
				cursor.close();
			}
		}

		return sleep;

	}

	public static SensorWakeLock getSensorWakeLock(Context context) {

		SensorWakeLock lock = SensorWakeLock.screen_dim;

		{
			ContentResolver resolver = context.getContentResolver();
			String path = m_pathContentCommon + m_pathSensorWakeLock;
			Uri uri = Uri.parse(path);
			Cursor cursor = resolver.query(uri, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				int index = cursor.getColumnIndexOrThrow(m_keyColumn_SensorWakeLock);
				String value = cursor.getString(index);
				lock = SensorWakeLock.valueOf(value);
				cursor.close();
			}
		}

		return lock;

	}

	public static Connection getConnection(Context context, Network network) {
		switch (network) {
		case Mobile:
			return getMobileConnection(context);
		case Wifi:
			return getWifiConnection(context);
		case Bluetooth:
			return getBluetoothConnection(context);
		default:
			throw new IllegalArgumentException();
		}
	}

	public static Connection getDefaultConnection(Context context) {

		String path = m_pathContentNetwork + m_pathConnectionDefault;
		Connection connection = getConnection(context, path);
		return connection;

	}
	
	public static Connection getMobileConnection(Context context) {

		String path = m_pathContentNetwork + m_pathConnectionMobile;
		Connection connection = getConnection(context, path);
		return connection;

	}

	public static Connection getBluetoothConnection(Context context) {

		String path = m_pathContentNetwork + m_pathConnectionBluetooth;
		Connection connection = getConnection(context, path);
		return connection;

	}

	public static Connection getWifiConnection(Context context) {

		String path = m_pathContentNetwork + m_pathConnectionWifi;
		Connection connection = getConnection(context, path);
		return connection;

	}

	private static Connection getConnection(Context context, String strUri) {

		Connection connection = Connection.Manual;

		{
			ContentResolver resolver = context.getContentResolver();
			Uri uri = Uri.parse(strUri);
			Cursor cursor = resolver.query(uri, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				int index = cursor.getColumnIndexOrThrow(m_keyColumn_Connection);
				String value = cursor.getString(index);
				connection = Connection.valueOf(value);
				cursor.close();
			}
		}

		return connection;

	}

	public static void registerBootPackage(Context context) {
		ContentResolver resolver = context.getContentResolver();
		String path = m_pathContentBoot + m_pathPackages;
		Uri uri = Uri.parse(path);
		ContentValues values = new ContentValues();
		String namePackage = context.getPackageName();
		values.put(m_keyColumn_Package, namePackage);
		try {
			resolver.insert(uri, values);
		} catch (IllegalArgumentException e) {
			// do nothing.
		}
	}

	public static void unregisterBootPackage(Context context) {
		ContentResolver resolver = context.getContentResolver();
		String path = m_pathContentBoot + m_pathPackages;
		Uri uri = Uri.parse(path);
		String where = m_keyColumn_Package + "=?";
		String namePackage = context.getPackageName();
		String[] args = { namePackage };
		try {
			resolver.delete(uri, where, args);
		} catch (IllegalArgumentException e) {
			// do nothing.
		}
	}
	
}
