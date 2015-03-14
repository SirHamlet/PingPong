package com.serge.pingpong;

import com.google.gson.Gson;
import com.serge.pingpong.controller.PingController;
import com.serge.pingpong.misc.ContextHolder;
import com.serge.pingpong.model.BaseRequest;
import com.serge.pingpong.model.Ping;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author Serge Ivanov
 *         Netty channel handler to handle http requests
 */
public class ChannelHandler extends ChannelInboundHandlerAdapter {

    private Gson gson = (Gson) ContextHolder.getInstance().getContext().getBean( Gson.class );

    private PingController pingController = ContextHolder.getInstance().getContext().getBean( PingController.class );

    private static final String PING = "PING";

    private static final Logger LOG = LoggerFactory.getLogger( ChannelHandler.class );

    @Override
    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
        LOG.warn( "Exception caught, closing channel", cause );
        ctx.channel().close();
    }

    /**
     * Receives http message from client
     * @param ctx Netty context
     * @param msg Some http message
     * @throws Exception
     */
    @Override
    public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            if (!httpRequest.getMethod().equals( HttpMethod.POST ) || !httpRequest.getUri().contains( "/handler" )) {
                sendBadRequest( ctx );
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            if (httpContent.content().isReadable()) {
                ByteBuf content = httpContent.content();
                byte[] bytes = new byte[content.readableBytes()];
                content.readBytes( bytes );
                content.release();
                BaseRequest request = gson.fromJson( new String( bytes ), BaseRequest.class );
                switch (request.getType()) {
                    case PING:
                        sendPong( ctx, bytes );
                        break;
                    default:
                        sendBadRequest( ctx );
                }
            }
        } else {
            ctx.close();
        }
    }

    private void sendPong( ChannelHandlerContext ctx, byte[] bytes ) {
        ctx.writeAndFlush( new DefaultFullHttpResponse( HTTP_1_1, OK,
                Unpooled.copiedBuffer(
                        gson.toJson( pingController.handleRequest(
                                gson.fromJson( new String( bytes ), Ping.class ) ) ), CharsetUtil.UTF_8 ) ) )
                .addListener( ChannelFutureListener.CLOSE );
    }

    private void sendBadRequest( ChannelHandlerContext ctx ) {
        ctx.writeAndFlush(
                new DefaultHttpResponse( HTTP_1_1, BAD_REQUEST ) )
                .addListener( ChannelFutureListener.CLOSE );
    }
}
