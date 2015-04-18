package edu.sjsu.cmpe295.parket.model.response;

import java.util.List;

import edu.sjsu.cmpe295.parket.model.ParkingSpace;

/**
 * Created by bdeo on 4/16/15.
 */
public class SearchResponse {
    int count;
    List<ParkingSpace> parkingSpaces;

    public List<ParkingSpace> getParkingSpaces() {
        return parkingSpaces;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setParkingSpaces(List<ParkingSpace> parkingSpaces) {
        this.parkingSpaces = parkingSpaces;
    }
}
