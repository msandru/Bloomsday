package com.example.bloomsday.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.bloomsday.R;
import com.example.bloomsday.models.User;
import com.example.bloomsday.models.UserLocation;
import com.example.bloomsday.util.UserClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private final IBinder binder = new LocationBinder();
    private final static long FASTEST_INTERVAL = 2000; /* 3 sec */


    public class LocationBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel( CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT );

            ((NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE )).createNotificationChannel( channel );

            Notification notification = new NotificationCompat.Builder( this, CHANNEL_ID )
                    .setContentTitle( "" )
                    .setContentText( "" ).build();

            startForeground( 1, notification );
            getUserLocation();
        }

    }

    public void getUserLocation() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    User user = ((UserClient) (getApplicationContext())).getUser();
                    GeoPoint geoPoint = new GeoPoint( location.getLatitude(), location.getLongitude() );
                    final UserLocation userLocation = new UserLocation(user, geoPoint, null);

                    try {
                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection( getResources().getString( R.string.collection_user_locations ) )
                                .document( FirebaseAuth.getInstance().getCurrentUser().getUid() );
                        documentReference.set( userLocation ).addOnCompleteListener( new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d( TAG, "onComplete: \ninserted user location into database." +
                                            "\n latitude: " + userLocation.getGeo_point().getLatitude() +
                                            "\n longitude: " + userLocation.getGeo_point().getLongitude() );
                                }
                            }
                        } );
                    } catch (NullPointerException e) {
                        Log.e( TAG, "saveUserLocation: User instance is null" );
                        Log.e( TAG, "saveUserLocation: NullPointerException: " + e.getMessage() );
                        stopSelf();
                    }

                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }

        };
        LocationManager locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );
        if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, FASTEST_INTERVAL, 1, locationListener );
    }


}
