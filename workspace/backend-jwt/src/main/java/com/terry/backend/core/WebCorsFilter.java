package com.terry.backend.core;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebCorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Headers", "X-Frame-Options,Content-Type,Authorization");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.addHeader("Cache-Control", "no-cache");
        //추가
        res.addHeader("X-Frame-Options","SAMEORIGIN");
        // \\추가
        if (!"OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {

    }

}
