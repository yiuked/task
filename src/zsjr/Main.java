package zsjr;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.zsjr.netty.ProcessServerHandler;

import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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
						   .channel(NioServerSocketChannel.class)  // ���Nio����ˣ�����ǿͻ�����ʹ��NioSocketChannel
						   .localAddress(30023)					   // �󶨶˿ں�
						   .childHandler(new ChannelInitializer<Channel>() {

							@Override
							protected void initChannel(Channel channel) throws Exception {
								// TODO Auto-generated method stub
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
