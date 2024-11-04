package com.mashnu.geofencedemo

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.clevertap.android.geofence.CTGeofenceAPI
import com.clevertap.android.geofence.CTGeofenceSettings
import com.clevertap.android.geofence.Logger
import com.clevertap.android.geofence.interfaces.CTGeofenceEventsListener
import com.clevertap.android.geofence.interfaces.CTLocationUpdatesListener
import com.clevertap.android.sdk.CleverTapAPI
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var locationTextView: TextView
    private lateinit var button: Button
    private lateinit var clevertap: CleverTapAPI
    private val TAG = this::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        locationTextView = findViewById(R.id.textViewLocation)

        button = findViewById(R.id.button)
        button.setOnClickListener{
            fetchLocation()
        }

       clevertap = CleverTapAPI.getDefaultInstance(applicationContext)!!
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE)
        initGeofence()

        CTGeofenceAPI.getInstance(getApplicationContext())
            .setOnGeofenceApiInitializedListener(object:CTGeofenceAPI.OnGeofenceApiInitializedListener {
                override fun OnGeofenceApiInitialized() {
                    Toast.makeText(this@MainActivity, "Geofence initialized", Toast.LENGTH_SHORT).show()
                }
            })

        CTGeofenceAPI.getInstance(getApplicationContext())
            .setCtGeofenceEventsListener(object:CTGeofenceEventsListener {
                override fun onGeofenceEnteredEvent(jsonObject:JSONObject) {
                    Toast.makeText(this@MainActivity, "Geofence entered => $jsonObject", Toast.LENGTH_SHORT).show()
                }
                override fun onGeofenceExitedEvent(jsonObject:JSONObject) {
                    Toast.makeText(this@MainActivity, "Geofence exited => $jsonObject", Toast.LENGTH_SHORT).show()
                }
            })

        CTGeofenceAPI.getInstance(getApplicationContext())
            .setCtLocationUpdatesListener(object:CTLocationUpdatesListener{
                override fun onLocationUpdates(location:Location) {
                    locationTextView.text = "Lat: ${location.latitude} and Long: ${location.longitude}"
                }
            })

    }

    fun fetchLocation(){
        try
        {
            CTGeofenceAPI.getInstance(getApplicationContext()).triggerLocation()
        }
        catch (e:IllegalStateException) {
            // thrown when this method is called before geofence SDK initialization
            Log.d(TAG, "Geofence: exception => ${e.message}")
        }
    }


    fun initGeofence(){
        var ctGeofenceSettings = CTGeofenceSettings.Builder()
            .enableBackgroundLocationUpdates(true)//boolean to enable background location updates
            .setLogLevel(Logger.VERBOSE)//Log Level
            .setLocationAccuracy(CTGeofenceSettings.ACCURACY_HIGH)//byte value for Location Accuracy
            .setLocationFetchMode(CTGeofenceSettings.FETCH_LAST_LOCATION_PERIODIC)//byte value for Fetch Mode
            .setGeofenceMonitoringCount(50)//int value for number of Geofences CleverTap can monitor
            .setInterval(30*60*1000)//long value for interval in milliseconds
            .setFastestInterval(30*60*1000)//long value for fastest interval in milliseconds
            .setSmallestDisplacement(200f)//float value for smallest Displacement in meters
            .setGeofenceNotificationResponsiveness(5)// int value for geofence notification responsiveness in milliseconds
            .build()

        CTGeofenceAPI.getInstance(getApplicationContext()).init(ctGeofenceSettings, clevertap)
    }
}