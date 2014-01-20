package de.jere0710.arduinoremote.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class UdpTask extends AsyncTask<String, Integer, Boolean> {

	InetAddress inet_addr;
	DatagramSocket socket;

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
		byte[] buffer = (arg0[0] + "\r").getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
				inet_addr, 5005);
		try {
			socket = new DatagramSocket();
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
