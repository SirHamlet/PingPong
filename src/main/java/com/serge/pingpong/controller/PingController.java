package com.serge.pingpong.controller;

import com.serge.pingpong.model.Ping;
import com.serge.pingpong.model.Pong;
import com.serge.pingpong.service.PongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Serge Ivanov
 *         Controller for handling Pings and Pongs
 */
@Component
public class PingController extends BaseController<Pong, Ping> {

    @Autowired
    private PongService pongService;

    @Override
    public synchronized Pong handleRequest( Ping ping ) {
        return pongService.updatePong( ping );
    }
}
