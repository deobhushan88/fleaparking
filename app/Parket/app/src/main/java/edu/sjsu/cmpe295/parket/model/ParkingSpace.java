package edu.sjsu.cmpe295.parket.model;

/**
 * Created by bdeo on 4/9/15.
 */
public class ParkingSpace {
    String parkingSpaceId;
    String parkingSpaceAddress;
    Double parkingSpaceLat;
    Double parkingSpaceLong;
    boolean disabledParkingFlag;
    Double parkingSpaceRate;
    String startDateTime;
    String endDateTime;
    String parkingSpacePhoto;
    String parkingSpaceDescription;
    String qrCode;

    public ParkingSpace(String parkingSpaceId, String parkingSpaceAddress,
                        Double parkingSpaceLat, Double parkingSpaceLong,
                        boolean disabledParkingFlag, Double parkingSpaceRate,
                        String startDateTime, String endDateTime, String parkingSpacePhoto,
                        String parkingSpaceDescription, String qrCode) {
        this.parkingSpaceId = parkingSpaceId;
        this.parkingSpaceAddress = parkingSpaceAddress;
        this.parkingSpaceLat = parkingSpaceLat;
        this.parkingSpaceLong = parkingSpaceLong;
        this.disabledParkingFlag = disabledParkingFlag;
        this.parkingSpaceRate = parkingSpaceRate;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.parkingSpacePhoto = parkingSpacePhoto;
        this.parkingSpaceDescription = parkingSpaceDescription;
        this.qrCode = qrCode;
    }

    public String getParkingSpaceId() {
        return parkingSpaceId;
    }

    public void setParkingSpaceId(String parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
    }

    public String getParkingSpaceAddress() {
        return parkingSpaceAddress;
    }

    public void setParkingSpaceAddress(String parkingSpaceAddress) {
        this.parkingSpaceAddress = parkingSpaceAddress;
    }

    public Double getParkingSpaceLat() {
        return parkingSpaceLat;
    }

    public void setParkingSpaceLat(Double parkingSpaceLat) {
        this.parkingSpaceLat = parkingSpaceLat;
    }

    public Double getParkingSpaceLong() {
        return parkingSpaceLong;
    }

    public void setParkingSpaceLong(Double parkingSpaceLong) {
        this.parkingSpaceLong = parkingSpaceLong;
    }

    public boolean isDisabledParkingFlag() {
        return disabledParkingFlag;
    }

    public void setDisabledParkingFlag(boolean disabledParkingFlag) {
        this.disabledParkingFlag = disabledParkingFlag;
    }

    public Double getParkingSpaceRate() {
        return parkingSpaceRate;
    }

    public void setParkingSpaceRate(Double parkingSpaceRate) {
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

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
