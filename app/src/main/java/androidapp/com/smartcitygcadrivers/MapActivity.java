package androidapp.com.smartcitygcadrivers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.map.CameraPosition;
import com.tomtom.online.sdk.map.Chevron;
import com.tomtom.online.sdk.map.ChevronBuilder;
import com.tomtom.online.sdk.map.Icon;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.Marker;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.Route;
import com.tomtom.online.sdk.map.RouteBuilder;
import com.tomtom.online.sdk.map.SimpleMarkerBalloon;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.map.TomtomMapCallback;
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
import com.tomtom.online.sdk.routing.RoutingApi;
import com.tomtom.online.sdk.routing.data.FullRoute;
import com.tomtom.online.sdk.routing.data.RouteQuery;
import com.tomtom.online.sdk.routing.data.RouteQueryBuilder;
import com.tomtom.online.sdk.routing.data.RouteResponse;
import com.tomtom.online.sdk.routing.data.RouteType;
import com.tomtom.online.sdk.search.OnlineSearchApi;
import com.tomtom.online.sdk.search.SearchApi;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    TomtomMap tomtomMap;
    private SearchApi searchApi;
    private RoutingApi routingApi;
    private LatLng departurePosition;
    private LatLng destinationPosition;
    private Button routeButton;
    private Route route;
    private Dialog dialogInProgress;
    private Icon departureIcon;
    private Icon destinationIcon;
    private LatLng wayPoints;
    private Chevron chevron;
    private ChevronBuilder chevronBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initTomTomServices();
        initLocations();
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create route method
                drawRoute(departurePosition, destinationPosition);
//                startTracking();
                addMarker();
            }
        });

    }

    @Override
    public void onMapReady(@NonNull TomtomMap tomtomMap) {
        this.tomtomMap = tomtomMap;
        this.tomtomMap.setMyLocationEnabled(true);
        Location userLocation = tomtomMap.getUserLocation();
        LatLng amsterdam = new LatLng(52.37, 4.90);
        SimpleMarkerBalloon balloon = new SimpleMarkerBalloon(" ");
        this.tomtomMap.addMarker(new MarkerBuilder(amsterdam).markerBalloon(balloon));
        this.tomtomMap.addOnMarkerClickListener(new TomtomMapCallback.OnMarkerClickListener() {
            @Override
            public void onMarkerClick(@NonNull Marker marker) {
                //Toast.makeText(MapActivity.this, "i clicked the marker", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MapActivity.this);
                builder1.setMessage("Pickup Done ?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
        this.tomtomMap.centerOn(CameraPosition.builder(amsterdam).zoom(15).build());
    }

    private void initTomTomServices() {
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getAsyncMap(this);
        searchApi = OnlineSearchApi.create(this);
        routingApi = OnlineRoutingApi.create(this);
        routeButton = findViewById(R.id.routeButton);
        departureIcon = Icon.Factory.fromResources(MapActivity.this, R.drawable.ic_map_route_departure);
        destinationIcon = Icon.Factory.fromResources(MapActivity.this, R.drawable.ic_map_route_destination);
//          activeIcon1 = Icon.Factory.fromResources(MapActivity.this, R.drawable.ic_chevron_dot);
//        activeIcon2 = Icon.Factory.fromResources(MapActivity.this, R.drawable.ic_chevron_dot);
//        chevronBuilder = ChevronBuilder.create(activeIcon1,activeIcon2);
    }

    private void initLocations() {
        departurePosition = new LatLng(18.60, 	74);// kharadi
        destinationPosition = new LatLng(18.5679, 73.9143);// viman nagar
        wayPoints = new LatLng(18.5513,73.9422); // gulmohar orchids
    }

    private RouteQuery createRouteQuery(LatLng start, LatLng stop,  LatLng[] wayPoints) {
        return (wayPoints != null)
                ? new RouteQueryBuilder(start, stop)
                        .withWayPoints(wayPoints)
                        .withRouteType(RouteType.FASTEST)
                        .build()
                : new RouteQueryBuilder(start, stop)
                        .withRouteType(RouteType.FASTEST)
                        .build();
    }

    private void drawRoute(LatLng start, LatLng stop) {
        LatLng[] latLngs = {wayPoints, new LatLng(18.5570, 73.9302), new LatLng(18.5622, 73.9166)};
        drawRouteWithWayPoints(start, stop, latLngs);
    }

    private void drawRouteWithWayPoints(LatLng start, LatLng stop, LatLng[] wayPoints) {
        RouteQuery routeQuery = createRouteQuery(start, stop, wayPoints);
        //showDialogInProgress();
        routingApi.planRoute(routeQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<RouteResponse>() {
                    @Override
                    public void onSuccess(RouteResponse routeResult) {
                        //dismissDialogInProgress();
                        displayRoutes(routeResult.getRoutes());
                        tomtomMap.displayRoutesOverview();
                    }

                    private void displayRoutes(List<FullRoute> routes) {
                        for (FullRoute fullRoute : routes) {
                            route = tomtomMap.addRoute(new RouteBuilder(
                                    fullRoute.getCoordinates()).startIcon(departureIcon)
                                    .endIcon(destinationIcon).isActive(true));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleApiError(e);
                        clearMap();
                    }
                });
    }

    private void addMarker() {
        LatLng[] markerLatLong = {wayPoints, new LatLng(18.5570, 73.9302), new LatLng(18.5622, 73.9166)};
        for (int i = 0; i < markerLatLong.length; i++) {
            SimpleMarkerBalloon balloon = new SimpleMarkerBalloon("Home 1");
            this.tomtomMap.addMarker(new MarkerBuilder(markerLatLong[i]).markerBalloon(balloon));
        }
    }

    private void showDialogInProgress() {
        if(!dialogInProgress.isShowing()) {
            dialogInProgress.show();
        }
    }

    private void dismissDialogInProgress() {
        if(dialogInProgress.isShowing()) {
            dialogInProgress.dismiss();
        }
    }

    private void handleApiError(Throwable e) {
        dismissDialogInProgress();
        Toast.makeText(MapActivity.this, getString(R.string.api_response_error,
                e.getLocalizedMessage()), Toast.LENGTH_LONG).show();
    }

    private void clearMap() {
        tomtomMap.clear();
        departurePosition = null;
        destinationPosition = null;
        route = null;
    }


    public void startTracking() {
        tomtomMap.centerOn(
                CameraPosition.builder(tomtomMap.getCenterOfMap())
//                        .animationDuration(NO_ANIMATION_TIME)
//                        .pitch(DEFAULT_MAP_PITCH_LEVEL_FOR_DRIVING)
//                        .zoom(DEFAULT_MAP_ZOOM_LEVEL_FOR_DRIVING)
                        .build()
        );
        //tag::doc_start_chevron_tracking[]
        tomtomMap.getDrivingSettings().startTracking(chevron);
        //end::doc_start_chevron_tracking[]
    }


}
