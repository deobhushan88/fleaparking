package edu.sjsu.cmpe295.parket.http;

import edu.sjsu.cmpe295.parket.model.request.BookParkingSpaceRequest;
import edu.sjsu.cmpe295.parket.model.request.CheckInOutRequest;
import edu.sjsu.cmpe295.parket.model.request.IdTokenParameterRequest;
import edu.sjsu.cmpe295.parket.model.response.BookParkingSpaceResponse;
import edu.sjsu.cmpe295.parket.model.response.CheckInOutResponse;
import edu.sjsu.cmpe295.parket.model.response.QueryParkingSpacesResponse;
import edu.sjsu.cmpe295.parket.model.response.SearchResponse;
import edu.sjsu.cmpe295.parket.model.request.SearchRequest;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by bdeo on 4/16/15.
 */
public interface BackendService {
    @POST("/search")
    void search(@Body SearchRequest searchRequest, Callback<SearchResponse> response);

    @POST("/users/self/queryparkingspaces")
    void queryParkingSpaces(@Body IdTokenParameterRequest idTokenParameterRequest,
                            Callback<QueryParkingSpacesResponse> response);
    @POST("/users/self/bookings")
    void bookParkingSpace(@Body BookParkingSpaceRequest bookParkingSpaceRequest,
                          Callback<BookParkingSpaceResponse> response);
    @POST("/users/self/bookings/{bookingId}")
    void checkInOutParkingSpace(@Path("bookingId") String bookingId,
                                @Body CheckInOutRequest checkInOutRequest,
                                Callback<CheckInOutResponse> response);
}
