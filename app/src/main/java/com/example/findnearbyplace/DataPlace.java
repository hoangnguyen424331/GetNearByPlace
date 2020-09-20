package com.example.findnearbyplace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataPlace {
    private HashMap<String,String> getSimplePlace(JSONObject googlePlaceJson)
    {
        HashMap<String, String> googlePlaceMap=new HashMap<>();
        String NameOfPlace="-NA-";
        String vicinity="-NA-";
        String latitude="";
        String longitude="";
        String reference="";

        try {
            if(!googlePlaceJson.isNull("name"))
            {
                NameOfPlace = googlePlaceJson.getString("name");
            }
            if(!googlePlaceJson.isNull("vicinity"))
            {
                vicinity = googlePlaceJson.getString("vicinity");
            }
            latitude=googlePlaceJson.getJSONObject("geometry").getJSONObject("LOCATION").getString("lat");
            longitude=googlePlaceJson.getJSONObject("geometry").getJSONObject("LOCATION").getString("lng");
            reference= googlePlaceJson.getString("reference");

            googlePlaceJson.put("place_name", NameOfPlace);
            googlePlaceJson.put("vicinity", vicinity);
            googlePlaceJson.put("lat", latitude);
            googlePlaceJson.put("lng", longitude);
            googlePlaceJson.put("reference", reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }



    private List<HashMap<String,String>> getAllNearbyPlace(JSONArray jsonArray)
    {
        int couter=jsonArray.length();

        List<HashMap<String, String>> NearbyPlaceList = new ArrayList<>();

        HashMap<String, String> NearbyPlaceMap = null;
        for(int i=0;i<couter;i++)
        {
            try {
                NearbyPlaceMap = getSimplePlace((JSONObject) jsonArray.get(i));
                NearbyPlaceList.add(NearbyPlaceMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return NearbyPlaceList;
    }

    public List<HashMap<String,String>> parse(String JSONdata)
    {
        JSONArray jsonArray=null;
        JSONObject jsonObject;

        try {

            jsonObject=new JSONObject(JSONdata);
            jsonArray =jsonObject.getJSONArray("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getAllNearbyPlace(jsonArray);
    }
}
