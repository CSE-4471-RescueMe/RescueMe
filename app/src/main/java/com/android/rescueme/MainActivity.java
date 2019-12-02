package com.android.rescueme;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private TabLayout mTabLayout;
    private TabItem mTabItemHome;
    private TabItem mTabItemEmergency;
    private TabItem mTabItemSettings;
    private ViewPager mViewPager;
    private PagerController mPagerController;

    // variables for getting location
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private Location location;
    private String provider;
    private String finalAddress = "";
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 11;
    private static final int MY_PERMISSION_SEND_SMS = 12;
    final static int VIDEO_CAPTURED = 1;
    private AddressResultReceiver myResultReciever;

    Uri uriVideo = null;

    private TextView mContactFullName;
    private TextView mContactEmail;
    private TextView mContactPhoneNumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContactFullName = findViewById(R.id.contact_name);
        mContactEmail = findViewById(R.id.contact_email);
        mContactPhoneNumer = findViewById(R.id.contact_phone_number);

        mTabLayout = findViewById(R.id.tab_layout);
        mTabItemHome = findViewById(R.id.tab_home);
        mTabItemEmergency = findViewById(R.id.tab_emergency);
        mTabItemSettings = findViewById(R.id.tab_settings);
        mViewPager = findViewById(R.id.view_pager);

        mPagerController = new PagerController(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mPagerController);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        // Try to obtain user's location upon opening the app
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        tryToGetLocation();
        tryToGetLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission( this,android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,
                    new String [] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    MY_PERMISSION_ACCESS_FINE_LOCATION
            );
        }
        else {
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        final double lat = location.getLatitude();
        final double lng = location.getLongitude();

        latitude = lat;
        longitude = lng;
        System.out.println("lat and long: " + lat + ", " + lng);

        myResultReciever = new AddressResultReceiver(new android.os.Handler());
        startIntentService();
        System.out.println("finalAddress: " + finalAddress);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // May not need code here.
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
        System.out.println("Provider enabled: " + provider);

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
        System.out.println("Provider disabled: " + provider);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    tryToGetLocation();
                } else {
                    // permission denied, either disable the location functionality that depends on this permission, or continue asking for permission.
                    // We continually ask for permission since RescueMe needs to get the user's location so RescueMe can send the location to the user's emergency contact.
                    //showAlert(getString(R.string.error), getString(R.string.message));
                }
            }
            case MY_PERMISSION_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    System.out.println("Permission granted to send sms!");
                } else {
                    System.out.println("Permission denied to send sms!");
                    // permission denied, either disable the location functionality that depends on this permission, or continue asking for permission.
                    // We continually ask for permission since RescueMe needs to get the user's location so RescueMe can send the location to the user's emergency contact.
                    //showAlert(getString(R.string.error), getString(R.string.message));
                }
            }
        }
    }

    // Checks if the user has provided permission to use their location. If not, then request the user's permission. If permission has been granted, obtain the user's location.
    public void tryToGetLocation () {
        if (ContextCompat.checkSelfPermission( this,android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,
                    new String [] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    MY_PERMISSION_ACCESS_FINE_LOCATION
            );
        }
        else {
            // We have permission to obtain user's location
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            provider = locationManager.getBestProvider(criteria, false);
            location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                onLocationChanged(location);
            }
            else {
                locationManager.requestLocationUpdates(provider, 1000, 0, this);
            }
        }
    }

    protected void startIntentService(){
        Intent intent = new Intent(this,FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER,myResultReciever);
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA,location);
        startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            finalAddress = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);
            if (finalAddress == null) {
                finalAddress = "";
            }

            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
            }

        }
    }

    public static class FetchAddressIntentService extends IntentService {
        ResultReceiver myReceiver;
        public FetchAddressIntentService(){
            super("Fetching address");

        }


        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String errorMessage = "";
            //Get the location passed to this service through an extra
            Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
            myReceiver = intent.getParcelableExtra(Constants.RECEIVER);

            List<Address> address = null;
            try{
                address = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            }
            catch(Exception e){
                // Calling toast within an intent is problematic
                // Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();
            }

            //Handle the case when there is no location found
            if(address == null || address.size() == 0){
                // Calling toast within an intent is problematic
                //Toast.makeText(this, "No address found", Toast.LENGTH_LONG).show();
                deliverResulttoReciever(Constants.FAILURE_RESULT,"No address Found");
            }
            else{
                Address currentAddress = address.get(0);
                ArrayList<String> addressFragment = new ArrayList<String>();

                //Fetch the address lines using getAddressLine
                //join them and send them to the thread
                for(int i = 0;i<=currentAddress.getMaxAddressLineIndex();i++)
                {
                    addressFragment.add(currentAddress.getAddressLine(i));
                }
                deliverResulttoReciever(Constants.SUCCESS_RESULT, TextUtils.join(System.getProperty("line.saparator"),addressFragment));
            }


        }

        private void deliverResulttoReciever(int resultCode, String message) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.RESULT_DATA_KEY,message);
            myReceiver.send(resultCode,bundle);
        }

        public final class Constants {
            public static final int SUCCESS_RESULT = 0;
            public static final int FAILURE_RESULT = 1;
            public static final String PACKAGE_NAME =
                    "com.google.android.gms.location.sample.locationaddress";
            public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
            public static final String RESULT_DATA_KEY = PACKAGE_NAME +
                    ".RESULT_DATA_KEY";
            public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
                    ".LOCATION_DATA_EXTRA";
        }

    }

    private void requestSmsPermission() {
        String permission = Manifest.permission.READ_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    public void sendMessage(android.view.View view) {
        // try to get permission to send sms
        if (ContextCompat.checkSelfPermission( this,android.Manifest.permission.SEND_SMS ) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,
                    new String [] { android.Manifest.permission.SEND_SMS },
                    MY_PERMISSION_SEND_SMS
            );
        }
        else {
            // We have permission to send sms
            String message = "";
            if (finalAddress.equals("")) {
                message = "Help me at: http://maps.google.com/maps?saddr=" + latitude + "," + longitude;
            } else {
                message = "Help me at: " + finalAddress;
            }
            SmsManager smsManger = SmsManager.getDefault();
            StringBuffer smsBody = new StringBuffer();
            smsBody.append(Uri.parse(message));
            android.telephony.SmsManager.getDefault().sendTextMessage("5554", null, smsBody.toString(), null, null);
            sendVideo();
        }
    }

    public void sendVideo() {
        Intent captureVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        captureVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        captureVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(captureVideoIntent, VIDEO_CAPTURED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == VIDEO_CAPTURED){
                uriVideo = data.getData();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra("address", "6505551212");
                intent.putExtra("sms_body", "video: ");
                intent.putExtra(Intent.EXTRA_STREAM, uriVideo);
                //intent.setType("video/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);}
                //Toast.makeText(MainActivity.this, uriVideo.getPath(), Toast.LENGTH_LONG).show();
            }
        }else if(resultCode == RESULT_CANCELED){
            uriVideo = null;
            //Toast.makeText(MainActivity.this,"Cancelled!",Toast.LENGTH_LONG).show();
        }
    }
}