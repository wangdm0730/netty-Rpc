package com.lagou.handler;

import com.lagou.service.UserServiceImpl;
import com.lagou.util.JSONSerializer;
import com.lagou.util.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class UserServerHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {

    public static ApplicationContext applicationContext;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RpcRequest rpcRequest = new JSONSerializer().deserialize(RpcRequest.class, msg.toString().getBytes());
        // 判断是否符合约定，符合则调用本地方法，返回数据
        // msg:  UserService#success
        Object[] parameters = rpcRequest.getParameters();
        if(parameters[0].toString().equals("success")){
            UserServiceImpl userService = (UserServiceImpl) getObject("userServiceImpl");
            String result = userService.sayHello(parameters[0].toString());
            ctx.writeAndFlush(result);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static Object getObject(String name){
        return applicationContext.getBean(name);
    }
}
