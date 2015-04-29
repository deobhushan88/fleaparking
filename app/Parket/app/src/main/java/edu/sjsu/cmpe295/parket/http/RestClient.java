package edu.sjsu.cmpe295.parket.http;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by bdeo on 4/16/15.
 */
public class RestClient {
    private static RestClient ourInstance = new RestClient();
    private static final String BASE_URL = "https://parketb.com";
    private static BackendService backendService;


    public synchronized static BackendService getInstance() {
        return backendService;
    }

    private RestClient() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(requestInterceptor)
                .build();
        backendService = restAdapter.create(BackendService.class);
    }
}
