package edu.sjsu.cmpe295.parket.model.request;

/**
 * Created by bdeo on 4/26/15.
 */
public class CheckInOutRequest {
    String idToken;
    String action;

    public CheckInOutRequest(String idToken, String action) {
        this.idToken = idToken;
        this.action = action;
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
}
