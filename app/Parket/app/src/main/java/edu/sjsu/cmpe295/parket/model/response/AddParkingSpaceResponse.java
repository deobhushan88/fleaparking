package edu.sjsu.cmpe295.parket.model.response;

/**
 * Created by bdeo on 4/29/15.
 */
public class AddParkingSpaceResponse {

    String parkingSpaceId;

    public AddParkingSpaceResponse(String parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
    }

    public String getParkingSpaceId() {
        return parkingSpaceId;
    }

    public void setParkingSpaceId(String parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
    }
}
