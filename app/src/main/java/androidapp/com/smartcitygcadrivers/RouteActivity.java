package androidapp.com.smartcitygcadrivers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RouteActivity extends AppCompatActivity {

    private Button routeButtonSlot1;
    private Button routeButtonSlot2;
    private Button routeButtonSlot3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        setRouteButtons();
        routeButtonSlot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calls getRoutes here and calls the MapActivity
                Intent intentRouteOne = new Intent(RouteActivity.this, MapActivity.class);
                startActivity(intentRouteOne);
            }
        });

        routeButtonSlot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calls getRoutes here and calls the MapActivity
                Intent intentRouteTwo = new Intent(RouteActivity.this, MapActivity.class);
                startActivity(intentRouteTwo);
            }
        });

        routeButtonSlot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calls getRoutes here and calls the MapActivity
                // TODO: 13/11/18 check if route for time slot available or not then moving ahead.
                Intent intentRouteThree = new Intent(RouteActivity.this, MapActivity.class);
                startActivity(intentRouteThree);
            }
        });
    }

    private void setRouteButtons() {
        routeButtonSlot1 = findViewById(R.id.route_slot1);
        routeButtonSlot2 = findViewById(R.id.route_slot2);
        routeButtonSlot3 = findViewById(R.id.route_slot3);
    }

    /***
     * calls the api to get the route, if route not present yet we go a pop up saying so
     */
    private void getRoutes() {

    }
}
