package com.gphw.netty.tomcat;

import com.gphw.netty.tomcat.http.GpHwRequest;
import com.gphw.netty.tomcat.http.GpHwResponse;
import com.gphw.netty.tomcat.http.GpHwServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GpHwTomcat {

    private  int port = 8088;

    private Properties webxml = new Properties();

    private Map<String, GpHwServlet> servletMapping = new HashMap<String, GpHwServlet>();

    private void init(){
        try {
            String WEB_INF = this.getClass().getResource("/").getPath();
            InputStream fis = new FileInputStream(WEB_INF + "web.properties");
            webxml.load(fis);
            for (Object obj:webxml.keySet()){
                String key = obj.toString();
                if(key.endsWith(".url")) {
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName + ".className");
                    GpHwServlet servlet = (GpHwServlet) Class.forName(className).newInstance();
                    servletMapping.put(url, servlet);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void start(){
        init();

        //Netty封装了NIO，Reactor模型，Boss，worker
        // Boss线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // Worker线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // Netty服务
            //ServetBootstrap   ServerSocketChannel
            ServerBootstrap server = new ServerBootstrap();
            // 链路式编程
            server.group(bossGroup, workerGroup)
                    // 主线程处理类,看到这样的写法，底层就是用反射
                    .channel(NioServerSocketChannel.class)
                    // 子线程处理类 , Handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 客户端初始化处理
                        protected void initChannel(SocketChannel client) throws Exception {
                            //Netty对HTTP协议的封装，顺序有要求
                            // HttpResponseEncoder 编码器
                            client.pipeline().addLast(new HttpResponseEncoder());
                            // HttpRequestDecoder 解码器
                            client.pipeline().addLast(new HttpRequestDecoder());
                            // 业务逻辑处理
                            client.pipeline().addLast(new GpHwTomcatHandler());
                        }

                    })
                    // 针对主线程的配置 分配线程最大数量 128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 针对子线程的配置 保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = server.bind(port).sync();
            System.out.println("GpHw Tomcat 已启动，监听的端口是：" + port);
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public class GpHwTomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest){
                HttpRequest req = (HttpRequest) msg;

                // 转交给我们自己的request实现
                GpHwRequest request = new GpHwRequest(ctx,req);
                // 转交给我们自己的response实现
                GpHwResponse response = new GpHwResponse(ctx,req);
                // 实际业务处理
                String url = request.getUrl();

                if(servletMapping.containsKey(url)){
                    servletMapping.get(url).service(request, response);
                }else{
                    response.write("404 - Not Found");
                }

            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }

    }

    /**
     * 启动
     * @param args
     */
    public static void main(String[] args) {
        new GpHwTomcat().start();
    }
}
