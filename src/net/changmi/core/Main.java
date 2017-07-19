package net.changmi.core;

import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.changmi.netty.ProcessServerHandler;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class); 

	public static void main(String[] args) throws Exception{
//		try{
//			logger.info("System is booting...");
//			TaskProcess taskProcess = new TaskProcess();
//			TaskListen taskListen = new TaskListen(10501);
//			taskProcess.start();
//			taskListen.start();
//			logger.info("TaskListen is started.");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		NioEventLoopGroup master = new NioEventLoopGroup();
		NioEventLoopGroup slave = new NioEventLoopGroup();
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(master, slave) 				   // master用于监听请求，slave用于处理请求
					       .option(ChannelOption.SO_REUSEADDR, true)
					       .option(ChannelOption.SO_BACKLOG, 10000)
					       //.option(ChannelOption.SO_RCVBUF, 2048)
					       .childOption(ChannelOption.SO_RCVBUF, 1048576*20)
					       .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048)) // 这个才是接收长字符的关键，不加此代码，只能接收不超过1024长度的字符串
						   .channel(NioServerSocketChannel.class)  // 这个Nio服务端，如果是客户端则使用NioSocketChannel
						   .localAddress(30023)					   // 绑定端口号
						   .childHandler(new ChannelInitializer<Channel>() {

							@Override
							protected void initChannel(Channel channel) throws Exception {
								// TODO Auto-generated method stub
								channel.pipeline().addLast("decoder", new StringDecoder());  
								channel.pipeline().addLast("encoder", new StringEncoder());  
								//channel.pipeline().addLast(new LineBasedFrameDecoder(1024*5));
								//channel.pipeline().addLast(new StringDecoder());
								channel.pipeline().addLast(new ProcessServerHandler());
							}
							   
						});
			// 前面的相当于配置，此处才算上运行，注意加上.sync(); Nio是异步绑定，不会阻塞，因此，此处如果不设置同步，程序会立即结束。
			ChannelFuture channelFuture = serverBootstrap.bind().sync();
			channelFuture.channel().closeFuture().sync();
		} finally {
			slave.shutdownGracefully();
			master.shutdownGracefully();
		}

	}
}
