package com.estimote.blank;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String DEFAULT_ZONE_NAME = "storage";
    private static final String NOT_IN_ZONE_TEXT = "You are not in beacons range.";

    private ProximityObserver proximityObserver;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private Connection connection;
    private String zoneName = DEFAULT_ZONE_NAME;
    private Button buttonToggle;
    private int activeStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupProximityObserver();
        setupProximityZones();
        setupRequirementsWizard();
        setupToggleButton();
    }

    private void setupProximityObserver() {
        EstimoteCloudCredentials estimoteCloudCredentials = ((MyApplication) getApplication()).estimoteCloudCredentials;

        proximityObserver = new ProximityObserverBuilder(getApplicationContext(), estimoteCloudCredentials)
                .onError(throwable -> {
                    Log.e("app", "Proximity observer error: " + throwable);
                    return null;
                })
                .withBalancedPowerMode()
                .build();
    }

    private void setupProximityZones() {
        ProximityZone zone1 = createProximityZone("tomasz-mieszkowicz-s-proxi-lfr");
        ProximityZone zone2 = createProximityZone("tomasz-mieszkowicz-s-proxi-lft");
        ProximityZone zone3 = createProximityZone("tomasz-mieszkowicz-s-proxi-lfy");

        List<ProximityZone> zoneList = new ArrayList<>(Arrays.asList(zone1, zone2, zone3));

        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        () -> {
                            Log.d("app", "Requirements fulfilled");
                            proximityObserver.startObserving(zoneList);
                            return null;
                        },
                        requirements -> {
                            Log.e("app", "Requirements missing: " + requirements);
                            return null;
                        },
                        throwable -> {
                            Log.e("app", "Requirements error: " + throwable);
                            return null;
                        });
    }

    private ProximityZone createProximityZone(String tag) {
        return new ProximityZoneBuilder()
                .forTag(tag)
                .inNearRange()
                .onEnter(context -> {
                    zoneName = context.getAttachments().get("tomasz-mieszkowicz-s-proxi-lfr/title");
                    Log.d("app", "You are in " + zoneName + " room.");
                    updateLocationText("You are in " + zoneName + " room.");
                    sendDatabaseStatement(zoneName + " room");
                    return null;
                })
                .onExit(context -> {
                    zoneName = DEFAULT_ZONE_NAME;
                    Log.d("app", "You left.");
                    updateLocationText(NOT_IN_ZONE_TEXT);
                    sendDatabaseStatement(zoneName);
                    return null;
                })
                .build();
    }

    private void updateLocationText(String text) {
        TextView textView = findViewById(R.id.location);
        textView.setText(text);
    }

    private void setupToggleButton() {
        buttonToggle = findViewById(R.id.toggleButton);
        buttonToggle.setOnClickListener(view -> {
            activeStatus ^= 1;
            String location = zoneName.equals(DEFAULT_ZONE_NAME) ? zoneName : zoneName + " room";
            sendDatabaseStatement(location);
        });
    }

    public void sendDatabaseStatement(String zone) {
        ConSQL conSQL = new ConSQL();
        connection = conSQL.conClass();

        if (connection != null) {
            try {
                String sqlStatement = "INSERT INTO [devicesStatus] ([deviceName], [currentLocation], [dateAndTime], [isActive]) " +
                        "VALUES ('" + Build.MODEL + "', '" + zone + "', '" + new Timestamp(new Date().getTime()) + "', '" + activeStatus + "');";
                Statement statement = connection.createStatement();
                statement.execute(sqlStatement);
                connection.close();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
    }
}
