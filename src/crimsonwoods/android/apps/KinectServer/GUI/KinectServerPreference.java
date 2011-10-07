package crimsonwoods.android.apps.KinectServer.GUI;

import crimsonwoods.android.apps.KinectServer.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class KinectServerPreference {
	private Context mContext;
	
	private static KinectServerPreference instance = null;
	
	private KinectServerPreference(Context context) {
		mContext = context;
	}
	
	public static synchronized KinectServerPreference createInstance(Context context) {
		if (null == instance) {
			instance = new KinectServerPreference(context);
		}
		return instance;
	}
	
	public static synchronized KinectServerPreference getInstance() {
		return instance;
	}
	
	private String getString(final int resId) {
		return mContext.getString(resId);
	}

	private SharedPreferences getDefaultSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(mContext);
	}
	
	public int getPortNumber() {
		final String portNumber = getDefaultSharedPreferences().getString(getString(R.string.prefs_key_portnumber), getString(R.string.prefs_value_portnumber));
		return Integer.parseInt(portNumber);
	}
	
	public void setPortNumber(final String portNumber) {
		Editor editor = getDefaultSharedPreferences().edit();
		editor.putString(getString(R.string.prefs_key_portnumber), portNumber);
		editor.commit();
	}
}
