package com.caseybrooks.androidbibletools.providers.joshuaproject;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.caseybrooks.androidbibletools.ABT;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.io.CachingStringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class JoshuaProject implements Downloadable, Response.Listener<String>, Response.ErrorListener {

    OnResponseListener listener;
    String APIKey;

    //basic data used in the Joshua Project embeddable widget
    String peopleNameInCountry;
    String country;
    int population;
    String language;
    String religion;
    String status;
    String photoCreditUrl;
    Bitmap photo;

    //extra information we may want to use
    float latitude;
    float longitude;
    String peopleGroupUrl;


    @Override
    public void download(OnResponseListener listener) {
        APIKey = ABT.getInstance().getMetadata().getString("JoshuaProject_ApiKey", null);

        if(TextUtils.isEmpty(APIKey)) {
            throw new IllegalStateException("API key not set in ABT metadata. Please add 'JoshuaProject_ApiKey' key to metadata.");
        }

        this.listener = listener;
        String tag = "JoshuaProject";

        String url = "http://api.joshuaproject.net/v1/people_groups/daily_unreached.json?api_key=" + APIKey;

        CachingStringRequest htmlObjReq = new CachingStringRequest(Request.Method.GET, url, this, this);
        htmlObjReq.setExpireMillis(CachingStringRequest.Timeout.OneDay.millis);
        htmlObjReq.setRefreshMillis(CachingStringRequest.Timeout.OneHour.millis);

        ABT.getInstance().addToRequestQueue(htmlObjReq, tag);
    }

    @Override
    public void onResponse(String response) {
        if(TextUtils.isEmpty(response)) {
            onErrorResponse(new VolleyError("Empty response"));
            return;
        }

        try {
            JSONObject object = new JSONArray(response).getJSONObject(0);
            peopleNameInCountry = object.optString("PeopNameInCountry");
            country = object.optString("Ctry");
            population = object.optInt("Population");
            language = object.optString("PrimaryLanguageName");
            religion = object.optString("PrimaryReligion");
            status = object.optString("JPScaleText");
            photoCreditUrl = object.optString("PeopleGroupPhotoURL");
            latitude = (float) object.optDouble("Latitude");
            longitude = (float) object.optDouble("Longitude");
            peopleGroupUrl = object.optString("PeopleGroupURL");

            ABT.getInstance().getImageLoader().get(photoCreditUrl.replace("http://", "https://"), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    photo = response.getBitmap();
                    if(listener != null) {
                        listener.responseFinished(true);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if(listener != null) {
                        listener.responseFinished(false);
                    }
                }
            });
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        if(listener != null) {
            listener.responseFinished(false);
        }
    }

//Getters
//--------------------------------------------------------------------------------------------------
    public String getPeopleNameInCountry() {
        return peopleNameInCountry;
    }

    public String getCountry() {
        return country;
    }

    public int getPopulation() {
        return population;
    }

    public String getLanguage() {
        return language;
    }

    public String getReligion() {
        return religion;
    }

    public String getStatus() {
        return status;
    }

    public String getPhotoCreditUrl() {
        return photoCreditUrl;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getPeopleGroupUrl() {
        return peopleGroupUrl;
    }
}
