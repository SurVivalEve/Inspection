package com.example.inspection;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.inspection.models.Appointment;
import com.example.inspection.models.History;
import com.example.inspection.models.Processing;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AppointmentDetailFragment extends Fragment implements OnMapReadyCallback {
    private CoordinatorLayout coordinatorLayout;
    private ScrollView scrollView;
    private Processing processing;
    private History history;
    private Appointment appointment;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Double tpLat = 22.293104, toLng = 114.172586;
    private String[] data = new String[9];
    private Button acceptBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_detail, container, false);
        // get processing job details from adapter
        Bundle bundle = this.getArguments();
        processing = (Processing) bundle.getSerializable("processing");
        history = (History) bundle.getSerializable("history");
        appointment = (Appointment) bundle.getSerializable("appointment");

        if (processing != null) {
            data[0] = processing.getTitle();
            data[1] = processing.getRemark();
            data[2] = processing.getTstatus();
            data[3] = processing.getPhone();
            data[4] = processing.getFullname();
            data[5] = processing.getBuilding().concat(" " + processing.getFlatBlock());
            data[6] = processing.getDistrictEN();
            data[7] = processing.getIsland();
            data[8] = processing.getBuilding();
        } else if (history != null) {
            data[0] = history.getTitle();
            data[1] = history.getRemark();
            data[2] = history.getTstatus();
            data[3] = history.getPhone();
            data[4] = history.getFullname();
            data[5] = history.getBuilding().concat(" " + history.getFlatBlock());
            data[6] = history.getDistrictEN();
            data[7] = history.getIsland();
            data[8] = history.getBuilding();
        } else if (appointment != null) {
            data[0] = appointment.getId();
            data[1] = appointment.getRemark();
            data[2] = appointment.getStatus();
            data[3] = appointment.getCustomer().getPhone();
            data[4] = appointment.getCustomer().getFullname();
            data[5] = appointment.getBuilding().concat(" " + appointment.getFlatBlock());
            data[6] = appointment.getDistrict();
            data[7] = "N/A";
            data[8] = appointment.getBuilding();
        }

        init(view, data);

        acceptBtn = (Button) view.findViewById(R.id.accpet);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTaskFragment addTaskFragment = new AddTaskFragment();
                FragmentTransaction ft = ((MainMenu) getContext()).getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putSerializable("appointment", appointment);
                addTaskFragment.setArguments(bundle);
                ft.replace(R.id.main_fragment, addTaskFragment, "add_task_fragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void init(View view, final String[] data) {

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.job_details_layout);
        ((TextView) view.findViewById(R.id.jtitle)).setText(data[0]);
        ((TextView) view.findViewById(R.id.remark)).setText(data[1]);
        ((TextView) view.findViewById(R.id.tstatus)).setText(data[2]);
        ((TextView) view.findViewById(R.id.phone)).setText(data[3]);
        ((TextView) view.findViewById(R.id.fullname)).setText(data[4]);
        ((TextView) view.findViewById(R.id.address)).setText(data[5]);
        ((TextView) view.findViewById(R.id.district)).setText(data[6]);
        ((TextView) view.findViewById(R.id.island)).setText(data[7]);
        scrollView = (ScrollView) view.findViewById(R.id.detail_scroll);
        ImageView imageView = (ImageView) view.findViewById(R.id.transparent_image);
        ImageView imagecall = (ImageView) view.findViewById(R.id.jobdetails_image_call);

        // for codeing verison map

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false);

        mapFragment = SupportMapFragment.newInstance(options);
        mapFragment.getMapAsync(this);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.map_container, mapFragment);
        ft.commit();


        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });


        imagecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+data[3]));
                startActivity(intentDial);
            }
        });

    }

    public void fillRow(View view, String title, String description) {
        TextView titleView = (TextView) view.findViewById(R.id.card_title);
        titleView.setText(title);

        TextView descriptionView = (TextView) view.findViewById(R.id.card_description);
        descriptionView.setText(description);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
//        googleMap.setOnMyLocationChangeListener(this);


        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                return false;
            }
        });


        Geocoder geocoder = new Geocoder(getContext(), Locale.TRADITIONAL_CHINESE);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(data[8], 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            double latitude = addresses.get(0).getLatitude();
            double longitude = addresses.get(0).getLongitude();

            LatLng location = new LatLng(latitude, longitude);

            CameraUpdate center =
                    CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
            googleMap.addMarker(new MarkerOptions().position(location).title("Customer Address"));
            googleMap.animateCamera(center);


        }


        // Add a marker in Sydney, Australia, and move the camera.
//        LatLng sydney = new LatLng(22, 114);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


//    @Override
//    public void onMyLocationChange(Location location) {
//        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
//        map.addMarker(new MarkerOptions().position(loc).title("You"));
//        if(map != null){
//            map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));
//        }
//    }


}
