// Created by plusminus on 00:23:14 - 03.10.2008
package com.example.habbit.fragments;

import java.io.IOException;
import java.lang.Math;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.fragment.app.Fragment;

import com.example.habbit.R;
import com.example.habbit.activities.MapActivity;
import com.example.habbit.handlers.HabitEventInteractionHandler;
import com.example.habbit.models.Habit;
import com.example.habbit.models.HabitEvent;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.events.MapListener;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Habbit map view Fragment.
 * @author cmput301 team 41
 * orginal code from:
 * @author Marc Kurtz
 * @author Manuel Stahl
 */
public class MapFragment extends Fragment {
    // ===========================================================
    // Constants
    // ===========================================================

    private static final String PREFS_NAME = "org.andnav.osm.prefs";
    private static final String PREFS_TILE_SOURCE = "tilesource";
    private static final String PREFS_LATITUDE_STRING = "latitudeString";
    private static final String PREFS_LONGITUDE_STRING = "longitudeString";
    private static final String PREFS_ORIENTATION = "orientation";
    private static final String PREFS_ZOOM_LEVEL_DOUBLE = "zoomLevelDouble";

    private static final int MENU_ABOUT = Menu.FIRST + 1;
    private static final int MENU_LAST_ID = MENU_ABOUT + 1; // Always set to last unused id

    // ===========================================================
    // Fields
    // ===========================================================
    private SharedPreferences mPrefs;
    private MapView mMapView;
    private View mTransView;
    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay = null;
    private MinimapOverlay mMinimapOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private CopyrightOverlay mCopyrightOverlay;
    private MapListener mMapListener;

    private float[] lastTouchDownXY = new float[2];

    private Handler mHandler = new Handler(Looper.getMainLooper());

    MarkerInfoWindow markerInfoWindow;

    float disX;
    float disY;
    long prevTime = 0;
    long eTime;

    GeoPoint defCurrPoint;
    GeoPoint selectedPoint;


    Marker startMarker;

    HabitEvent habitEvent;
    Habit habit;
    int justView;

    double Latitude;
    double Longitude;

    boolean touched = false;
    boolean lockTillLocFound = true;

    //Bundle bundle = new Bundle();

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        habitEvent = (HabitEvent) getArguments().getSerializable("habitEvent");
        habit = (Habit) getArguments().getSerializable("habit");
        justView = (int) getArguments().getSerializable("justView");
        Latitude = habitEvent.getLatitude();
        Longitude = habitEvent.getLongitude();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Note! we are programmatically construction the map view
        //be sure to handle application lifecycle correct (see note in on pause)
        mMapView = new MapView(inflater.getContext());
        final ITileSource tileSource = TileSourceFactory.MAPNIK;
        mMapView.setTileSource(tileSource);
        mMapView.setClickable(false);
        mMapView.setDestroyMode(false);
        mMapView.setTag("mapView"); // needed for OpenStreetMapViewTest
        lastTouchDownXY[0] = 0;
        lastTouchDownXY[1] = 0;

        startMarker = new Marker(mMapView);

        //View v = getView().findViewById(R.id.info_window);
        //markerInfoWindow = new MarkerInfoWindow(R.id.info_window,mMapView);


