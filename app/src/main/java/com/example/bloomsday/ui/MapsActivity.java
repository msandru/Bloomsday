package com.example.bloomsday.ui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.bloomsday.R;
import com.example.bloomsday.models.ClusterMarker;
import com.example.bloomsday.models.PolylineData;
import com.example.bloomsday.models.User;
import com.example.bloomsday.models.UserLocation;
import com.example.bloomsday.services.LocationService;
import com.example.bloomsday.util.DataBaseHelper;
import com.example.bloomsday.util.MyClusterManagerRenderer;
import com.example.bloomsday.util.UserClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;
    private ClusterMarker mClusterMarker;
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();
    private static final int LOCATION_UPDATE_INTERVAL = 2000;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private ClusterManager<ClusterMarker> mClusterManager;
    private final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003, ERROR_DIALOG_REQUEST = 9001;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private UserLocation mUserLocation;
    private LocationService locationService;
    private String TAG = "MapsActivity";
    private boolean mLocationPermissionGranted = false;
    private boolean bound;
    boolean userMarkerSet = false;
    private GeoApiContext mGeoApiContext;
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private Marker mSelectedMarker = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            locationService = ((LocationService.LocationBinder) iBinder).getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder().apiKey( getString( R.string.google_api_key ) ).build();
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {

            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle ) );

            if (!success) {
                Log.e( TAG, "Style parsing failed." );
            }
        } catch (Resources.NotFoundException e) {
            Log.e( TAG, "Can't find style. Error: ", e );
        }

        mMap = googleMap;
        addLandmarksMarker();
        mMap.setOnPolylineClickListener( this );
        setCameraView();
    }


    public boolean checkMapServices() {
        Log.d( TAG, "We verify whether or not we are allowed to use Google Play Services" );
        if (isServicesOK()) {
            Log.d( TAG, "We verify whether or not we are allowed to use GPS" );
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( "This application requires GPS to work properly, do you want to enable it?" )
                .setCancelable( false )
                .setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent( android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                        startActivityForResult( enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS );
                    }
                } );
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            Log.d( TAG, "The GPS access is not granted yet" );
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    public void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device.
         */
        Log.d( TAG, "getLocationPermission: called." );
        if (ContextCompat.checkSelfPermission( this.getApplicationContext(),
                ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Log.d( TAG, "Everything is fine!" );
            setUserDetails();
        } else {
            ActivityCompat.requestPermissions( this,
                    new String[]{ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION );
        }
    }


    public boolean isServicesOK() {
        Log.d( TAG, "isServicesOK: checking google services version" );

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable( MapsActivity.this );

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d( TAG, "isServicesOK: Google Play Services is working" );
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError( available )) {
            //an error occurred but we can resolve it
            Log.d( TAG, "isServicesOK: an error occurred but we can fix it" );
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog( MapsActivity.this, available, ERROR_DIALOG_REQUEST );
            dialog.show();
        } else {
            Toast.makeText( this, "You can't make map requests", Toast.LENGTH_SHORT ).show();
        }
        return false;
    }

    public void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent( this, LocationService.class );

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                MapsActivity.this.startForegroundService( serviceIntent );
            } else {
                bindService( serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE );
            }
        }
    }

    public boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService( ACTIVITY_SERVICE );
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices( Integer.MAX_VALUE )) {
            if ("com.example.bloomsday.services.LocationService".equals( service.service.getClassName() )) {
                Log.d( TAG, "isLocationServiceRunning: location service is already running." );
                return true;
            }
        }
        Log.d( TAG, "isLocationServiceRunning: location service is not running." );
        return false;
    }

    public void startUserLocationsRunnable() {
        Log.d( TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations." );
        mHandler.postDelayed( mRunnable = new Runnable() {
            @Override
            public void run() {
                retrieveUserLocation();
                mHandler.postDelayed( mRunnable, LOCATION_UPDATE_INTERVAL );
            }
        }, LOCATION_UPDATE_INTERVAL );
    }

    public void createMapMarker(GoogleMap map, double lat, double lon, String title, int avatar, String snippet) {

        if (mMap != null) {

            if (mClusterManager == null) {
                mClusterManager = new ClusterManager<ClusterMarker>( this.getApplicationContext(), mMap );
            }
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        this,
                        mMap,
                        mClusterManager
                );
                mClusterManager.setRenderer( mClusterManagerRenderer );
                /**The customised rendered is applied over the Cluster Manager or over all the Cluster Markers**/
            }
        }

        ClusterMarker newClusterMarker = new ClusterMarker(
                new LatLng( lat, lon ),
                title,
                avatar,
                snippet
        );
        if (snippet == "This is you")
            mClusterMarker = newClusterMarker;
        mClusterManager.addItem( newClusterMarker );
        mClusterManager.cluster();
    }

    public void addLandmarksMarker() {

        DataBaseHelper dataBaseHelper = new DataBaseHelper( this );
        Cursor cursor;
        cursor = dataBaseHelper.getAllData();

        if (cursor.getCount() == 0) {
            Log.d( "DataBaseHelper", "Error getting data" );
        }

        while (cursor.moveToNext()) {
            Log.d(TAG, "The cursor points to latitude: " + cursor.getDouble( 1 ));
            Log.d(TAG, "The cursor points to longitude: " + cursor.getDouble( 2 ));
            createMapMarker( mMap, cursor.getDouble( 1 ), cursor.getDouble( 2 ), cursor.getString( 3 ), cursor.getInt( 4 ), cursor.getString( 5 ) );
        }

        cursor.close();
        dataBaseHelper.close();
        mMap.setOnInfoWindowClickListener( this );
    }

    public void addUserMarkers() {

        try {
            String snippet = "This is you";
            createMapMarker( mMap, mUserLocation.getGeo_point().getLatitude(), mUserLocation.getGeo_point().getLongitude(), mUserLocation.getUser().getUsername(), Integer.parseInt( mUserLocation.getUser().getAvatar() ), snippet );
            Log.d( TAG, "The user marker has been added successfully" );

        } catch (Exception e) {
        }
    }

    public void setCameraView() {

        // Set a boundary to start
        //When the map is displayed we want the focus to be on Dublin and its surroundings
        LatLng dublin = new LatLng( 53.350140, -6.266155 );
        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( dublin, 10 ) );

    }

    public void setUserDetails() {
        if (mUserLocation == null) {
            mUserLocation = new UserLocation();
            DocumentReference userRef = FirebaseFirestore.getInstance().collection( getResources().getString( R.string.collection_users ) ).
                    document( FirebaseAuth.getInstance().getCurrentUser().getUid() );
            userRef.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User user = task.getResult().toObject( User.class );
                    mUserLocation.setUser( user );
                    ((UserClient) (getApplicationContext())).setUser( user );
                    startLocationService();

                }
            } );
        }
    }

    public void retrieveUserLocation() {
        final DocumentReference userLocationReference = FirebaseFirestore.getInstance().collection( getResources().getString( R.string.collection_user_locations ) ).
                document( FirebaseAuth.getInstance().getCurrentUser().getUid() );
        userLocationReference.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    final UserLocation updatedUserLocation = task.getResult().toObject( UserLocation.class );

                    try {
                        GeoPoint geoPoint = new GeoPoint(
                                updatedUserLocation.getGeo_point().getLatitude(),
                                updatedUserLocation.getGeo_point().getLongitude()
                        );

                        if (userMarkerSet == false) {
                            mUserLocation.setGeo_point( geoPoint );
                            addUserMarkers();
                            userMarkerSet = true;
                        }
                        mClusterMarker.setPosition( new LatLng( geoPoint.getLatitude(), geoPoint.getLongitude() ) );
                        mClusterManagerRenderer.setUpdateMarker( mClusterMarker );

                    } catch (NullPointerException e) {
                        Log.e( TAG, "retrieveUserLocations: NullPointerException: " + e.getMessage() );
                    }
                }
            }
        } );
    }

    private void removeTripMarkers(){
        for(Marker marker: mTripMarkers){
            marker.remove();
        }
    }

    private void resetSelectedMarker(){
        if(mSelectedMarker != null){
            mSelectedMarker.setVisible(true);
            mSelectedMarker = null;
            removeTripMarkers();
        }
    }


    private void calculateDirections(Marker marker) {
        Log.d( TAG, "calculateDirections: calculating directions." );

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest( mGeoApiContext );

        directions.alternatives( true );
        directions.origin( new com.google.maps.model.LatLng( mClusterMarker.getPosition().latitude, mClusterMarker.getPosition().longitude ) );
        Log.d( TAG, "calculateDirections: destination: " + destination.toString() );
        directions.destination( destination ).setCallback( new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d( TAG, "calculateDirections: routes: " + result.routes[0].toString() );
                Log.d( TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration );
                Log.d( TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance );
                Log.d( TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString() );

                Log.d( TAG, "onResult: successfully retrieved directions." );
                addPolylinesToMap( result );
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e( TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        } );
    }


    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                if(mPolyLinesData.size() > 0){
                    for(PolylineData polylineData: mPolyLinesData){
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }

                double duration = 999999999;
                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(MapsActivity.this, R.color.darkGrey));
                    polyline.setClickable(true);
                    mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));

                    // highlight the fastest route and adjust camera
                    double tempDuration = route.legs[0].duration.inSeconds;
                    if(tempDuration < duration){
                        duration = tempDuration;
                        onPolylineClick(polyline);
                    }

                    mSelectedMarker.setVisible(false);
                }
            }
        });
    }


    public void onInfoWindowClick(final Marker marker) {
        if(marker.getTitle().contains("Trip")){
            final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setMessage("Would you like to open Google Maps?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            String latitude = String.valueOf(marker.getPosition().latitude);
                            String longitude = String.valueOf(marker.getPosition().longitude);
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            try{
                                if (mapIntent.resolveActivity(MapsActivity.this.getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                }
                            }catch (NullPointerException e){
                                Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                                Toast.makeText(MapsActivity.this, "Couldn't open map", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
        else{
            if (marker.getSnippet().equals( "This is you" )) {
                marker.hideInfoWindow();
            } else {

                final AlertDialog.Builder builder = new AlertDialog.Builder( MapsActivity.this );
                builder.setMessage( "Would you like to visit " + marker.getTitle() + " ?" );
                builder.setCancelable( true );
                builder.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        resetSelectedMarker();
                        mSelectedMarker = marker;
                        calculateDirections( marker );
                        dialog.dismiss();
                    }
                } );
                builder.setNegativeButton( "No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                } );
                final AlertDialog alert = builder.create();
                alert.show();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                Log.d( TAG, "We have been grated permission to Google Play Services, GPS and locations" );
                //One singleton object is created that contains all the data of a user, the userLocation gets the data of the user and the serviceLocation is running
                setUserDetails();


            } else
                getLocationPermission();

            //The app retrieved constantly the location coordinates from the database updated in the service and updates the custom map markers
            startUserLocationsRunnable();

        }


    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        int index = 0;
        for (PolylineData polylineData : mPolyLinesData) {
            index++;
            Log.d( TAG, "onPolylineClick: toString: " + polylineData.toString() );
            if (polyline.getId().equals( polylineData.getPolyline().getId() )) {
                polylineData.getPolyline().setColor( ContextCompat.getColor( this, R.color.blue ) );
                polylineData.getPolyline().setZIndex( 1 );

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                Marker marker = mMap.addMarker( new MarkerOptions()
                        .position( endLocation )
                        .title( "Trip #" + index )
                        .snippet( "Duration: " + polylineData.getLeg().duration
                        ) );

                mSelectedMarker.setVisible( false );
                mTripMarkers.add( marker );

                marker.showInfoWindow();

            } else {
                polylineData.getPolyline().setColor( ContextCompat.getColor( this, R.color.darkGrey ) );
                polylineData.getPolyline().setZIndex( 0 );
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService( serviceConnection );
            bound = false;
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


}
