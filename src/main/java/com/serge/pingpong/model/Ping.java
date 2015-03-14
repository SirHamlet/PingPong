package com.serge.pingpong.model;

/**
 * Created by Serge Ivanov
 * Ping entity
 */
public class Ping extends BaseRequest {

    String userId;

    public Ping() {
    }

    public Ping( String userId ) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId( String userId ) {
        this.userId = userId;
    }
}
