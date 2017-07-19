package net.changmi.core;

import org.apache.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.changmi.netty.ProcessClientHandler;
import net.changmi.netty.ProcessServerHandler;

public class Client {
	private static Logger logger = Logger.getLogger(Main.class); 

	public static void main(String[] args) throws Exception{
		NioEventLoopGroup master = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(master); 				   // master用于监听请求，slave用于处理请求
			bootstrap.channel(NioSocketChannel.class);  // 这个Nio服务端，如果是客户端则使用NioSocketChannel
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel sc) throws Exception {
								sc.pipeline().addLast(new ProcessClientHandler());
							}
							   
						});
			bootstrap.option(ChannelOption.TCP_NODELAY, true);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
			ChannelFuture cf = bootstrap.connect("127.0.0.1", 30023).sync();
			String json = "1.{last_ip=127.0.0.1, logintime=3, block_status=0, paypassword=null, tuijian_userid=1, terminal=0, type=1, up_ip=171.214.213.210, realname=, spreads_key=045082, password=7e100345eb9c6a1c192a90001016cce2, province=40, user_id=2, phone=13688045082, last_time=1457846897, reg_time=1457747459, up_time=1457844047, reg_ip=127.0.0.1, yibao=1, email=, username=zsjr_20145} \r\n";
			json += "2.{last_ip=127.0.0.1, logintime=3, block_status=0, paypassword=null, tuijian_userid=1, terminal=0, type=1, up_ip=171.214.213.210, realname=, spreads_key=045082, password=7e100345eb9c6a1c192a90001016cce2, province=40, user_id=2, phone=13688045082, last_time=1457846897, reg_time=1457747459, up_time=1457844047, reg_ip=127.0.0.1, yibao=1, email=, username=zsjr_20145} \r\n";
			json += "3.{last_ip=127.0.0.1, logintime=3, block_status=0, paypassword=null, tuijian_userid=1, terminal=0, type=1, up_ip=171.214.213.210, realname=, spreads_key=045082, password=7e100345eb9c6a1c192a90001016cce2, province=40, user_id=2, phone=13688045082, last_time=1457846897, reg_time=1457747459, up_time=1457844047, reg_ip=127.0.0.1, yibao=1, email=, username=zsjr_20145} \r\n";
			json += "4.{last_ip=127.0.0.1, logintime=3, block_status=0, paypassword=null, tuijian_userid=1, terminal=0, type=1, up_ip=171.214.213.210, realname=, spreads_key=045082, password=7e100345eb9c6a1c192a90001016cce2, province=40, user_id=2, phone=13688045082, last_time=1457846897, reg_time=1457747459, up_time=1457844047, reg_ip=127.0.0.1, yibao=1, email=, username=zsjr_20145} \r\n";
			json += "5.{last_ip=127.0.0.1, logintime=3, block_status=0, paypassword=null, tuijian_userid=1, terminal=0, type=1, up_ip=171.214.213.210, realname=, spreads_key=045082, password=7e100345eb9c6a1c192a90001016cce2, province=40, user_id=2, phone=13688045082, last_time=1457846897, reg_time=1457747459, up_time=1457844047, reg_ip=127.0.0.1, yibao=1, email=, username=zsjr_20145} \r\n";

			
			cf.channel().write(Unpooled.copiedBuffer(json.getBytes()));
			cf.channel().flush();
			cf.channel().close().sync();
		} finally {
			master.shutdownGracefully();
		}

	}
}
