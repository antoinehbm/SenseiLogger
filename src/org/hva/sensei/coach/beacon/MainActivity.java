package org.hva.sensei.coach.beacon;

import org.hva.sensei.coach.configuring.BeaconConnection;
import org.hva.sensei.coach.configuring.BeaconConnectionListener;
import org.hva.sensei.coach.scanning.BLEScan;
import org.hva.sensei.coach.scanning.beaconListener;
import org.hva.sensei.logger.R;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity implements beaconListener, BeaconConnectionListener {

	BLEScan leScanner;
	AbstractBeacon lfb = null;
	BeaconConnection beaconConnection;

	TextView statusLabel = null;

	boolean connected = false;

	boolean vibrating = false;

	boolean leach = false;

	TextView distance = null;
	TextView battery = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_coach);
		statusLabel = (TextView) findViewById(R.id.status_label);
		distance = (TextView) findViewById(R.id.distance);
		battery = (TextView) findViewById(R.id.battery);
		statusLabel.setText("Disconnected");
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		if (savedInstanceState == null) {
		}
		
		SeekBar seek = (SeekBar) findViewById(R.id.vibratespeed);
		seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			int seekbar_value = 0;

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				seekbar_value = progress;
				onStopTrackingTouch(seekBar);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				beaconConnection.transmitDataWithoutResponse("AT+PIO2"+seekbar_value);
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	Ringtone r;

	@Override
	public void beaconFound(AbstractBeacon b) {
		if (b.getDevice().getAddress().trim().equals("20:CD:39:AD:67:ED")) {
			statusLabel.setText("Found your bracelet");
			GlimwormBeacon glb = (GlimwormBeacon)b;
			battery.setText("Battery level:"+glb.getBatteryLevel()+"%");
			lfb = b;
			if (leach) {
				distance.setText("Distance to bracelet: " + lfb.getDistance() + "");
				if (lfb.getDistance() > 15) {
					try {

						r.play();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					r.stop();
				}
			}
		}

	}

	public void startScan(View v) {
		if (!connected) {
			leScanner = new BLEScan(this, 2000);
			leScanner.addBeaconListener(this);
			leScanner.startScan();
		}
	}

	public void startConnect(View v) {
		if (connected) {
			beaconConnection.Disconnect();
		} else {
			if (lfb != null) {
				leScanner.stopScan();
				beaconConnection = new BeaconConnection(this, lfb.getDevice().getAddress().trim());
				beaconConnection.addListener(this, 0);
				beaconConnection.Connect();
			}
		}
	}

	public void vibrate(View v) {
		if (connected) {
			if (!vibrating) {
				beaconConnection.transmitDataWithoutResponse("AT+PIO21");
				statusLabel.setText("Shaking your bracelet");
				vibrating = true;
			} else {
				beaconConnection.transmitDataWithoutResponse("AT+PIO20");
				statusLabel.setText("Connected to your bracelet");
				vibrating = false;
			}
		}
	}

	public void leach(View v) {
		if (leScanner != null)
			leScanner.stopScan();
		leScanner = new BLEScan(this, 2000);
		leScanner.addBeaconListener(this);
		leScanner.startIntervalScan(5000);
		leach = true;
	}

	@Override
	public void beaconConnected() {
		statusLabel.setText("Connected to your bracelet");
		connected = true;
		TextView tv = (TextView) findViewById(R.id.connect);
		tv.setText("Disconnect");
	}

	@Override
	public void beaconSystemDisconnected() {
		statusLabel.setText("Disconnected");
		connected = false;
		TextView tv = (TextView) findViewById(R.id.connect);
		tv.setText("Connect");
	}

	@Override
	public void beaconUserDisconnected() {
		statusLabel.setText("Disconnected");
		connected = false;
		TextView tv = (TextView) findViewById(R.id.connect);
		tv.setText("Connect");
	}

	@Override
	public void dataReceived(BeaconMessage bm) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scanningStarted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scanningStopped() {
		// TODO Auto-generated method stub

	}

	boolean led1on = false, led2on = false, led3on = false, led4on = false, led5on = false, led6on = false, led7on = false, led8on = false;

	public void ledOne(View v) {
		if (connected) {
			if (!led1on) {
				beaconConnection.transmitData("AT+PIO31");
				led1on = true;
			} else {
				beaconConnection.transmitData("AT+PIO30");
				led1on = false;
			}
		}
	}
	
	
	public void ledTwo(View v) {
		if (connected) {
			if (!led2on) {
				beaconConnection.transmitData("AT+PIO41");
				led2on = true;
			} else {
				beaconConnection.transmitData("AT+PIO40");
				led2on = false;
			}
		}
	}
	
	public void ledThree(View v) {
		if (connected) {
			if (!led3on) {
				beaconConnection.transmitData("AT+PIO51");
				led3on = true;
			} else {
				beaconConnection.transmitData("AT+PIO50");
				led3on = false;
			}
		}
	}
	
	public void ledFour(View v) {
		if (connected) {
			if (!led4on) {
				beaconConnection.transmitData("AT+PIO61");
				led4on = true;
			} else {
				beaconConnection.transmitData("AT+PIO60");
				led4on = false;
			}
		}
	}
	
	
	public void ledFive(View v) {
		if (connected) {
			if (!led5on) {
				beaconConnection.transmitData("AT+PIO71");
				led5on = true;
			} else {
				beaconConnection.transmitData("AT+PIO70");
				led5on = false;
			}
		}
	}
	
	public void ledSix(View v) {
		if (connected) {
			if (!led6on) {
				beaconConnection.transmitData("AT+PIO81");
				led6on = true;
			} else {
				beaconConnection.transmitData("AT+PIO80");
				led6on = false;
			}
		}
	}
	
	public void ledSeven(View v) {
		if (connected) {
			if (!led7on) {
				beaconConnection.transmitData("AT+PIO91");
				led7on = true;
			} else {
				beaconConnection.transmitData("AT+PIO90");
				led7on = false;
			}
		}
	}
	
	public void ledEight(View v) {
		if (connected) {
			if (!led8on) {
				beaconConnection.transmitData("AT+PIOA1");
				led8on = true;
			} else {
				beaconConnection.transmitData("AT+PIOA0");
				led8on = false;
			}
		}
	}

}
