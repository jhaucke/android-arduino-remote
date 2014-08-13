package de.jere0710.arduinoremote.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

public class WiFiOffDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("WLAN nicht aktiv")
				.setMessage(
						"Um eine Netzwerkverbindung herzustellen, muss das WLAN aktiviert werden.")
				.setPositiveButton("Einstellungen öffnen",
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								WiFiOffDialogFragment.this.dismiss();
								startActivity(new Intent(
										Settings.ACTION_WIFI_SETTINGS));
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}

}
