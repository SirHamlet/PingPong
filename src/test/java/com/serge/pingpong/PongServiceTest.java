package com.serge.pingpong;

import com.serge.pingpong.model.Ping;
import com.serge.pingpong.model.Pong;
import com.serge.pingpong.service.PongService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Serge Ivanov
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = PingPongConfig.class )
public class PongServiceTest {

    @Autowired
    PongService pongService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void testSync() {

        Ping firstPing = new Ping( "first" );
        Ping secondPing = new Ping( "second" );
        Ping thirdPing = new Ping( "third" );

        pongService.updatePong( firstPing );

        pongService.updatePong( secondPing );
        pongService.updatePong( secondPing );

        pongService.updatePong( thirdPing );
        pongService.updatePong( thirdPing );
        pongService.updatePong( thirdPing );

        pongService.dumpAllToDB();

        Pong dbFirstPong = mongoTemplate.findOne( query( where( "userId" ).is( firstPing.getUserId() ) ), Pong.class );
        Pong dbSecondPong = mongoTemplate.findOne( query( where( "userId" ).is( secondPing.getUserId() ) ), Pong.class );
        Pong dbThirdPong = mongoTemplate.findOne( query( where( "userId" ).is( thirdPing.getUserId() ) ), Pong.class );

        assertEquals( 1, dbFirstPong.getPongCount() );
        assertEquals( 2, dbSecondPong.getPongCount() );
        assertEquals( 3, dbThirdPong.getPongCount() );
    }

}
