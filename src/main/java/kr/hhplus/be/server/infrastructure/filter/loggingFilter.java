package kr.hhplus.be.server.infrastructure.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class loggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(loggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 요청 로깅
        logger.info("Request: method={}, uri={}", httpRequest.getMethod(), httpRequest.getRequestURI());

        // 체인을 통해 요청 처리
        chain.doFilter(request, response);

        // 응답 로깅
        logger.info("Response: status={}", httpResponse.getStatus());
    }

}
