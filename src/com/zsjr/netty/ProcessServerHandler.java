package com.zsjr.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ProcessServerHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	 ByteBuf m = (ByteBuf) msg;
         try {
             // 打印出来
             System.out.println(m.toString(io.netty.util.CharsetUtil.US_ASCII));
             byte[] req = "SUCESS".getBytes();
             ByteBuf firstMessage = Unpooled.buffer(req.length);
             firstMessage.writeBytes(req);
             ChannelFuture f = ctx.writeAndFlush(firstMessage);
             f.addListener(ChannelFutureListener.CLOSE);
         } finally {
             m.release();
         }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}