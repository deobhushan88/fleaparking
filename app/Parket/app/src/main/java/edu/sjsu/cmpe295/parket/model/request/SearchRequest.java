package edu.sjsu.cmpe295.parket.model.request;

/**
 * Created by bdeo on 4/16/15.
 */
public class SearchRequest {
    private String idToken;
    private String action;
    private Double userLat;
    private Double userLong;
    private String queryStartDateTime;
    private String queryStopDateTime;
    private Double range;

    public SearchRequest(String idToken, String action, Double userLat, Double userLong,
                         String queryStartDateTime, String queryStopDateTime, Double range) {
        this.idToken = idToken;
        this.action = action;
        this.userLat = userLat;
        this.userLong = userLong;
        this.queryStartDateTime = queryStartDateTime;
        this.queryStopDateTime = queryStopDateTime;
        this.range = range;
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

    public Double getUserLat() {
        return userLat;
    }

    public void setUserLat(Double userLat) {
        this.userLat = userLat;
    }

    public Double getUserLong() {
        return userLong;
    }

    public void setUserLong(Double userLong) {
        this.userLong = userLong;
    }

    public String getQueryStartDateTime() {
        return queryStartDateTime;
    }

    public void setQueryStartDateTime(String queryStartDateTime) {
        this.queryStartDateTime = queryStartDateTime;
    }

    public String getQueryStopDateTime() {
        return queryStopDateTime;
    }

    public void setQueryStopDateTime(String queryStopDateTime) {
        this.queryStopDateTime = queryStopDateTime;
    }

    public Double getRange() {
        return range;
    }

    public void setRange(Double range) {
        this.range = range;
    }
}
