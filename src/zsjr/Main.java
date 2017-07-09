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
			serverBootstrap.group(master, slave) 				   // master用于监听请求，slave用于处理请求
						   .channel(NioServerSocketChannel.class)  // 这个Nio服务端，如果是客户端则使用NioSocketChannel
						   .localAddress(30023)					   // 绑定端口号
						   .childHandler(new ChannelInitializer<Channel>() {

							@Override
							protected void initChannel(Channel channel) throws Exception {
								// TODO Auto-generated method stub
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
