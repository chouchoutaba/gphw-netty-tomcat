package com.gphw.netty.tomcat.servlet;

import com.gphw.netty.tomcat.http.GpHwRequest;
import com.gphw.netty.tomcat.http.GpHwResponse;
import com.gphw.netty.tomcat.http.GpHwServlet;

public class GpHwFailServlet extends GpHwServlet {

    @Override
    public void doGet(GpHwRequest req, GpHwResponse resp) throws Exception{
        doPost(req, resp);
    }

    @Override
    public void doPost(GpHwRequest req, GpHwResponse resp) throws Exception{
        resp.write("Hello,You are fail!!");
    }
}
