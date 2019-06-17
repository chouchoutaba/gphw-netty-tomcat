package com.gphw.netty.tomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

public class GpHwResponse {
    private ChannelHandlerContext ctx;
    private HttpRequest req;

    public GpHwResponse(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }

    /**
     * 返回数据
     * @param resp
     */
    public void write(String resp) throws Exception{
        try {
            if (resp == null || resp.length() == 0) {
                return;
            }
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(resp.getBytes("UTF-8"))
            );
            response.headers().set("Content-type", "text/html");
            ctx.write(response);
        }finally {
            ctx.flush();
            ctx.close();
        }
    }
}
