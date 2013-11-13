package de.jere0710.arduinoremote.fragments;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import de.jere0710.arduinoremote.R;
import de.jere0710.arduinoremote.interfaces.Constants;

public class CarRemoteControlFragment extends Fragment implements Constants,
		SensorEventListener {

	private View mContentView;
	private SensorManager mSensorManager;
	private Sensor mRotationVectorSensor;
	private final float[] mRotationMatrix = new float[16];
	private final float[] orientationArray = new float[3];

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

		mContentView = inflater.inflate(R.layout.fragment_car_remote_control,
				container, false);

		mSensorManager = (SensorManager) getActivity().getSystemService(
				Context.SENSOR_SERVICE);
		mRotationVectorSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

		return mContentView;
	}

	@Override
	public void onResume() {

		// enable our sensor when the activity is resumed, ask for
		// 10 ms updates.
		mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
		super.onResume();
	}

	@Override
	public void onPause() {

		// make sure to turn our sensor off when the activity is paused
		mSensorManager.unregisterListener(this);
		super.onPause();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		// we received a sensor event. it is a good practice to check
		// that we received the proper event
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			// convert the rotation-vector to a 4x4 matrix. the matrix
			// is interpreted by Open GL as the inverse of the
			// rotation-vector, which is what we want.
			SensorManager.getRotationMatrixFromVector(mRotationMatrix,
					event.values);
			SensorManager.getOrientation(mRotationMatrix, orientationArray);

			// Log.e("PADAM", "Z: " + orientationArray[0] + " X: "
			// + orientationArray[1] + " Y: " + orientationArray[2]);

			SeekBar seekBarSteering = ((SeekBar) mContentView
					.findViewById(R.id.seekBarSteering));
			seekBarSteering
					.setProgress(-Math.round(orientationArray[1] * 100) + 150);

			// SeekBar seekBarGas = ((SeekBar) mContentView
			// .findViewById(R.id.seekBarGas));
			// seekBarGas.setProgress(Math.round(orientationArray[2] * 100) +
			// 100);
		}
	}
}
