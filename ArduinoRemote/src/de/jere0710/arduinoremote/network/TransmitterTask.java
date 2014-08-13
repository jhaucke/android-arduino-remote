package de.jere0710.arduinoremote.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import de.jere0710.arduinoremote.fragments.CarRemoteControlFragment;

public class TransmitterTask extends AsyncTask<String, Integer, Boolean> {

	private InetAddress inet_addr;
	private DatagramSocket socket;

	private byte[] carControlBuffer;

	private DatagramPacket carControlPacket;

	@Override
	protected Boolean doInBackground(String... arg0) {

		byte[] ip_bytes = new byte[] { (byte) 192, (byte) 168, (byte) 240,
				(byte) 1 };
		try {
			inet_addr = InetAddress.getByAddress(ip_bytes);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		try {
			socket = new DatagramSocket();

			while (true) {
				carControlBuffer = (CarRemoteControlFragment.getSteeringValue()
						+ ":" + CarRemoteControlFragment.getGasValue())
						.getBytes();
				carControlPacket = new DatagramPacket(carControlBuffer,
						carControlBuffer.length, inet_addr, 5005);

				// Log.e("STEERING",
				// "steering: "
				// + CarRemoteControlFragment.getSteeringValue());
				// Log.e("GAS", "gas: " +
				// CarRemoteControlFragment.getGasValue());

				socket.send(carControlPacket);

				Thread.sleep(10);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
