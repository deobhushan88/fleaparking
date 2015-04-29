package edu.sjsu.cmpe295.parket.model.response;

/**
 * Created by bdeo on 4/26/15.
 */
public class CheckInOutResponse {
    String status;

    public CheckInOutResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

