package com.gphw.netty.tomcat.http;

public abstract class GpHwServlet {
    public void service (GpHwRequest req,GpHwResponse resp)throws Exception{
        //根据method判断执行什么方法
        if("GET".equalsIgnoreCase(req.getMethod())){
            doGet(req,resp);
        }else if("POST".equalsIgnoreCase(req.getMethod())){
            doPost(req,resp);
        }
    }

    public abstract void doGet(GpHwRequest req,GpHwResponse resp)throws Exception;

    public abstract void doPost(GpHwRequest req,GpHwResponse resp)throws Exception;
}
