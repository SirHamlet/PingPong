package com.serge.pingpong.repository;

import com.serge.pingpong.model.Pong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by Serge Ivanov
 */
@Component
public class PongRepositoryImpl implements PongRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void refresh( Pong pong ) {
        mongoTemplate.upsert( query( where( "userId" ).is( pong.getUserId() ) ),
                new Update() {{
                    set( "pongCount", pong.getPongCount() );
                    set( "userId", pong.getUserId() );
                }},
                Pong.class );
    }
}
