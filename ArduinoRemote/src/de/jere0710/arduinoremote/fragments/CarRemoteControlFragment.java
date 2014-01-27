package de.jere0710.arduinoremote.fragments;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import de.jere0710.arduinoremote.R;
import de.jere0710.arduinoremote.interfaces.Constants;
import de.jere0710.arduinoremote.network.UdpTask;

public class CarRemoteControlFragment extends Fragment implements Constants,
		SensorEventListener, OnSeekBarChangeListener {

	private View mContentView;
	private SensorManager mSensorManager;
	private Sensor mRotationVectorSensor;
	private SeekBar seekBarGas;
	private SeekBar seekBarSteering;
	private PowerManager.WakeLock wl;
	private final float[] mRotationMatrix = new float[16];
	private final float[] orientationArray = new float[3];

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.fragment_car_remote_control,
				container, false);

		mSensorManager = (SensorManager) getActivity().getSystemService(
				Context.SENSOR_SERVICE);
		mRotationVectorSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		seekBarGas = ((SeekBar) mContentView.findViewById(R.id.seekBarGas));
		seekBarSteering = ((SeekBar) mContentView
				.findViewById(R.id.seekBarSteering));
		seekBarGas.setOnSeekBarChangeListener(this);
		seekBarSteering.setEnabled(false);
		seekBarSteering.setOnSeekBarChangeListener(this);
		centeringControl();

		PowerManager pm = (PowerManager) getActivity().getSystemService(
				Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"CarRemoteControlFragment");

		return mContentView;
	}

	@Override
	public void onResume() {

		// enable our sensor when the activity is resumed, ask for
		// 10 ms updates.
		mSensorManager.registerListener(this, mRotationVectorSensor, 10000);

		// acquire the WAKE_LOCK
		wl.acquire();

		super.onResume();
	}

	@Override
	public void onPause() {

		// make sure to turn our sensor off when the activity is paused
		mSensorManager.unregisterListener(this);

		// release the WAKE_LOCK
		wl.release();

		super.onPause();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

		// nothing to do
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
					.setProgress(-Math.round(orientationArray[1] * 100) + 90);

			// SeekBar seekBarGas = ((SeekBar) mContentView
			// .findViewById(R.id.seekBarGas));
			// seekBarGas.setProgress(Math.round(orientationArray[2] * 100) +
			// 100);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		UdpTask requestTask = new UdpTask();

		if (seekBar.getId() == R.id.seekBarGas) {

			// Log.e("GAS", "gas:" + progress);
			requestTask.execute("g:" + progress);
		}

		if (seekBar.getId() == R.id.seekBarSteering) {

			// Log.e("STEERING", "steering:" + progress);
			requestTask.execute("s:" + progress);
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

		// nothing to do
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

		if (seekBar.getId() == R.id.seekBarGas) {

			seekBarGas.setProgress(60);
		}
	}

	private void centeringControl() {
		seekBarSteering.setProgress(80);
		seekBarGas.setProgress(60);
	}
}