        Drawable myDrawable;
        Resources res = getResources();
        try {
            myDrawable = Drawable.createFromXml(res, res.getXml(R.xml.ic__192595));
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            startMarker.setIcon(myDrawable);
            startMarker.setImage(myDrawable);
            startMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    if(touched){
                        double lat = selectedPoint.getLatitude();
                        double lon = selectedPoint.getLongitude();
                        Log.d("lookee",String.valueOf(lat));
                        Log.d("lookee",String.valueOf(lon));
                        habitEvent.setLatitude(lat);
                        habitEvent.setLongitude(lon);

                        Bundle bundle = new Bundle();
                        bundle.putDouble("latKey", lat);
                        bundle.putDouble("lonKey", lon);

                        requireActivity().getSupportFragmentManager().setFragmentResult("requestKey", bundle);

                        //HabitEventInteractionHandler h = new HabitEventInteractionHandler(habit);
                        //h.addHabitEventLocation(habitEvent);
                        //getActivity().finish();

                    }
                    touched = true;
                   // marker.setInfoWindow(markerInfoWindow);
                    marker.setTitle("Click Me Again to Confirm Location!");
                    marker.showInfoWindow();

                    return true;
                }
            });

        } catch (Exception ex) {
            Log.e("Error", "Exception loading drawable");
        }


        mMapView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            /**
             * mouse wheel zooming ftw
             * http://stackoverflow.com/questions/11024809/how-can-my-view-respond-to-a-mousewheel
             * @param v
             * @param event
             * @return
             */
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_SCROLL:
                        Log.d("look","actionscrool");
                        if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f)
                            mMapView.getController().zoomOut();
                        else {
                            //this part just centers the map on the current mouse location before the zoom action occurs
                            IGeoPoint iGeoPoint = mMapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                            mMapView.getController().animateTo(iGeoPoint);
                            mMapView.getController().zoomIn();
                        }
                }

                return true;
            }
        });


        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Do something here for touch point down event
                        Log.d("look", "touched!");
                        if (Arrays.equals(lastTouchDownXY, new float[]{0.0F, 0.0F}) && prevTime == 0) {
                            lastTouchDownXY[0] = event.getX();
                            lastTouchDownXY[1] = event.getY();
                            prevTime = event.getEventTime();
                        } else {
                            eTime = (event.getEventTime() - prevTime);
                            disX = Math.abs(event.getX() - lastTouchDownXY[0]);
                            disY = Math.abs(event.getY() - lastTouchDownXY[1]);
                            lastTouchDownXY[0] = event.getX();
                            lastTouchDownXY[1] = event.getY();
                            prevTime = event.getEventTime();
                            if (eTime > 80 && eTime < 400 && disX < 200 && disY < 200) {
                                Log.d("look", "click seen");
                                touched = false;
                                Log.d("look", "pointX: "+lastTouchDownXY[0]+" "+"pointY: "+lastTouchDownXY[1]);
                                InfoWindow.closeAllInfoWindowsOn(mMapView);

                                GeoPoint mark = (GeoPoint) mMapView.getProjection().fromPixels(
                                        (int) lastTouchDownXY[0],
                                        (int) lastTouchDownXY[1]);
                                selectedPoint = mark.clone();
                                startMarker.setPosition(mark);
                                mMapView.invalidate();
                                mMapView.getOverlays().add(startMarker);
                                return true;
                            }
                            Log.d("look", String.valueOf(eTime));
                            Log.d("look", String.valueOf(disX));
                            Log.d("look", String.valueOf(disY));


                        }
                }
                return false;
            }
        });

        if(justView == 1 || (Latitude == 0 && Longitude == 0)) {
            mMapView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    return true;
                }
            });
        }

        return mMapView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Context context = this.getActivity();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();

        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        //My Location
        //note you have handle the permissions yourself, the overlay did not do it for you
        GpsMyLocationProvider provider = new GpsMyLocationProvider(requireContext());
        provider.addLocationSource(LocationManager.GPS_PROVIDER);
        mLocationOverlay = new MyLocationNewOverlay(provider, mMapView);
        mLocationOverlay.enableMyLocation();

        if( (habitEvent.getLatitude() == 0 && habitEvent.getLongitude() == 0) || habitEvent == null) {
            final Toast t = Toast.makeText(getContext(),"Searching ...",Toast.LENGTH_LONG);
            t.show();
            mLocationOverlay.runOnFirstFix(new Runnable() {
                public void run() {
                    Log.d("SeeWhoFirst", String.format("First location fix: %s", mLocationOverlay.getLastFix()));
                    Log.d("SeeWhoFirst", "Orientation"+mMapView.getMapOrientation());
                    GeoPoint mark = new GeoPoint(mLocationOverlay.getLastFix());
                    selectedPoint = mark.clone();

                    startMarker.setPosition(mark);
                    startMarker.setTitle("Click to confirm event location");

                    mMapView.invalidate();
                    mMapView.getOverlays().add(startMarker);
                    mHandler.post(new Runnable() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void run() {
                            IMapController controller = mMapView.getController();
                            controller.setCenter(selectedPoint);
                            t.cancel();
                            Toast.makeText(getContext(),"Location Found!",Toast.LENGTH_SHORT).show();
                            //startMarker.setInfoWindow(markerInfoWindow);
                            startMarker.setTitle("Click me to confirm location, or double-tap to select another!");
                            startMarker.showInfoWindow();
                            lockTillLocFound = false;
                            mMapView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent event) {

                                    switch (event.getAction()) {
                                        case MotionEvent.ACTION_DOWN:
                                            // Do something here for touch point down event
                                            Log.d("look", "touched!");
                                            if (Arrays.equals(lastTouchDownXY, new float[]{0.0F, 0.0F}) && prevTime == 0) {
                                                lastTouchDownXY[0] = event.getX();
                                                lastTouchDownXY[1] = event.getY();
                                                prevTime = event.getEventTime();
                                            } else {
                                                eTime = (event.getEventTime() - prevTime);
                                                disX = Math.abs(event.getX() - lastTouchDownXY[0]);
                                                disY = Math.abs(event.getY() - lastTouchDownXY[1]);
                                                lastTouchDownXY[0] = event.getX();
                                                lastTouchDownXY[1] = event.getY();
                                                prevTime = event.getEventTime();
                                                if (eTime > 80 && eTime < 400 && disX < 200 && disY < 200) {
                                                    Log.d("look", "click seen");
                                                    touched = false;
                                                    InfoWindow.closeAllInfoWindowsOn(mMapView);
                                                    GeoPoint mark = (GeoPoint) mMapView.getProjection().fromPixels(
                                                            (int) lastTouchDownXY[0],
                                                            (int) lastTouchDownXY[1]);
                                                    selectedPoint = mark.clone();
                                                    startMarker.setPosition(mark);
                                                    mMapView.invalidate();
                                                    mMapView.getOverlays().add(startMarker);
                                                    return true;
                                                }
                                                Log.d("look", String.valueOf(eTime));
                                                Log.d("look", String.valueOf(disX));
                                                Log.d("look", String.valueOf(disY));


                                            }
                                    }
                                    return false;
                                }
                            });
                        }
                    });
                }
            });
        }


        //Mini map
        /*mMinimapOverlay = new MinimapOverlay(context, mMapView.getTileRequestCompleteHandler());
        mMinimapOverlay.setWidth(dm.widthPixels / 5);
        mMinimapOverlay.setHeight(dm.heightPixels / 5);
        mMapView.getOverlays().add(this.mMinimapOverlay);*/


        //Copyright overlay
        mCopyrightOverlay = new CopyrightOverlay(context);
        //i dislike this a little, but it seems as if certain versions of android and/or
        //device types handle screen offsets differently
        mMapView.getOverlays().add(this.mCopyrightOverlay);


        //On screen compass
        mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context),
                mMapView);
        mCompassOverlay.enableCompass();
        mMapView.getOverlays().add(this.mCompassOverlay);


        //map scale
        mScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mMapView.getOverlays().add(this.mScaleBarOverlay);


        //support for map rotation
       /* mRotationGestureOverlay = new RotationGestureOverlay(mMapView);
        mRotationGestureOverlay.setEnabled(true);
        mMapView.getOverlays().add(this.mRotationGestureOverlay);*/
        mMapView.setMapOrientation(0.0F);


        //needed for pinch zooms
        mMapView.setMultiTouchControls(true);

        //scales tiles to the current screen's DPI, helps with readability of labels
        mMapView.setTilesScaledToDpi(true);

        //Info window stuff



        //the rest of this is restoring the last map location the user looked at
        final float zoomLevel = mPrefs.getFloat(PREFS_ZOOM_LEVEL_DOUBLE, 5);
        mMapView.getController().setZoom(zoomLevel);
        final String latitudeString = mPrefs.getString(PREFS_LATITUDE_STRING, "1.0");
        final String longitudeString = mPrefs.getString(PREFS_LONGITUDE_STRING, "1.0");
        final double latitude = Double.valueOf(latitudeString);
        final double longitude = Double.valueOf(longitudeString);
        mMapView.setExpectedCenter(new GeoPoint(latitude, longitude));
        if( (habitEvent.getLatitude() != 0 || habitEvent.getLongitude() != 0) ){
            selectedPoint = new GeoPoint(Latitude,Longitude);
            startMarker.setPosition(selectedPoint);
            startMarker.setTitle("Click to confirm event location");
            mMapView.invalidate();
            mMapView.getOverlays().add(startMarker);
            //startMarker.showInfoWindow();
             IMapController controller = mMapView.getController();
             controller.setCenter(selectedPoint);
             controller.animateTo(selectedPoint);
             controller.setZoom(16.0);
        }
    }

    @Override
    public void onPause() {
        //save the current location
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREFS_TILE_SOURCE, mMapView.getTileProvider().getTileSource().name());
        edit.putFloat(PREFS_ORIENTATION, mMapView.getMapOrientation());
        edit.putString(PREFS_LATITUDE_STRING, String.valueOf(mMapView.getMapCenter().getLatitude()));
        edit.putString(PREFS_LONGITUDE_STRING, String.valueOf(mMapView.getMapCenter().getLongitude()));
        edit.putFloat(PREFS_ZOOM_LEVEL_DOUBLE, (float) mMapView.getZoomLevelDouble());
        edit.apply();

        mMapView.onPause();
        super.onPause();
        mLocationOverlay.disableMyLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //this part terminates all of the overlays and background threads for osmdroid
        //only needed when you programmatically create the map
        mMapView.onDetach();

    }

    @Override
    public void onResume() {
        super.onResume();
        mLocationOverlay.enableMyLocation();
        final String tileSourceName = mPrefs.getString(PREFS_TILE_SOURCE,
                TileSourceFactory.DEFAULT_TILE_SOURCE.name());
        try {
            final ITileSource tileSource = TileSourceFactory.MAPNIK;
            mMapView.setTileSource(tileSource);
        } catch (final IllegalArgumentException e) {
            mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        }

        mMapView.onResume();
    }



   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Put overlay items first
        mMapView.getOverlayManager().onCreateOptionsMenu(menu, MENU_LAST_ID, mMapView);

        // Put "About" menu item last
        menu.add(0, MENU_ABOUT, Menu.CATEGORY_SECONDARY, R.string.about).setIcon(
                android.R.drawable.ic_menu_info_details);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu pMenu) {
        mMapView.getOverlayManager().onPrepareOptionsMenu(pMenu, MENU_LAST_ID, mMapView);
        super.onPrepareOptionsMenu(pMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMapView.getOverlayManager().onOptionsItemSelected(item, MENU_LAST_ID, mMapView)) {
            return true;
        }

        switch (item.getItemId()) {
            case MENU_ABOUT:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.app_name).setMessage(R.string.about_message)
                        .setIcon(R.drawable.ic__192595)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //
                                    }
                                }
                        );
                builder.create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void zoomIn() {
        mMapView.getController().zoomIn();
    }

    public void zoomOut() {
        mMapView.getController().zoomOut();
    }

    public void invalidateMapView() {
        mMapView.invalidate();
    }
}