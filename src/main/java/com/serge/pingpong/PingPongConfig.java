package com.serge.pingpong;

import com.google.gson.Gson;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.GroupProperties;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.serge.pingpong.service.PongSerializer;
import com.serge.pingpong.model.Pong;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author Serge Ivanov
 *         Spring config for this app.
 */
@Configuration
@ComponentScan( "com.serge.pingpong" )
@PropertySource( "classpath:all.properties" )
@EnableMongoRepositories
public class PingPongConfig extends AbstractMongoConfiguration {

    @Value( "${server.port}" )
    private int serverPort;

    @Value( "${server.boss.pool.size}" )
    private int bossCount;

    @Value( "${server.worker.pool.size}" )
    private int workerCount;

    @Value( "${hz.port}" )
    private int hzPort;

    @Value( "${hz.name}" )
    private String hzName;

    @Value( "${hz.password}" )
    private String hzPassword;

    @Value( "${mongo.db.name}" )
    private String mongoDbName;

    @Value( "${mongo.host}" )
    private String mongoDbHost;

    @Value( "${mongo.port}" )
    private int mongoDbPort;

    @Bean( name = "bossGroup", destroyMethod = "shutdownGracefully" )
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup( bossCount );
    }

    @Bean( name = "workerGroup", destroyMethod = "shutdownGracefully" )
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup( workerCount );
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public PongSerializer pongSerializer() {
        return new PongSerializer();
    }

    @Override
    public MongoTemplate mongoTemplate() throws Exception {
        return super.mongoTemplate();
    }

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config cfg = new Config();
        cfg.getSerializationConfig().addSerializerConfig( new SerializerConfig() {{
            setTypeClass( Pong.class );
            setImplementation( pongSerializer() );
        }} );
        NetworkConfig network = cfg.getNetworkConfig();
        network.setPort( hzPort );
        cfg.setProperty( "hazelcast.initial.min.cluster.size", "1" );
        cfg.setProperty( GroupProperties.PROP_SHUTDOWNHOOK_ENABLED, "false");
        cfg.setGroupConfig( new GroupConfig( hzName, hzPassword ) );
        JoinConfig join = network.getJoin();
        join.getTcpIpConfig().setEnabled( false );
        join.getAwsConfig().setEnabled( false );
        join.getMulticastConfig().setEnabled( false );
        return Hazelcast.newHazelcastInstance( cfg );
    }

    @Bean( name = "bootstrap" )
    public ServerBootstrap bootstrap() throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        b.group( bossGroup(), workerGroup() )
                .channel( NioServerSocketChannel.class )
                .handler( new LoggingHandler( LogLevel.INFO ) )
                .option( ChannelOption.SO_REUSEADDR, true )
                .childHandler( new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel( SocketChannel socketChannel ) throws Exception {
                        socketChannel.pipeline().addLast( new LoggingHandler( LogLevel.INFO ) )
                                .addLast( new HttpRequestDecoder() )
                                .addLast( new HttpObjectAggregator( 1048576 ) )
                                .addLast( new HttpResponseEncoder() )
                                .addLast( new ChannelHandler() );
                    }
                } )
                .childOption( ChannelOption.AUTO_READ, true )
                .childOption( ChannelOption.SO_REUSEADDR, true );
        return b;
    }

    /**
     * Necessary to make the Value annotations work.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    protected String getDatabaseName() {
        return mongoDbName;
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient( mongoDbHost, mongoDbPort );
    }
}
