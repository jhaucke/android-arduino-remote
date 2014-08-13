package de.jere0710.arduinoremote.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import de.jere0710.arduinoremote.interfaces.Constants;

public class ConnectionDialogFragment extends DialogFragment implements
		Constants {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("SSID: " + getArguments().getString(ARG_NETWORKNAME)
				+ " PW: " + getArguments().getString(ARG_PASSPHRASE));
		// Create the AlertDialog object and return it
		return builder.create();
	}

}
