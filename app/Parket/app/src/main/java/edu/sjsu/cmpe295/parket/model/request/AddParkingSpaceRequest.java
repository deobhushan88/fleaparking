package edu.sjsu.cmpe295.parket.model.request;

/**
 * Created by bdeo on 4/29/15.
 */
public class AddParkingSpaceRequest {

    String idToken;
    String parkingSpaceLabel;
    String parkingSpaceDescription;
    String addrLine1;
    String addrLine2;
    String city;
    String state;
    String country;
    int zip;
    boolean disabledParkingFlag;
    String parkingSpacePhoto;
    double parkingSpaceLat;
    double parkingSpaceLong;

    public AddParkingSpaceRequest(String idToken, String parkingSpaceLabel,
                                  String parkingSpaceDescription, String addrLine1,
                                  String addrLine2, String city, String state, String country,
                                  int zip, boolean disabledParkingFlag, String parkingSpacePhoto,
                                  double parkingSpaceLat, double parkingSpaceLong) {
        this.idToken = idToken;
        this.parkingSpaceLabel = parkingSpaceLabel;
        this.parkingSpaceDescription = parkingSpaceDescription;
        this.addrLine1 = addrLine1;
        this.addrLine2 = addrLine2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
        this.disabledParkingFlag = disabledParkingFlag;
        this.parkingSpacePhoto = parkingSpacePhoto;
        this.parkingSpaceLat = parkingSpaceLat;
        this.parkingSpaceLong = parkingSpaceLong;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
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

    public String getAddrLine1() {
        return addrLine1;
    }

    public void setAddrLine1(String addrLine1) {
        this.addrLine1 = addrLine1;
    }

    public String getAddrLine2() {
        return addrLine2;
    }

    public void setAddrLine2(String addrLine2) {
        this.addrLine2 = addrLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
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
}
