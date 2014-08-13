package de.jere0710.arduinoremote.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import de.jere0710.arduinoremote.R;
import de.jere0710.arduinoremote.fragments.CarRemoteControlFragment;
import de.jere0710.arduinoremote.fragments.ConnectionDialogFragment;
import de.jere0710.arduinoremote.fragments.CustomRemoteControlFragment;
import de.jere0710.arduinoremote.fragments.WiFiOffDialogFragment;
import de.jere0710.arduinoremote.interfaces.Constants;
import de.jere0710.arduinoremote.network.WiFiBroadcastReceiver;

public class RemoteControlActivity extends Activity implements Constants,
		ActionBar.OnNavigationListener, OnCheckedChangeListener,
		GroupInfoListener, ChannelListener {

	/**
	 * Wird vom Bundle als auch vom Broadcast
	 * (WIFI_P2P_CONNECTION_CHANGED_ACTION) gesetzt!
	 */
	private Boolean menuCheckBoxConnectionState = null;
	private WifiP2pManager manager;
	private boolean isWifiP2pEnabled = false;
	private boolean retryChannel = false;

	/**
	 * 0 = no network, ready for new one 1 = network created
	 */
	private int networkState;
	private SharedPreferences sharedPref;

	private final IntentFilter intentFilter = new IntentFilter();
	private Channel channel;
	private BroadcastReceiver receiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_remote_control);

		sharedPref = getPreferences(MODE_PRIVATE);
		networkState = sharedPref.getInt(VARIABLE_NETWORKSTATE, 0);

		// add necessary intent values to be matched.

		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.title_section1),
								getString(R.string.title_section2), }), this);

		if (savedInstanceState != null) {

			if (savedInstanceState.containsKey(MENU_CHECKBOXCONNECTION)) {
				menuCheckBoxConnectionState = savedInstanceState
						.getBoolean(MENU_CHECKBOXCONNECTION);
			}
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {

		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}

		// if (savedInstanceState.containsKey(VARIABLE_NETWORKSTATE)) {
		// networkState = savedInstanceState.getInt(VARIABLE_NETWORKSTATE);
		// }
	}

	/** register the BroadcastReceiver with the intent values to be matched */
	@Override
	public void onResume() {
		super.onResume();

		receiver = new WiFiBroadcastReceiver(this, manager, channel);
		registerReceiver(receiver, intentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();

		unregisterReceiver(receiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_remote_control, menu);

		if (menuCheckBoxConnectionState != null) {

			CheckBox cBox = (CheckBox) menu.findItem(R.id.menu_connection)
					.getActionView().findViewById(R.id.checkBoxConnection);
			cBox.setChecked(menuCheckBoxConnectionState);
		}

		CheckBox cBox = (CheckBox) menu.findItem(R.id.menu_connection)
				.getActionView().findViewById(R.id.checkBoxConnection);
		cBox.setOnCheckedChangeListener(this);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());

		outState.putBoolean(MENU_CHECKBOXCONNECTION,
				((CheckBox) findViewById(R.id.checkBoxConnection)).isChecked());

		// outState.putInt(VARIABLE_NETWORKSTATE, networkState);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {

		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(VARIABLE_NETWORKSTATE, networkState);
		editor.commit();
		super.onDestroy();
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {

		switch (position) {
		case 0:

			getFragmentManager().beginTransaction()
					.replace(R.id.container, new CarRemoteControlFragment())
					.commit();

			return true;
		case 1:

			getFragmentManager().beginTransaction()
					.replace(R.id.container, new CustomRemoteControlFragment())
					.commit();
			return true;
		}
		return true;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		switch (buttonView.getId()) {
		case R.id.checkBoxConnection:

			handleConnection(buttonView, isChecked);
			break;

		default:
			break;
		}
	}

	private void handleConnection(CompoundButton view, boolean isChecked) {

		if (isChecked) {

			if (!isWifiP2pEnabled) {

				view.setChecked(false);
				DialogFragment dFragment = new WiFiOffDialogFragment();
				dFragment.show(getFragmentManager(), TAG_WIFIOFF_DIALOG);
				return;
			}
			createWiFiNetwork();
		} else {

			removeWiFiNetwork();
		}

	}

	public void setMenuCheckBoxConnectionState(
			Boolean menuCheckBoxConnectionState) {

		this.menuCheckBoxConnectionState = menuCheckBoxConnectionState;
	}

	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {

		this.isWifiP2pEnabled = isWifiP2pEnabled;
	}

	public void setMenuIconStateCheckBoxConnectionWithoutEvent(
			boolean isIconChecked) {

		setMenuCheckBoxConnectionState(isIconChecked);

		CheckBox cBox = (CheckBox) findViewById(R.id.checkBoxConnection);
		if (cBox != null) {

			cBox.setOnCheckedChangeListener(null);
			cBox.setChecked(menuCheckBoxConnectionState);
			cBox.setOnCheckedChangeListener(this);
		}
	}

	public void createWiFiNetwork() {

		manager.createGroup(channel, new ActionListener() {

			@Override
			public void onSuccess() {

				Toast.makeText(RemoteControlActivity.this, "creating...",
						Toast.LENGTH_LONG).show();

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {

						networkState = 0;
						manager.requestGroupInfo(channel,
								RemoteControlActivity.this);
					}
				}, 4000);
			}

			@Override
			public void onFailure(int reasonCode) {
				Toast.makeText(RemoteControlActivity.this,
						"Creation Failed : " + reasonCode, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	public void removeWiFiNetwork() {

		manager.removeGroup(channel, new ActionListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(RemoteControlActivity.this,
						"removing network...", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int reasonCode) {
				Toast.makeText(RemoteControlActivity.this,
						"removing network failed!", Toast.LENGTH_SHORT).show();

			}
		});
	}

	@Override
	public void onGroupInfoAvailable(WifiP2pGroup group) {

		if (group != null) {

			Log.e("########### My WiFi-Test App", group.getPassphrase());
			Log.e("########### My WiFi-Test App", group.getNetworkName());
			Log.e("########### My WiFi-Test App", group.getInterface());
			Log.e("########### My WiFi-Test App", group.getClientList().size()
					+ "");
			Log.e("########### My WiFi-Test App", group.getOwner().deviceName);
			Log.e("########### My WiFi-Test App",
					group.getOwner().deviceAddress);

			int numberOfConnectedClients = group.getClientList().size();

			DialogFragment dFragment = (DialogFragment) getFragmentManager()
					.findFragmentByTag(TAG_CONNECTION_DIALOG);

			if (numberOfConnectedClients == 0 && dFragment == null
					&& networkState == 0) {

				dFragment = new ConnectionDialogFragment();
				Bundle args = new Bundle();
				args.putString(ConnectionDialogFragment.ARG_NETWORKNAME,
						group.getNetworkName());
				args.putString(ConnectionDialogFragment.ARG_PASSPHRASE,
						group.getPassphrase());
				dFragment.setArguments(args);
				dFragment.show(getFragmentManager(), TAG_CONNECTION_DIALOG);
				networkState = 1;
			} else if (numberOfConnectedClients == 1 && dFragment != null) {

				dFragment.dismiss();
			} else if (numberOfConnectedClients > 1) {

				Toast.makeText(this, "More than one client is not supported!",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onChannelDisconnected() {
		// we will try once more
		if (manager != null && !retryChannel) {
			Toast.makeText(this, "Channel lost. Trying again",
					Toast.LENGTH_LONG).show();
			// resetData();
			retryChannel = true;
			manager.initialize(this, getMainLooper(), this);
		} else {
			Toast.makeText(
					this,
					"Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
					Toast.LENGTH_LONG).show();
		}
	}
}
