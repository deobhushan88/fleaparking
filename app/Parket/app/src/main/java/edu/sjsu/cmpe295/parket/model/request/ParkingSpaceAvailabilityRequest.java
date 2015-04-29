package edu.sjsu.cmpe295.parket.model.request;

/**
 * Created by amodrege on 4/28/15.
 */
public class ParkingSpaceAvailabilityRequest {
    private String idToken;
    private String action;
    private boolean parkingSpaceAvailabilityFlag;
    private String startDateTime;
    private String endDateTime;
    private double parkingSpaceRate;

    public ParkingSpaceAvailabilityRequest(String idToken, String action, boolean parkingSpaceAvailabilityFlag, String startDateTime, String endDateTime, double parkingSpaceRate) {
        this.idToken = idToken;
        this.action = action;
        this.parkingSpaceAvailabilityFlag = parkingSpaceAvailabilityFlag;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.parkingSpaceRate = parkingSpaceRate;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isParkingSpaceAvailabilityFlag() {
        return parkingSpaceAvailabilityFlag;
    }

    public void setParkingSpaceAvailabilityFlag(boolean parkingSpaceAvailabilityFlag) {
        this.parkingSpaceAvailabilityFlag = parkingSpaceAvailabilityFlag;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public double getParkingSpaceRate() {
        return parkingSpaceRate;
    }

    public void setParkingSpaceRate(double parkingSpaceRate) {
        this.parkingSpaceRate = parkingSpaceRate;
    }
}
