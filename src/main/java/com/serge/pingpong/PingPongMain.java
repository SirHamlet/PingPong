package com.serge.pingpong;

import com.serge.pingpong.misc.ContextHolder;
import com.serge.pingpong.service.PongService;
import io.netty.bootstrap.ServerBootstrap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Serge Ivanov
 *         Main entry point for the app
 */
public class PingPongMain {

    public static void main( String[] args ) throws InterruptedException {

        ApplicationContext context = new AnnotationConfigApplicationContext( PingPongConfig.class );
        ContextHolder.getInstance().setContext( new AnnotationConfigApplicationContext( PingPongConfig.class ) );
        Runtime.getRuntime().addShutdownHook( new ShutdownSyncHook( context.getBean( PongService.class ) ) );
        ServerBootstrap bootstrap = context.getBean( ServerBootstrap.class );
        bootstrap
                .bind( Integer.parseInt( context.getEnvironment().getProperty( "server.port" ) ) )
                .sync()
                .channel()
                .closeFuture()
                .sync();
    }

    /**
     * This hook is needed to handle kill signal from OS
     */
    private static class ShutdownSyncHook extends Thread {

        PongService pongService;

        public ShutdownSyncHook( PongService pongService ) {
            this.pongService = pongService;
        }

        @Override
        public void run() {
            System.out.println( "Going to save cache content. Then shutdown." );
            pongService.dumpAllToDB();
        }
    }
}
