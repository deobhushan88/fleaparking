package edu.sjsu.cmpe295.parket.http;

import edu.sjsu.cmpe295.parket.model.request.IdTokenParameterRequest;
import edu.sjsu.cmpe295.parket.model.response.QueryParkingSpacesResponse;
import edu.sjsu.cmpe295.parket.model.response.SearchResponse;
import edu.sjsu.cmpe295.parket.model.request.SearchRequest;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by bdeo on 4/16/15.
 */
public interface BackendService {
    @POST("/search")
    void search(@Body SearchRequest searchRequest, Callback<SearchResponse> response);

    @POST("/users/self/queryparkingspaces")
    void queryParkingSpaces(@Body IdTokenParameterRequest idTokenParameterRequest,
                            Callback<QueryParkingSpacesResponse> response);
}
