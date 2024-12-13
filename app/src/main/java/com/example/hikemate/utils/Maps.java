package com.example.hikemate.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.hikemate.R;
import com.example.hikemate.model.Mountain;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.location.Address;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Maps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Spinner mapTypeSpinner;
    private LatLng lokasiAwal, lokasiTujuan;
    private EditText destinationEditText;
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Button btnFindMountain;
    private TextView timeRemainingTextView;
    private List<Mountain> mountainList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_maps);

        btnFindMountain = findViewById(R.id.btnFindMountain);
        btnFindMountain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findNearestMountains();
            }
        });

        geocoder = new Geocoder(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        destinationEditText = findViewById(R.id.destination);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        initializeMountains();

        mapTypeSpinner = findViewById(R.id.spinner);
        setupMapTypeSpinner();

        destinationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    String destination = destinationEditText.getText().toString().trim();

                    if (!destination.isEmpty()) {
                        processDestination(destination);
                    } else {
                        destinationEditText.setError("Lokasi tujuan tidak boleh kosong");
                    }
                    return true;
                }
                return false;
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Tampilkan dialog untuk meminta pengguna mengaktifkan GPS
        }
    }

    private void processDestination(String destination) {
        try {
            List<Address> addresses = geocoder.getFromLocationName(destination, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                lokasiTujuan = new LatLng(address.getLatitude(), address.getLongitude());
                getDeviceLocation();
            } else {
                Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal mencari lokasi", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        lokasiAwal = new LatLng(location.getLatitude(), location.getLongitude());

                        mMap.clear();

                        mMap.addMarker(new MarkerOptions().position(lokasiAwal).title("Lokasi Anda"));
                        mMap.addMarker(new MarkerOptions().position(lokasiTujuan).title("Lokasi Tujuan"));

                        zoomToRoute();
                        drawRoute();
                    } else {
                        Toast.makeText(this, "Tidak dapat menemukan lokasi Anda", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDeviceLocation();
            } else {
                Toast.makeText(this, "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        timeRemainingTextView = findViewById(R.id.time_remaining);
        setupMapTypeSpinner();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            lokasiAwal = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions()
                                    .position(lokasiAwal)
                                    .title("Lokasi Saya"));
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Set custom InfoWindowAdapter
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                // Return null to use the default InfoWindow frame
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View infoView = getLayoutInflater().inflate(R.layout.mountain_info, null);
                TextView title = infoView.findViewById(R.id.title);
                TextView snippet = infoView.findViewById(R.id.snippet);

                title.setText(marker.getTitle());
                snippet.setText(marker.getSnippet());
                return infoView;
            }
        });

    }


    private void setupMapTypeSpinner() {
        String[] mapTypes = {"Normal", "Satelit", "Terrain", "Hibrid"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                mapTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapTypeSpinner.setAdapter(adapter);

        mapTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mMap != null) {
                    switch (position) {
                        case 0: mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); break;
                        case 1: mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); break;
                        case 2: mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN); break;
                        case 3: mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void zoomToRoute() {
        LatLng centerPoint = new LatLng(
                (lokasiAwal.latitude + lokasiTujuan.latitude) / 2,
                (lokasiAwal.longitude + lokasiTujuan.longitude) / 2
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPoint, getZoomLevel()));
    }

    private float getZoomLevel() {
        double distance = calculateDistance(lokasiAwal, lokasiTujuan);
        if (distance < 10) return 14;
        if (distance < 50) return 8;
        if (distance < 200) return 6;
        return 5;
    }

    private double calculateDistance(LatLng start, LatLng end) {
        float[] results = new float[1];
        Location.distanceBetween(
                start.latitude, start.longitude,
                end.latitude, end.longitude,
                results
        );
        return results[0] / 1000.0; // Konversi meter ke kilometer
    }

    private void drawRoute() {
        if (lokasiAwal == null || lokasiTujuan == null) {
            return;
        }

        mMap.addPolyline(new PolylineOptions()
                .add(lokasiAwal, lokasiTujuan)
                .width(5)
                .color(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        );

        double distance = calculateDistance(lokasiAwal, lokasiTujuan);
        double estimasiWaktuJam = distance / 5; // Asumsi 5 km/jam
        int estimasiWaktuMenit = (int) Math.round(estimasiWaktuJam * 60);

        if (timeRemainingTextView != null) {
            String waktuEstimasi = estimasiWaktuMenit + " minutes left";
            timeRemainingTextView.setText(waktuEstimasi);
        }
    }

    private void initializeMountains() {
        // Sample mountains
        mountainList = new ArrayList<>();
        mountainList.add(new Mountain("Gunung Merapi", -7.5405, 110.4460,
                "Salah satu gunung api teraktif di Indonesia", 4, 2930));
        mountainList.add(new Mountain("Gunung Semeru", -8.1080, 112.9220,
                "Gunung tertinggi di Pulau Jawa", 5, 3676));
        mountainList.add(new Mountain("Gunung Rinjani", -8.4131, 116.4552,
                "Gunung terkenal di Pulau Lombok", 4, 3726));
        mountainList.add(new Mountain("Gunung Bromo", -7.9425, 112.9530,
                "Gunung dengan pemandangan sunrise yang spektakuler", 3, 2329));
        mountainList.add(new Mountain("Gunung Kerinci", -1.6976, 101.2644,
                "Gunung tertinggi di Sumatera", 5, 3805));
        mountainList.add(new Mountain("Gunung Gede", -6.7707, 106.9853,
                "Bagian dari Taman Nasional Gede Pangrango", 3, 2958));
        mountainList.add(new Mountain("Gunung Pangrango", -6.8146, 106.9850,
                "Berada di sebelah Gunung Gede", 4, 3019));
        mountainList.add(new Mountain("Gunung Lawu", -7.6257, 111.1928,
                "Terkenal dengan pemandangan indah dan jalur pendakian yang mistis", 4, 3265));
        mountainList.add(new Mountain("Gunung Slamet", -7.2422, 109.2083,
                "Gunung tertinggi di Jawa Tengah", 5, 3428));
        mountainList.add(new Mountain("Gunung Arjuno", -7.7661, 112.5949,
                "Berada dekat dengan Gunung Welirang", 4, 3339));
        mountainList.add(new Mountain("Gunung Welirang", -7.7379, 112.5270,
                "Dikenal dengan sumber belerangnya", 3, 3156));
        mountainList.add(new Mountain("Gunung Sindoro", -7.2972, 109.9919,
                "Berpasangan dengan Gunung Sumbing", 4, 3153));
        mountainList.add(new Mountain("Gunung Sumbing", -7.3756, 110.0527,
                "Gunung kembar dari Sindoro", 4, 3371));
        mountainList.add(new Mountain("Gunung Agung", -8.3405, 115.5080,
                "Gunung tertinggi di Bali", 4, 3031));
        mountainList.add(new Mountain("Gunung Tambora", -8.2475, 117.9925,
                "Gunung yang terkenal karena letusan dahsyatnya pada tahun 1815", 5, 2850));
        mountainList.add(new Mountain("Gunung Bukit Raya", -0.6595, 112.5283,
                "Gunung tertinggi di Kalimantan", 3, 2278));
        mountainList.add(new Mountain("Gunung Binaiya", -3.1508, 129.4633,
                "Gunung tertinggi di Maluku", 4, 3027));
        mountainList.add(new Mountain("Gunung Cartensz Pyramid", -4.0828, 137.1566,
                "Puncak tertinggi di Indonesia", 5, 4884));
    }


    private void findNearestMountains() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mMap.clear();
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            List<Mountain> nearestMountains = findNearestMountainsInRadius(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    200 // 200 km radius
                            );
                            for (Mountain mountain : nearestMountains) {
                                LatLng mountainLocation = new LatLng(mountain.getLatitude(), mountain.getLongitude());
                                mMap.addMarker(new MarkerOptions()
                                        .position(mountainLocation)
                                        .title(mountain.getName())
                                        .snippet(String.format(
                                                "Ketinggian: %.0f m\nKesulitan: %d\n%s",
                                                mountain.getHeight(),
                                                mountain.getDifficulty(),
                                                mountain.getDescription()
                                        ))
                                        .icon(getMarkerIconFromColor("#00664F"))
                                );
                            }

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 8));
                        } else {
                            Toast.makeText(Maps.this, "Lokasi tidak dapat ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Maps.this, "Gagal mendapatkan lokasi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private List<Mountain> findNearestMountainsInRadius(double lat, double lon, double radiusKm) {
        List<Mountain> nearestMountains = new ArrayList<>();

        for (Mountain mountain : mountainList) {
            float[] results = new float[1];
            Location.distanceBetween(
                    lat, lon,
                    mountain.getLatitude(), mountain.getLongitude(),
                    results
            );

            if (results[0] / 1000 <= radiusKm) {
                nearestMountains.add(mountain);
            }
        }

        return nearestMountains;
    }

    private BitmapDescriptor getMarkerIconFromColor(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }
}