package com.lagou.client;

import com.lagou.util.JSONSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class UserClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;
    private String result;
    private Object para;


    public void channelActive(ChannelHandlerContext ctx) {
        context = ctx;
    }

    /**
     * 收到服务端数据，唤醒等待线程
     */

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
        result = msg.toString();
        System.out.println("channelRead执行完成"+result);
        notify();
    }

    /**
     * 写出数据，开始等待唤醒
     */

    public synchronized Object call() throws InterruptedException {
        context.writeAndFlush(new String(new JSONSerializer().serialize(para)));
        System.out.println("call方法执行成功"+para);
        wait();
        return result;
    }

    /*
     设置参数
     */
    void setPara(Object para) {
        this.para = para;
    }

}
