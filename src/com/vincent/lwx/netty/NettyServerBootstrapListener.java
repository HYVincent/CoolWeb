package com.vincent.lwx.netty;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author 徐飞
 * @version 2016/02/24 19:43
 */
public class NettyServerBootstrapListener implements ServletContextListener{
	
//	private static final Logger logger = LogManager.getLogger(NettyServerBootstrapListener.class);

	public void contextInitialized(ServletContextEvent sce) {
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, worker);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_BACKLOG, 128);
		// 通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		// 保持长连接状态
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel socketChannel) throws Exception {
				ChannelPipeline p = socketChannel.pipeline();
				// 添加对象解码器 负责对序列化POJO对象进行解码 设置对象序列化最大长度为1M 防止内存溢出
				// 设置线程安全的WeakReferenceMap对类加载器进行缓存 支持多线程并发访问 防止内存溢出
				p.addLast(new ObjectDecoder(1024 * 1024,
						ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
				p.addLast(new ObjectEncoder());
				p.addLast(new NettyServerHandler());
			}
		});
		ChannelFuture f = null;
		try {
			f = bootstrap.bind(9999).sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (f.isSuccess()) {
			System.out.println("netty server start success!");
//			logger.debug("netty service start is success");
		}else{
//			logger.debug("netty service start error,please check code!");
			System.out.println("netty service start error,please check code!");
		}
	}

}
