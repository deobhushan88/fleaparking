package edu.sjsu.cmpe295.parket.http;

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
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        backendService = restAdapter.create(BackendService.class);
    }
}
