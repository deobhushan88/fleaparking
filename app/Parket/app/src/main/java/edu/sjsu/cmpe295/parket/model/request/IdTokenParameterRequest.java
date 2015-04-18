package edu.sjsu.cmpe295.parket.model.request;

/**
 * Created by bdeo on 4/18/15.
 */
public class IdTokenParameterRequest {
    String idToken;

    public IdTokenParameterRequest(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
