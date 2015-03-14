package com.serge.pingpong.repository;

import com.serge.pingpong.model.Pong;

/**
 * Created by Serge Ivanov
 * Simple repository for updating pongs in db
 */
public interface PongRepository {

    void refresh( Pong pong );
}
