package com.estimote.blank;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProximityObserver proximityObserver;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    Connection connection;
    String zoneName = "storage";
    String notInZoneText = "You are not in beacons range.";
    static String defaultZoneName = "storage";

    private Button buttonToggle;
    int activeStatus=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //**********************************************************************************************************//
        //DATABASE CONNECTION SET UP
        //**********************************************************************************************************//
        //String sqlstatement;
        //Statement statement;
        //ResultSet resultSet;

        //ConSQL conSQL = new ConSQL();
        //connection = conSQL.conClass();

        //**********************************************************************************************************//
        //CREDENTIALS SET UP
        //**********************************************************************************************************//
        EstimoteCloudCredentials estimoteCloudCredentials =
                ((MyApplication) getApplication()).estimoteCloudCredentials;

        //**********************************************************************************************************//
        //PROXIMITY OBSERVER SET UP
        //**********************************************************************************************************//
        this.proximityObserver =
                new ProximityObserverBuilder(getApplicationContext(), estimoteCloudCredentials)
                        .onError(throwable -> {
                            Log.e("app", "proximity observer error: " + throwable);
                            return null;
                        })
                        .withBalancedPowerMode()
                        .build();

        //**********************************************************************************************************//
        //ZONES SET UP
        //**********************************************************************************************************//
        ProximityZone zone1 = new ProximityZoneBuilder()
                .forTag("tomasz-mieszkowicz-s-proxi-lfr")
                .inNearRange()
                .onEnter(context -> {
                    zoneName = context.getAttachments().get("tomasz-mieszkowicz-s-proxi-lfr/title");
                    Log.d("app", "You are in " + zoneName + " room.");

                    TextView textView = findViewById(R.id.location);
                    textView.setText("You are in " + zoneName +  " room.");

                    sendDatabaseStatement(zoneName + " room");

                    return null;
                })
                .onExit(context -> {
                    zoneName = defaultZoneName;
                    Log.d("app", "You left.");
                    TextView textView = findViewById(R.id.location);
                    textView.setText(notInZoneText);
                    sendDatabaseStatement(zoneName);
                    return null;
                })
                .build();
        ProximityZone zone2 = new ProximityZoneBuilder()
                .forTag("tomasz-mieszkowicz-s-proxi-lft")
                .inNearRange()
                .onEnter(context -> {
                    zoneName = context.getAttachments().get("tomasz-mieszkowicz-s-proxi-lfr/title");
                    Log.d("app", "You are in " + zoneName + " room.");

                    TextView textView = findViewById(R.id.location);
                    textView.setText("You are in " + zoneName +  " room.");

                    sendDatabaseStatement(zoneName + " room");

                    return null;
                })
                .onExit(context -> {
                    zoneName = defaultZoneName;
                    Log.d("app", "You left.");
                    TextView textView = findViewById(R.id.location);
                    textView.setText(notInZoneText);
                    sendDatabaseStatement(zoneName);
                    return null;
                })
                .build();
        ProximityZone zone3 = new ProximityZoneBuilder()
                .forTag("tomasz-mieszkowicz-s-proxi-lfy")
                .inNearRange()
                .onEnter(context -> {
                    zoneName = context.getAttachments().get("tomasz-mieszkowicz-s-proxi-lfr/title");
                    Log.d("app", "You are in " + zoneName + " room.");

                    TextView textView = findViewById(R.id.location);
                    textView.setText(notInZoneText);

                    sendDatabaseStatement(zoneName + " room");

                    return null;
                })
                .onExit(context -> {
                    zoneName = defaultZoneName;
                    Log.d("app", "You left.");
                    TextView textView = findViewById(R.id.location);
                    textView.setText(notInZoneText);
                    sendDatabaseStatement(zoneName);
                    return null;
                })
                .build();

        List<ProximityZone> zoneList = new ArrayList<>(Arrays.asList(zone1, zone2, zone3));

        //**********************************************************************************************************//
        //PROXIMITY OBSERVER START IF REQUIREMENTS MEET
        //**********************************************************************************************************//
        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        // onRequirementsFulfilled
                        () -> {
                            Log.d("app", "requirements fulfilled");
                            proximityObserver.startObserving(zoneList);
                            return null;
                        },
                        // onRequirementsMissing
                        requirements -> {
                            Log.e("app", "requirements missing: " + requirements);
                            return null;
                        },
                        // onError
                        throwable -> {
                            Log.e("app", "requirements error: " + throwable);
                            return null;
                        });


    buttonToggle=findViewById(R.id.toggleButton);

    buttonToggle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            activeStatus ^= 1;

            if(zoneName.equals("storage")) sendDatabaseStatement(zoneName);
                else sendDatabaseStatement(zoneName + " room");

        }
    });
    }

    public void sendDatabaseStatement(String zone){
        ConSQL conSQL = new ConSQL();
        connection = conSQL.conClass();

        Date date = new Date();

        if(connection!=null){
            try{
                String sqlstatement = "INSERT INTO [devicesStatus] ([deviceName], [currentLocation], [dateAndTime], [isActive]) VALUES ('"+Build.MODEL+"', '"+zone+"', '"+new Timestamp(date.getTime())+"','"+activeStatus+"');";

                Statement statement = connection.createStatement();
                statement.execute(sqlstatement);
                connection.close();
            }
            catch (Exception e){
                Log.e("Error",e.getMessage());
            }
        }
    }
}
