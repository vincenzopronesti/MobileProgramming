package com.ipss.worldbank.network.rest;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;

import com.ipss.worldbank.entity.Country;
import com.ipss.worldbank.entity.Indicator;
import com.ipss.worldbank.entity.IndicatorData;
import com.ipss.worldbank.entity.PageMetaData;
import com.ipss.worldbank.entity.Topic;
import com.ipss.worldbank.network.NetworkController;


public class WorldBanksRest {
    /** The WorldBanKRest Class handle the REST call to the worldbank web site.
     *  It support:
     *  <ul>
     *      <li>Fetch all country data</li>
     *      <li>Fetch all topics</li>
     *      <li>Fetch all Indicator given a topic</Li>
     *      <li>Fetch all data given a country and a indicator</li>
     *  </ul>
     * */
    private NetworkController networkController;
    private static final String slash = "/";
    private static final String qm = "?";
    private static final String and = "&";
    private static final String wbBaseUrl = "https://api.worldbank.org/v2/";
    private static final String wbCountryUrl = "country";
    private static final String wbCountriesUrl = "countries";
    private static final String wbIndicatorUrl = "indicators";
    private static final String wbTopicsUrl = "topics";
    private static final String wbJsonFormat = "format=json";
    private static final String wbperPage = "per_page=";

    public WorldBanksRest(Context ctx) {
        networkController = NetworkController.getInstance(ctx);
    }

    public void getCountryList(Response.Listener<Country[]> listener,
                               Response.ErrorListener errorListener) {
        /**
         * The <i>getCountryList()</i> fetch all country data from worldbank in a asynchronous way.
         * @param listener: handle what to do when the request is successful.
         * @param ErrorListener: handle what happen when the request is unsuccessful.
         */
        String baseUrl = wbBaseUrl + wbCountryUrl + qm + wbJsonFormat + and + wbperPage;
        // request to fetch metadata and then fetch all country, CountryListener fetch all country.
        CountryListener countryListener = new CountryListener(baseUrl, listener, errorListener);
        PageMetaDataRequest mdRequest = new PageMetaDataRequest(baseUrl + 1,  countryListener, errorListener);
        networkController.addToRequestQueue(mdRequest);
    }

    public void getTopicsList(Response.Listener<Topic[]> listener,
                              Response.ErrorListener errorListener) {
        final String baseUrl = wbBaseUrl + wbTopicsUrl + qm + wbJsonFormat + and + wbperPage;
        TopicsListener topicsListener = new TopicsListener(baseUrl, listener, errorListener);
        PageMetaDataRequest mdRequest = new PageMetaDataRequest(baseUrl + 1, topicsListener, errorListener);
        networkController.addToRequestQueue(mdRequest);
    }

    public void getIndicatorsListFromTopic(
            Topic topic,
            Response.Listener<Indicator[]> listener,
            Response.ErrorListener errorListener) {
        String baseUrl = wbBaseUrl + wbTopicsUrl + slash + topic.getId() + slash + wbIndicatorUrl +
                qm + wbJsonFormat + and + wbperPage;
        IndicatorListener indicatorListener = new IndicatorListener(baseUrl, listener, errorListener);
        PageMetaDataRequest mdRequest = new PageMetaDataRequest(baseUrl + 1, indicatorListener, errorListener);
        networkController.addToRequestQueue(mdRequest);
    }

    public void getDataFromCountryAndIndicator(Country country,
                                        Indicator indicator,
                                        Response.Listener<IndicatorData[]> listener,
                                        Response.ErrorListener errorListener) {
        String baseUrl = wbBaseUrl + wbCountriesUrl + slash + country.getIso2code() + slash
                + wbIndicatorUrl + slash + indicator.getId() + qm + wbJsonFormat + and + wbperPage;
        IndicatorDataListener indicatorDataListener = new IndicatorDataListener(baseUrl, listener, errorListener);
        PageMetaDataRequest mdRequest = new PageMetaDataRequest(baseUrl + 1, indicatorDataListener, errorListener);
        networkController.addToRequestQueue(mdRequest);
    }

    abstract class PageMetaDataListener implements Response.Listener<PageMetaData> {

        private String baseUrl;
        private Response.Listener listener;
        private Response.ErrorListener errorListener;
        public PageMetaDataListener(String baseUrl, Response.Listener listener, Response.ErrorListener errorListener) {
            this.baseUrl = baseUrl;
            this.listener = listener;
            this.errorListener = errorListener;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public Response.Listener getListener() {
            return listener;
        }

        public void setListener(Response.Listener listener) {
            this.listener = listener;
        }

        public Response.ErrorListener getErrorListener() {
            return errorListener;
        }

        public void setErrorListener(Response.ErrorListener errorListener) {
            this.errorListener = errorListener;
        }

        @Override
        public abstract void onResponse(PageMetaData response);
    }

    class CountryListener extends PageMetaDataListener {
        public CountryListener(String baseUrl, Response.Listener<Country[]> listener, Response.ErrorListener errorListener) {
            super(baseUrl, listener, errorListener);
        }

        @Override
        public void onResponse(PageMetaData response) {
            String url = this.getBaseUrl() + response.getTotal();
            CountryRequest request = new CountryRequest(url, this.getListener(), this.getErrorListener());
            networkController.addToRequestQueue(request);
        }
    }

    class TopicsListener extends PageMetaDataListener {
        public TopicsListener(String baseUrl, Response.Listener<Topic[]> listener, Response.ErrorListener errorListener) {
            super(baseUrl, listener, errorListener);
        }

        @Override
        public void onResponse(PageMetaData response) {
            String url = this.getBaseUrl() + response.getTotal();
            TopicsRequest request = new TopicsRequest(url,
                    this.getListener(), this.getErrorListener());
            networkController.addToRequestQueue(request);
        }
    }

    class IndicatorListener extends PageMetaDataListener {
        public IndicatorListener(String baseUrl, Response.Listener<Indicator[]> listener, Response.ErrorListener errorListener) {
            super(baseUrl, listener, errorListener);
        }

        @Override
        public void onResponse(PageMetaData response) {
            String url = this.getBaseUrl() + response.getTotal();
            IndicatorRequest request = new IndicatorRequest(url,
                    this.getListener(), this.getErrorListener());
            networkController.addToRequestQueue(request);
        }
    }

    class IndicatorDataListener extends PageMetaDataListener {
        public IndicatorDataListener(String baseUrl, Response.Listener<IndicatorData[]> listener, Response.ErrorListener errorListener) {
            super(baseUrl, listener, errorListener);
        }

        @Override
        public void onResponse(PageMetaData response) {
            String url = this.getBaseUrl() + response.getTotal();
            IndicatorDataRequest request = new IndicatorDataRequest(url,
                    this.getListener(), this.getErrorListener());
            Log.d("webUrl", url);
            networkController.addToRequestQueue(request);

        }
    }
}
