package com.lagou.client;

import com.lagou.service.UserService;

public class ClientBootStrap {

    public static  final String providerName="UserService#sayHello#";
    public static void main(String[] args) throws InterruptedException {
        RpcConsumer rpcConsumer = new RpcConsumer();
        UserService proxy = (UserService) rpcConsumer.createProxy(UserService.class, providerName);
        while (true){
            System.out.println(proxy.sayHello("success"));
            Thread.sleep(2000);
        }
    }

}
