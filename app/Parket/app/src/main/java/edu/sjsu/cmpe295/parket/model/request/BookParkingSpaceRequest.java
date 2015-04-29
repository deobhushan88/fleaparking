package edu.sjsu.cmpe295.parket.model.request;

/**
 * Created by bdeo on 4/26/15.
 */
public class BookParkingSpaceRequest {
    private String idToken;
    private String parkingSpaceId;
    private String bookingStartDateTime;
    private String bookingEndDateTime;

    public BookParkingSpaceRequest(String idToken, String parkingSpaceId, String bookingStartDateTime, String bookingEndDateTime) {
        this.idToken = idToken;
        this.parkingSpaceId = parkingSpaceId;
        this.bookingStartDateTime = bookingStartDateTime;
        this.bookingEndDateTime = bookingEndDateTime;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getParkingSpaceId() {
        return parkingSpaceId;
    }

    public void setParkingSpaceId(String parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
    }

    public String getBookingStartDateTime() {
        return bookingStartDateTime;
    }

    public void setBookingStartDateTime(String bookingStartDateTime) {
        this.bookingStartDateTime = bookingStartDateTime;
    }

    public String getBookingEndDateTime() {
        return bookingEndDateTime;
    }

    public void setBookingEndDateTime(String bookingEndDateTime) {
        this.bookingEndDateTime = bookingEndDateTime;
    }
}
