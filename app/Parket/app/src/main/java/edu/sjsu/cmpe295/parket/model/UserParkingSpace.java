package edu.sjsu.cmpe295.parket.model;

/**
 * Created by amodrege on 4/22/15.
 */
public class UserParkingSpace {
    String parkingSpaceId;
    String parkingSpaceLabel;
    String parkingSpaceDescription;
    String parkingSpaceAddress;
    boolean disabledParkingFlag;
    String parkingSpacePhoto;
    double parkingSpaceLat;
    double parkingSpaceLong;
    boolean parkingSpaceAvailabilityFlag;
    String startDateTime;
    String endDateTime;
    double parkingSpaceRate;

    public UserParkingSpace(String parkingSpaceId, String parkingSpaceLabel,
                                      String parkingSpaceDescription, String parkingSpaceAddress,
                                      boolean disabledParkingFlag, String parkingSpacePhoto,
                                      double parkingSpaceLat, double parkingSpaceLong,
                                      boolean parkingSpaceAvailabilityFlag, String startDateTime,
                                      String endDateTime, double parkingSpaceRate) {
        this.parkingSpaceId = parkingSpaceId;
        this.parkingSpaceLabel = parkingSpaceLabel;
        this.parkingSpaceDescription = parkingSpaceDescription;
        this.parkingSpaceAddress = parkingSpaceAddress;
        this.disabledParkingFlag = disabledParkingFlag;
        this.parkingSpacePhoto = parkingSpacePhoto;
        this.parkingSpaceLat = parkingSpaceLat;
        this.parkingSpaceLong = parkingSpaceLong;
        this.parkingSpaceAvailabilityFlag = parkingSpaceAvailabilityFlag;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.parkingSpaceRate = parkingSpaceRate;
    }

    public String getParkingSpaceId() {
        return parkingSpaceId;
    }

    public void setParkingSpaceId(String parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
    }

    public String getParkingSpaceLabel() {
        return parkingSpaceLabel;
    }

    public void setParkingSpaceLabel(String parkingSpaceLabel) {
        this.parkingSpaceLabel = parkingSpaceLabel;
    }

    public String getParkingSpaceDescription() {
        return parkingSpaceDescription;
    }

    public void setParkingSpaceDescription(String parkingSpaceDescription) {
        this.parkingSpaceDescription = parkingSpaceDescription;
    }

    public String getParkingSpaceAddress() {
        return parkingSpaceAddress;
    }

    public void setParkingSpaceAddress(String parkingSpaceAddress) {
        this.parkingSpaceAddress = parkingSpaceAddress;
    }

    public boolean isDisabledParkingFlag() {
        return disabledParkingFlag;
    }

    public void setDisabledParkingFlag(boolean disabledParkingFlag) {
        this.disabledParkingFlag = disabledParkingFlag;
    }

    public String getParkingSpacePhoto() {
        return parkingSpacePhoto;
    }

    public void setParkingSpacePhoto(String parkingSpacePhoto) {
        this.parkingSpacePhoto = parkingSpacePhoto;
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
