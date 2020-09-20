package com.ipss.worldbank.network.rest;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.ipss.worldbank.entity.Indicator;
import com.ipss.worldbank.entity.IndicatorData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class IndicatorDataRequest extends Request<IndicatorData[]> {
    private Response.Listener<IndicatorData[]> listener;
    public IndicatorDataRequest(String url,
                                Response.Listener<IndicatorData[]> listener,
                                Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = listener;
    }

    @Override
    protected Response<IndicatorData[]> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data);
            String header = new String(HttpHeaderParser.parseCharset(response.headers));
            JSONArray array = new JSONArray(json);
            JSONArray indicatorJsonArray = array.getJSONArray(1);
            IndicatorData[] idArray = jsonArrayToIndicatorDatas(indicatorJsonArray);
            return Response.success(idArray,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }


    }

    @Override
    protected void deliverResponse(IndicatorData[] response) {
        listener.onResponse(response);
    }

    private IndicatorData jsonToIndicatorData(JSONObject jobj) throws JSONException {
        try {
            IndicatorData result = new IndicatorData();
            result.setCountryIso3Code(jobj.getString("countryiso3code"));
            result.setDate(jobj.getInt("date"));
            result.setValue(jobj.getDouble("value"));
            result.setUnit(jobj.getString("unit"));
            result.setDecimal(jobj.getInt("decimal"));
            result.setObsStatus(jobj.getString("obs_status"));
            return result;
        } catch (JSONException e) {
            throw e;
        }
    }

    private IndicatorData[] jsonArrayToIndicatorDatas(JSONArray jArray) {
        LinkedList<IndicatorData> idList = new LinkedList<>();
        for (int i = 0; i < jArray.length(); i++) {
            try {
                JSONObject curr = jArray.getJSONObject(i);
                IndicatorData iData = jsonToIndicatorData(curr);
                idList.add(iData);
            } catch (JSONException e) {

            }
        }
        return idList.toArray(new IndicatorData[idList.size()]);
    }
}
