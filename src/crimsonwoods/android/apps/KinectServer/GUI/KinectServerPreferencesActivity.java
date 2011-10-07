package crimsonwoods.android.apps.KinectServer.GUI;

import crimsonwoods.android.apps.KinectServer.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class KinectServerPreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		
		final EditTextPreference ep = (EditTextPreference)findPreference(getString(R.string.prefs_key_portnumber));
		ep.setSummary(getString(R.string.prefs_summary_portnumber) + ": " + KinectServerPreference.getInstance().getPortNumber());
		ep.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				final String value = (String)newValue;
				int portNumber = Integer.parseInt((String)newValue);
				if ((0 > portNumber) || (portNumber > 65535)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(KinectServerPreferencesActivity.this);
					builder.setCancelable(true);
					builder.setIcon(android.R.drawable.ic_dialog_alert);
					builder.setTitle("Warning");
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					builder.setMessage(getString(R.string.warning_message_portnumber_outofrange));
					builder.show();
					return false;
				}
				ep.setSummary(getString(R.string.prefs_summary_portnumber) + ": " + portNumber);
				KinectServerPreference.getInstance().setPortNumber(value);
				return true;
			}
		});
	}
}
