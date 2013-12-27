package com.gideon.address;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddressActivity extends Activity {
    /** Called when the activity is first created. */
	String lat="", lon="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	Button btnLocation = (Button)findViewById(R.id.btnLocation);
		btnLocation.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// Acquire a reference to the system Location Manager
				LocationManager locationManager = (LocationManager) AddressActivity.this.getSystemService(Context.LOCATION_SERVICE);
				// Define a listener that responds to location updates
				LocationListener locationListener = new LocationListener() {
					public void onLocationChanged(Location location) {
						// Called when a new location is found by the network location provider.
						lat = Double.toString(location.getLatitude());
						lon = Double.toString(location.getLongitude());
						TextView tv = (TextView) findViewById(R.id.txtLoc);
						tv.setText("Your Location is:" + lat + "--" + lon);
					}

					public void onStatusChanged(String provider, int status, Bundle extras) {}
					public void onProviderEnabled(String provider) {}
					public void onProviderDisabled(String provider) {}
				};
				// Register the listener with the Location Manager to receive location updates
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			}
		});

		Button btnSend = (Button)findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				postData(lat, lon);
			}
		});

		Button btnAdd = (Button)findViewById(R.id.btnAddress);
		btnAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView tv = (TextView)findViewById(R.id.txtAddress);
				tv.setText(GetAddress(lat, lon));
			}
		});

	}

    public void postData(String la, String lo) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost  httppost = new HttpPost("http://54.200.84.125/test.php");

		try {
			// Execute HTTP Post Request
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("la",la));
			nameValuePairs.add(new BasicNameValuePair("lo",lo));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			HttpResponse response = httpclient.execute(httppost);
			String resp = response.getStatusLine().toString();
			Toast.makeText(this, resp, 5000).show();


		} catch (ClientProtocolException e) {
			Toast.makeText(this, "Error", 5000).show();
		} catch (IOException e) {
			Toast.makeText(this, "Error", 5000).show();
		}
	} 
	public String GetAddress(String lat, String lon)
	{
		Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
		String ret = "";
		try {
			List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
			if(addresses != null) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
				for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
				}
				ret = strReturnedAddress.toString();
			}
			else{
				ret = "No Address returned!";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = "Can't get Address!";
		}
		return ret;
	}
}