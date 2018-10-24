package androidapp.com.smartcitygcadrivers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
