package mx.androidtititlan.googleplusexample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusShare;

public class MainActivity extends Activity implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener {
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	private Button deepShare;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPlusClient = new PlusClient.Builder(this, this, this)
				.setVisibleActivities("http://schemas.google.com/AddActivity",
						"http://schemas.google.com/BuyActivity").build();
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signin in...");
		setContentView(R.layout.activity_main);
		findViewById(R.id.sign_in_button).setOnClickListener(this);
		deepShare = (Button) findViewById(R.id.deeplink);
		deepShare.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button && !mPlusClient.isConnected()) {
			if (mConnectionResult == null) {
				mConnectionProgressDialog.show();
			} else {
				try {
					mConnectionResult.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mConnectionResult = null;
					mPlusClient.connect();
					Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
		if (view.getId() == R.id.deeplink) {
			Intent shareIntent = new PlusShare.Builder(this)
					.setText("Este es mi ejemplo: http://androidtitlan.mx")
					.setType("text/plain")
					.setContentUrl(Uri.parse("http://androidtitlan.mx"))
					.getIntent();
			startActivityForResult(shareIntent, 0);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
			break;
		case R.id.action_signout:
			doGooglePlusSignOut();
			break;
		default:
			break;
		}
		return true;
	}

	private void doGooglePlusSignOut() {
		if (mPlusClient.isConnected()) {
			mPlusClient.clearDefaultAccount();
			mPlusClient.disconnect();
			mPlusClient.connect();
			Toast.makeText(this, "SignOut", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mConnectionProgressDialog.isShowing()) {
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException exception) {
					mPlusClient.connect();

				}

			}
		}
		mConnectionResult = result;
	}

	@Override
	public void onConnected(Bundle arg0) {
		mConnectionProgressDialog.dismiss();
		Toast.makeText(this, "User has connected", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent data) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR
				&& responseCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPlusClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}

	@Override
	public void onDisconnected() {
	}

}
