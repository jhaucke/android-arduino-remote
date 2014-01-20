package de.jere0710.arduinoremote.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;
import de.jere0710.arduinoremote.R;
import de.jere0710.arduinoremote.interfaces.Constants;
import de.jere0710.arduinoremote.listeners.DragListener;
import de.jere0710.arduinoremote.listeners.TouchListener;
import de.jere0710.arduinoremote.network.UdpTask;

public class CustomRemoteControlFragment extends Fragment implements Constants,
		OnCheckedChangeListener {

	private View mContentView;
	private Boolean savedMenuToggleButtonEditMode;
	private SharedPreferences sharedPref;

	// public static Fragment newInstance(final int bla) {
	// final Bundle bunde = new Bundle();
	// bunde.putInt("", bla);
	//
	// final Fragment fragment = new RemoteControlFragment();
	// fragment.setArguments(bunde);
	//
	// return fragment;
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(
				R.layout.fragment_custom_remote_control, container, false);
		setHasOptionsMenu(true);

		sharedPref = getActivity().getSharedPreferences(
				SHAREDPREF_CUSTOMREMOTECONTROLFRAGMENT, Context.MODE_PRIVATE);

		ToggleButton dummyButton = (ToggleButton) mContentView
				.findViewById(R.id.section_button);
		dummyButton.setTranslationX(sharedPref.getFloat(VARIABLE_BUTTONX, 0));
		dummyButton.setTranslationY(sharedPref.getFloat(VARIABLE_BUTTONY, 0));
//		dummyButton.setText("Dummy Button");
		// dummyButton.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // Toast.makeText(getActivity(), "Button clicked",
		// // Toast.LENGTH_SHORT).show();
		// new
		// RequestTask().execute("http://192.168.240.1/arduino/digital/13/1");
		//
		// }
		// });

		dummyButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				UdpTask requestTask = new UdpTask();

				if (isChecked) {

					requestTask
							.execute("1");
				} else {

					requestTask
							.execute("0");
				}
			}
		});

		if (savedInstanceState != null) {

			if (savedInstanceState.containsKey(MENU_TOGGLEBUTTONEDITMODE)) {
				savedMenuToggleButtonEditMode = savedInstanceState
						.getBoolean(MENU_TOGGLEBUTTONEDITMODE);
			}
			if (savedMenuToggleButtonEditMode != null) {

				handleDragAndDropListener(savedMenuToggleButtonEditMode);
			}
		}

		return mContentView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.fragment_custom_remote_control, menu);

		if (savedMenuToggleButtonEditMode != null) {

			ToggleButton tButton = (ToggleButton) menu
					.findItem(R.id.menu_editmode).getActionView()
					.findViewById(R.id.toggleButtonEditMode);
			tButton.setChecked(savedMenuToggleButtonEditMode);
		}
		ToggleButton tButton = (ToggleButton) menu.findItem(R.id.menu_editmode)
				.getActionView().findViewById(R.id.toggleButtonEditMode);
		tButton.setOnCheckedChangeListener(this);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		outState.putBoolean(
				MENU_TOGGLEBUTTONEDITMODE,
				((ToggleButton) getActivity().findViewById(
						R.id.toggleButtonEditMode)).isChecked());

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {

		Button dummyButton = (Button) mContentView
				.findViewById(R.id.section_button);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putFloat(VARIABLE_BUTTONX, dummyButton.getTranslationX());
		editor.putFloat(VARIABLE_BUTTONY, dummyButton.getTranslationY());
		editor.commit();
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		switch (buttonView.getId()) {
		case R.id.toggleButtonEditMode:

			handleDragAndDropListener(isChecked);
			break;

		default:
			break;
		}
	}

	void handleDragAndDropListener(boolean isChecked) {

		if (isChecked) {

			mContentView.findViewById(R.id.section_button).setOnTouchListener(
					new TouchListener());
			mContentView.findViewById(R.id.remote_control_fragment_layout)
					.setOnDragListener(new DragListener());
		} else {

			mContentView.findViewById(R.id.section_button).setOnTouchListener(
					null);
			mContentView.findViewById(R.id.remote_control_fragment_layout)
					.setOnDragListener(null);
		}
	}
}
