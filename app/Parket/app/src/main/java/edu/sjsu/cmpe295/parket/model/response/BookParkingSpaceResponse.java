package edu.sjsu.cmpe295.parket.model.response;

/**
 * Created by bdeo on 4/26/15.
 */
public class BookParkingSpaceResponse {
    private String bookingId;
    // A Parking Space always has a qrCode, parkingSpaceLat, parkingSpaceLong
    // associated with it, which we will set in the application
    private String qrCode = null;
    private double parkingSpaceLat;
    private double parkingSpaceLong;


    public BookParkingSpaceResponse(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public double getParkingSpaceLat() {
        return parkingSpaceLat;
    }

    public void setParkingSpaceLat(double parkingSpaceLat) {
        this.parkingSpaceLat = parkingSpaceLat;
    }

    public double getParkingSpaceLong() {
        return parkingSpaceLong;
    }

    public void setParkingSpaceLong(double parkingSpaceLong) {
        this.parkingSpaceLong = parkingSpaceLong;
    }
}
