package com.ipss.worldbank.network.rest;


import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.ipss.worldbank.entity.Country;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class CountryRequest extends Request<Country[]> {
    private Response.Listener<Country[]> listener;

    public CountryRequest(String url, Response.Listener<Country[]> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = listener;
    }

    @Override
    protected Response<Country[]> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data);
            String header = new String(HttpHeaderParser.parseCharset(response.headers));
            JSONArray array = new JSONArray(json);
            JSONArray countryArray = array.getJSONArray(1);
            Country[] countries = JsonArrayToCountries(countryArray);
            return Response.success(countries,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Country[] response) {
        listener.onResponse(response);
    }

    private Country JsonToCountry(JSONObject jobj) throws JSONException{
        try {
            Country result = new Country();
            result.setId(jobj.getString("id"));
            result.setIso2code(jobj.getString("iso2Code"));
            result.setName(jobj.getString("name"));
            return result;
        } catch (JSONException e) {
            throw e;
        }
    }

    private Country[] JsonArrayToCountries(JSONArray jarray) {
        LinkedList<Country> cList = new LinkedList<>();
        for (int i = 0; i < jarray.length(); i++) {
            try {
                JSONObject curr = jarray.getJSONObject(i);
                Country c = JsonToCountry(curr);
                cList.add(c);
            } catch (JSONException e) {

            }
        }
        return cList.toArray(new Country[cList.size()]);
    }
}
