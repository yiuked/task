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
			serverBootstrap.group(master, slave) 				   // master���ڼ�������slave���ڴ�������
					       .option(ChannelOption.SO_REUSEADDR, true)
					       .option(ChannelOption.SO_BACKLOG, 10000)
					       //.option(ChannelOption.SO_RCVBUF, 2048)
					       .childOption(ChannelOption.SO_RCVBUF, 1048576*20)
					       .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048)) // ������ǽ��ճ��ַ��Ĺؼ������Ӵ˴��룬ֻ�ܽ��ղ�����1024���ȵ��ַ���
						   .channel(NioServerSocketChannel.class)  // ���Nio����ˣ�����ǿͻ�����ʹ��NioSocketChannel
						   .localAddress(30023)					   // �󶨶˿ں�
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
			// ǰ����൱�����ã��˴����������У�ע�����.sync(); Nio���첽�󶨣�������������ˣ��˴����������ͬ�������������������
			ChannelFuture channelFuture = serverBootstrap.bind().sync();
			channelFuture.channel().closeFuture().sync();
		} finally {
			slave.shutdownGracefully();
			master.shutdownGracefully();
		}

	}
}
