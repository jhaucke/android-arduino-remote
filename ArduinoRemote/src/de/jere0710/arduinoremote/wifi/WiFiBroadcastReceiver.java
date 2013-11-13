/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.jere0710.arduinoremote.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import de.jere0710.arduinoremote.activities.RemoteControlActivity;
import de.jere0710.arduinoremote.interfaces.Constants;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiBroadcastReceiver extends BroadcastReceiver implements
		Constants {

	private RemoteControlActivity remoteControlActivity;
	private WifiP2pManager manager;
	private Channel channel;

	public WiFiBroadcastReceiver(RemoteControlActivity remoteControlActivity,
			WifiP2pManager manager, Channel channel) {
		super();

		this.remoteControlActivity = remoteControlActivity;
		this.manager = manager;
		this.channel = channel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

			Log.e("#### Broadcast", "WIFI_P2P_STATE_CHANGED_ACTION");

			// UI update to indicate wifi p2p status.
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				// Wifi Direct mode is enabled
				remoteControlActivity.setIsWifiP2pEnabled(true);
			} else {
				remoteControlActivity.setIsWifiP2pEnabled(false);
			}
			Log.d(TAG, "P2P state changed - " + state);
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

			Log.e("#### Broadcast", "WIFI_P2P_PEERS_CHANGED_ACTION");

			manager.requestGroupInfo(channel, remoteControlActivity);

			// // request available peers from the wifi p2p manager. This is an
			// // asynchronous call and the calling activity is notified with a
			// // callback on PeerListListener.onPeersAvailable()
			// if (manager != null) {
			// manager.requestPeers(channel, (PeerListListener)
			// mMainActivity.getFragmentManager()
			// .findFragmentById(R.id.frag_list));
			// }
			// Log.d(WiFiDirectActivity.TAG, "P2P peers changed");
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
				.equals(action)) {

			Log.e("#### Broadcast", "WIFI_P2P_CONNECTION_CHANGED_ACTION");

			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkInfo.isConnected()) {

				// we are connected with another device
				remoteControlActivity
						.setMenuIconStateCheckBoxConnectionWithoutEvent(true);
			} else {
				// It's a disconnect
				remoteControlActivity
						.setMenuIconStateCheckBoxConnectionWithoutEvent(false);
			}
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
				.equals(action)) {

			Log.e("#### Broadcast", "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");

			// DeviceListFragment fragment = (DeviceListFragment)
			// mMainActivity.getFragmentManager()
			// .findFragmentById(R.id.frag_list);
			// fragment.updateThisDevice((WifiP2pDevice)
			// intent.getParcelableExtra(
			// WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

		}
	}
}
