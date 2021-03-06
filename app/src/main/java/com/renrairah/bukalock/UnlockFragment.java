package com.renrairah.bukalock;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnlockFragment extends Fragment {
    private CardView gamesCardView, motionCardView;
    private TextView mLongitude, mLatitude, mWeather, mTemperature;
    private FusedLocationProviderClient fusedLocationClient;
    public static UnlockFragment newInstance() {
        UnlockFragment fragment = new UnlockFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_unlock, container, false);
        final int[] status = new int[1];
        SharedPreferences mValid = PreferenceManager.getDefaultSharedPreferences(getContext());
        status[0] =  mValid.getInt("valid", 0);
        gamesCardView = (CardView) rootView.findViewById(R.id.card_games);
        motionCardView = (CardView) rootView.findViewById(R.id.card_motion);

        int permission = 0;

        // Get Langitude and Longitude
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, permission);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // HTTP URL CONNECTION

                            String url = "http://api.openweathermap.org/data/2.5/weather?lat="+ location.getLatitude() + "&lon=" + location.getLongitude() +"&appid=e1fdfc163ccafda97f4286d04826bf85";

                            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try
                                    {
                                        JSONObject main_object = response.getJSONObject("main");
                                        JSONArray array = response.getJSONArray("weather");
                                        JSONObject object = array.getJSONObject(0);
                                        String temp = String.valueOf(main_object.getDouble("temp"));
                                        String weather = object.getString("description");
                                        String city = response.getString("name");

                                        double temp_int = Double.parseDouble(temp);
                                        double centi = temp_int - 273;
                                        centi = Math.round(centi);
                                        int temperature = (int)centi;

                                        // Update Weather and Temperature
                                        TextView mWeather = (TextView) getActivity().findViewById(R.id.weather);
                                        mWeather.setText(toTitleCase(weather));
                                        Log.d("Weather :", toTitleCase(weather));
                                        TextView mTemperature = (TextView) getActivity().findViewById(R.id.temperature);
                                        String city_temp = city + ", " + String.valueOf(temperature) +"°";
                                        Log.d("City, temperature: ", String.valueOf(city_temp));
                                        mTemperature.setText(city_temp);


                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }
                            );
                            RequestQueue queue = Volley.newRequestQueue(getActivity());
                            queue.add(jor);
                        }
                    }
                });
        gamesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status[0] == 1) {
                    PackageManager pm = getContext().getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage("com.renrairah.bukalabirin");
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Sorry")
                            .setMessage("You aren't eligible to unlock this door")
                            .setNegativeButton("OK", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
        motionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UnlockGestureActivity.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

}
