package edu.sjsu.cmpe295.parket.model;

/**
 * Created by bdeo on 4/9/15.
 */
public class ParkingSpace {
    String parkingSpaceId;
    String parkingSpaceOwnerId;
    String parkingSpaceAddress;
    Float parkingSpaceLat;
    Float parkingSpaceLong;
    boolean disabledParkingFlag;
    Float parkingSpaceRate;
    String startDateTime;
    String endDateTime;
    String parkingSpacePhoto;
    String parkingSpaceDescription;

    public String getParkingSpaceId() {
        return parkingSpaceId;
    }

    public void setParkingSpaceId(String parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
    }

    public String getParkingSpaceOwnerId() {
        return parkingSpaceOwnerId;
    }

    public void setParkingSpaceOwnerId(String parkingSpaceOwnerId) {
        this.parkingSpaceOwnerId = parkingSpaceOwnerId;
    }

    public String getParkingSpaceAddress() {
        return parkingSpaceAddress;
    }

    public void setParkingSpaceAddress(String parkingSpaceAddress) {
        this.parkingSpaceAddress = parkingSpaceAddress;
    }

    public Float getParkingSpaceLat() {
        return parkingSpaceLat;
    }

    public void setParkingSpaceLat(Float parkingSpaceLat) {
        this.parkingSpaceLat = parkingSpaceLat;
    }

    public Float getParkingSpaceLong() {
        return parkingSpaceLong;
    }

    public void setParkingSpaceLong(Float parkingSpaceLong) {
        this.parkingSpaceLong = parkingSpaceLong;
    }

    public boolean isDisabledParkingFlag() {
        return disabledParkingFlag;
    }

    public void setDisabledParkingFlag(boolean disabledParkingFlag) {
        this.disabledParkingFlag = disabledParkingFlag;
    }

    public Float getParkingSpaceRate() {
        return parkingSpaceRate;
    }

    public void setParkingSpaceRate(Float parkingSpaceRate) {
        this.parkingSpaceRate = parkingSpaceRate;
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

    public String getParkingSpacePhoto() {
        return parkingSpacePhoto;
    }

    public void setParkingSpacePhoto(String parkingSpacePhoto) {
        this.parkingSpacePhoto = parkingSpacePhoto;
    }

    public String getParkingSpaceDescription() {
        return parkingSpaceDescription;
    }

    public void setParkingSpaceDescription(String parkingSpaceDescription) {
        this.parkingSpaceDescription = parkingSpaceDescription;
    }
}
