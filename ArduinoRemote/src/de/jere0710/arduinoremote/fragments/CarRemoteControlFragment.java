package de.jere0710.arduinoremote.fragments;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import de.jere0710.arduinoremote.R;
import de.jere0710.arduinoremote.interfaces.Constants;
import de.jere0710.arduinoremote.network.TransmitterTask;

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

	AsyncTask<String, Integer, Boolean> transmitterTask;

	private static int gasValue;
	private static int steeringValue;

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
		seekBarSteering.setEnabled(false);

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

		// enable listener on seekbar
		seekBarGas.setOnSeekBarChangeListener(this);
		seekBarSteering.setOnSeekBarChangeListener(this);
		centeringControl();

		// start transmitter task
		TransmitterTask requestTask = new TransmitterTask();
		transmitterTask = requestTask.execute();

		super.onResume();
	}

	@Override
	public void onPause() {

		// make sure to turn our sensor off when the activity is paused
		mSensorManager.unregisterListener(this);

		// release the WAKE_LOCK
		wl.release();

		// disable listener on seekbar
		seekBarGas.setOnSeekBarChangeListener(null);
		seekBarSteering.setOnSeekBarChangeListener(null);
		centeringControl();

		// stop transmitter task
		SystemClock.sleep(1000);
		transmitterTask.cancel(true);

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

		if (seekBar.getId() == R.id.seekBarGas) {
			gasValue = progress;
		}

		if (seekBar.getId() == R.id.seekBarSteering) {
			steeringValue = progress;
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

		seekBarGas.setProgress(60);
		gasValue = 60;
		steeringValue = 80;
	}

	public static int getGasValue() {
		return gasValue;
	}

	public static int getSteeringValue() {
		return steeringValue;
	}
}
