package com.zahergoesapp.travelappilka;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebStorage;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zahergoesapp.travelappilka.Modules.DirectionFinder;
import com.zahergoesapp.travelappilka.Modules.DirectionFinderListener;
import com.zahergoesapp.travelappilka.Modules.Route;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private Button showinngPath;
    private Button SLtrafik;
    private EditText OriginInputText;
    private EditText DesitinationInputText;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private FirebaseAuth fireAuth;
    private DatabaseReference databaseReference;
    private Button logOut;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fireAuth = FirebaseAuth.getInstance();
        if(fireAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }
        SLtrafik = (Button) findViewById(R.id.SLButton);
        showinngPath = (Button) findViewById(R.id.showinngPath);
        OriginInputText = (EditText) findViewById(R.id.OriginInputText);
        DesitinationInputText = (EditText) findViewById(R.id.DesitinationInputText);
        logOut = (Button) findViewById(R.id.LogOutButton);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v== logOut){
                    fireAuth.signOut();
                    signOut();
                    finish();
                }
            }
        });



        showinngPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
                saveDestinations();
            }
        });
        SLtrafik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                getToSLinfo();
            }
        });
    }

      public void getToSLinfo(){
          Intent intent = new Intent(this, SLTrafikJsonAcivity.class);
          startActivity(intent);
      }




        private void sendRequest() {
        String OriginInput = OriginInputText.getText().toString();
        String DestinationInput = DesitinationInputText.getText().toString();
            if(OriginInput.isEmpty()){
                Toast.makeText(this, "Please enter your staring address", Toast.LENGTH_SHORT).show();
                return;
            }

            if(DestinationInput.isEmpty()){
                Toast.makeText(this,"Please enter your destination", Toast.LENGTH_SHORT).show();
                return;
            }

            try{
                new DirectionFinder(this, OriginInput,DestinationInput).execute();

            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }



    }

    private  void saveDestinations(){
        String start = OriginInputText.getText().toString().trim();
        String desitnation = DesitinationInputText.getText().toString().trim();

        DestinationInformation desti = new DestinationInformation(desitnation,start);

        FirebaseUser user = fireAuth.getCurrentUser();

        databaseReference.child(user.getUid()).setValue(desti);

        if (TextUtils.isEmpty(start)){
            return;
        }
        if (TextUtils.isEmpty(desitnation)){
            return;
        }

        Toast.makeText(this, "Your routes is being saved in our database, Thank you for using travel app", Toast.LENGTH_SHORT).show();


    }







public void signOut(){
    Intent signouT = new Intent(this,LoginActivity.class);
    startActivity(signouT);
}









    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng stockholmC = new LatLng(59.330816, 18.057921);
        mMap.addMarker(new MarkerOptions().position(stockholmC).title("Marker in Stockholm Central"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stockholmC, 15));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }


    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_person))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_goal))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }



}
