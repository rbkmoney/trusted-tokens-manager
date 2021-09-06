package com.rbkmoney.trusted.tokens.servlet;

import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

import java.io.IOException;

@WebServlet("/trusted/tokens")
public abstract class TrustedTokenServlet extends GenericServlet {

    private Servlet thriftServlet;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = servletHandler(new THServiceBuilder());
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }

    protected abstract Servlet servletHandler(THServiceBuilder builder);
}
