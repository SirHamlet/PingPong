package com.serge.pingpong.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.serge.pingpong.model.Ping;
import com.serge.pingpong.model.Pong;
import com.serge.pingpong.repository.PongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Serge Ivanov
 *         <p>
 *         This service is stand for saving pongs into the cache,
 *         syncing pongs between cache and db.
 */
@Service
public class PongService {

    @Autowired
    private PongRepository pongRepository;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Value( "${cache.sync.interval}" )
    private int syncInterval;

    private static final Logger LOG = LoggerFactory.getLogger( PongService.class );

    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool( 1 );

    @PostConstruct
    public void runSync() {
        scheduledExecutorService
                .scheduleAtFixedRate( (Runnable) this::dumpAllToDB, syncInterval, syncInterval, TimeUnit.MILLISECONDS );
    }

    public Pong updatePong( Ping ping ) {
        IMap<String, Pong> pongMap = hazelcastInstance.getMap( "pongMap" );
        Pong pong = pongMap.get( ping.getUserId() );
        if (pong == null) {
            Pong newPong = new Pong( ping.getUserId() );
            pongMap.set( ping.getUserId(), newPong.inc() );
            return newPong;
        } else {
            pongMap.set( ping.getUserId(), pong.inc() );
            return pong;
        }
    }

    public void dumpAllToDB() {
        LOG.info( "saving cache content" );
        IMap<String, Pong> pongMap = hazelcastInstance.getMap( "pongMap" );
        pongMap.values().stream().forEach( pongRepository::refresh );
    }
}
