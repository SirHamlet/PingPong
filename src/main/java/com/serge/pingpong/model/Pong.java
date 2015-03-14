package com.serge.pingpong.model;

import org.springframework.data.annotation.Id;

/**
 * Created by Serge Ivanov
 * Pong entity
 */
public class Pong {

    @Id
    private transient String id;

    private String userId;

    private int pongCount;

    public Pong( String userId ) {
        this.userId = userId;
    }

    public int getPongCount() {
        return pongCount;
    }

    public void setPongCount( int pongCount ) {
        this.pongCount = pongCount;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId( String userId ) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public Pong inc() {
        pongCount++;
        return this;
    }
}
