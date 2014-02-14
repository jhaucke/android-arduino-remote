package de.jere0710.arduinoremote.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;
import de.jere0710.arduinoremote.fragments.CarRemoteControlFragment;

public class TransmitterTask extends AsyncTask<String, Integer, Boolean> {

	private InetAddress inet_addr;
	private DatagramSocket socket;

	private byte[] gasBuffer;
	private byte[] steeringBuffer;

	private DatagramPacket gasPacket;
	private DatagramPacket steeringPacket;

	@Override
	protected Boolean doInBackground(String... arg0) {
		byte[] ip_bytes = new byte[] { (byte) 192, (byte) 168, (byte) 240,
				(byte) 1 };
		try {
			inet_addr = InetAddress.getByAddress(ip_bytes);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			socket = new DatagramSocket();

			while (true) {
				gasBuffer = ("g:" + CarRemoteControlFragment.getGasValue() + "\r")
						.getBytes();
				gasPacket = new DatagramPacket(gasBuffer, gasBuffer.length,
						inet_addr, 5005);
				steeringBuffer = ("s:"
						+ CarRemoteControlFragment.getSteeringValue() + "\r")
						.getBytes();
				steeringPacket = new DatagramPacket(steeringBuffer,
						steeringBuffer.length, inet_addr, 5005);

				Log.e("GAS", "gas: " + CarRemoteControlFragment.getGasValue());
				Log.e("STEERING",
						"steering: "
								+ CarRemoteControlFragment.getSteeringValue());

				socket.send(gasPacket);
				socket.send(steeringPacket);

				Thread.sleep(500);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
