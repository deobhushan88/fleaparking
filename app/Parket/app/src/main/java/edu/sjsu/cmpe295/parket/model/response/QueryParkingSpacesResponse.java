package edu.sjsu.cmpe295.parket.model.response;

import java.util.List;

import edu.sjsu.cmpe295.parket.model.UserParkingSpace;

/**
 * Created by bdeo on 4/18/15.
 */
public class QueryParkingSpacesResponse {
    int count;
    List<UserParkingSpace> parkingSpaces;

    public List<UserParkingSpace> getParkingSpaces() {
        return parkingSpaces;
    }

    public void setParkingSpaces(List<UserParkingSpace> parkingSpaces) {
        this.parkingSpaces = parkingSpaces;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
