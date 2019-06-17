package com.gphw.netty.tomcat.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

public class GpHwRequest {
    private ChannelHandlerContext ctx;
    private HttpRequest req;

    public  GpHwRequest(ChannelHandlerContext ctx,HttpRequest req){
        this.ctx=ctx;
        this.req = req;
    }

    public String getUrl(){
        return req.uri();
    }

    public String getMethod (){
        return req.method().name();
    }

    public Map<String, List<String>> getParams(){
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        return decoder.parameters();
    }

    /**
     * 根据参数名获取参数值
     * @param name
     * @return
     */
    public String getParam(String name){
        Map<String, List<String>> map = getParams();
        List<String> list = map.get(name);
        if(null==list||list.size()<=0){
            return  null;
        }else{
            return list.get(0);
        }
    }
}
