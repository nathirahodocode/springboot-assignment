package com.example.assignment.config;

import com.example.assignment.util.CorrelationIdHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_LENGTH = 2000;
    private static final int CACHE_LIMIT = 0; // 0 = unlimited caching
    private static final String CORRELATION_HEADER = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String correlationId = request.getHeader(CORRELATION_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        CorrelationIdHolder.set(correlationId);
        MDC.put("correlationId", correlationId);
        response.setHeader(CORRELATION_HEADER, correlationId);

        // Spring Boot 4 (Spring Framework 7) requires buffer size as 2nd arg
        ContentCachingRequestWrapper reqWrapper = new ContentCachingRequestWrapper(request, CACHE_LIMIT);
        ContentCachingResponseWrapper resWrapper = new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();
        try {
            chain.doFilter(reqWrapper, resWrapper);
        } finally {
            long duration = System.currentTimeMillis() - start;
            log.info("HTTP {} {} -> {} | duration={}ms | reqBody={} | resBody={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    resWrapper.getStatus(),
                    duration,
                    truncate(new String(reqWrapper.getContentAsByteArray(), StandardCharsets.UTF_8)),
                    truncate(new String(resWrapper.getContentAsByteArray(), StandardCharsets.UTF_8)));

            resWrapper.copyBodyToResponse();
            MDC.remove("correlationId");
            CorrelationIdHolder.clear();
        }
    }

    private String truncate(String body) {
        if (body == null) return "";
        body = maskSensitive(body);
        return body.length() > MAX_PAYLOAD_LENGTH
                ? body.substring(0, MAX_PAYLOAD_LENGTH) + "...[truncated]"
                : body;
    }

    private String maskSensitive(String body) {
        return body.replaceAll("(?i)(\"password\"\\s*:\\s*\")[^\"]*(\")", "$1***$2");
    }
}