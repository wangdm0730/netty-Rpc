package com.lagou.client;

import com.lagou.util.JSONSerializer;
import com.lagou.util.RpcDecoder;
import com.lagou.util.RpcEncoder;
import com.lagou.util.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcConsumer {

    //创建线程池对象
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static UserClientHandler userClientHandler;
    //1.创建一个代理对象 providerName：UserService#sayHello success?
    public Object createProxy(final Class<?> serviceClass,final String providerName){
        //借助JDK动态代理生成代理对象
        return  Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{serviceClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //（1）调用初始化netty客户端的方法
                if(userClientHandler == null){
                 initClient();
                }
                //封装请求参数对象
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setParameters(args);
                // 设置参数
                userClientHandler.setPara(rpcRequest);
                // 去服务端请求数据
                return executor.submit(userClientHandler).get();
            }
        });
    }

    //2.初始化netty客户端
    public static  void initClient() throws InterruptedException {
         userClientHandler = new UserClientHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new StringDecoder());
//                        pipeline.addLast(new RpcEncoder(RpcRequest.class,new JSONSerializer()));
//                        pipeline.addLast(new RpcDecoder(RpcRequest.class,new JSONSerializer()));
                        pipeline.addLast(userClientHandler);
                    }
                });

        bootstrap.connect("127.0.0.1",8990).sync();
        System.out.println("客户端连接远程服务成功，端口号："+8990);
    }

}
