//package com.kcb.recon.tool.authentication.security;
//
//import com.kcb.recon.tool.authentication.entities.User;
//import com.kcb.recon.tool.authentication.repositories.UserSessionsRepository;
//import com.kcb.recon.tool.common.entities.AuditTrail;
//import com.kcb.recon.tool.common.repositories.AuditTrailsRepository;
//import com.google.gson.Gson;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.util.ContentCachingRequestWrapper;
//import org.springframework.web.util.ContentCachingResponseWrapper;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.*;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Value("${cors.origin}")
//    private String origin;
//
//    private final JwtTokenService jwtService;
//    private final UserDetailsService userDetailsService;
//    private final AuditTrailsRepository auditTrailsRepository;
//    private final UserSessionsRepository userTokenRepository;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        response.addHeader("Access-Control-Allow-Origin", origin);
//        response.addHeader("Access-Control-Allow-Credentials", "true");
//        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
//        response.addHeader("Access-Control-Allow-Headers", "*");
//
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            response.setStatus(HttpServletResponse.SC_OK);
//            return;
//        }
//
//        String authHeader = request.getHeader("Authorization");
//        String token = null;
//        String username = null;
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//            try {
//                username = jwtService.extractUsername(token);
//            } catch (io.jsonwebtoken.ExpiredJwtException e) {
//                log.warn("JWT Token has expired: {}", e.getMessage());
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setContentType("application/json");
//                response.getWriter().write("{\"error\": \"Token has expired. Please log in again.\"}");
//                return;
//            } catch (Exception e) {
//                log.error("JWT parsing error: {}", e.getMessage());
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setContentType("application/json");
//                response.getWriter().write("{\"error\": \"Invalid token.\"}");
//                return;
//            }
//        }
//
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//            if (userDetails instanceof User user) {
//                Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
//                if (jwtService.validateToken(token, userDetails)) {
//                    UsernamePasswordAuthenticationToken authToken =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
//                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//
//    private void logRequestDetails(HttpServletRequest request) {
//        RequestInfo info = new RequestInfo();
//        info.setMethod(request.getMethod());
//        info.setRequestURI(request.getRequestURI());
//        info.setRemoteAddr(request.getRemoteAddr());
//        info.setHeaders(extractHeaders(request));
//        info.setParameters(extractParameters(request));
//
//        log.info("{} - {} - Request", info.getRequestURI(), new Date());
//        log.info(new Gson().toJson(info));
//    }
//
//    private void logResponseDetails(HttpServletRequest request, HttpServletResponse response,
//                                    ContentCachingRequestWrapper requestWrapper,
//                                    ContentCachingResponseWrapper responseWrapper, long startTime) throws IOException {
//        int statusCode = response.getStatus();
//        log.info("Response Status: {}", statusCode);
//        long timeTaken = System.currentTimeMillis() - startTime;
//        String requestBody = getStringValue(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding());
//        String responseBody = getStringValue(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());
//        log.info(
//                "Finished Processing : METHOD={}; REQUESTURI={}; REQUEST PAYLOAD={}; RESPONSE CODE={}; RESPONSE={}; TIME TAKEN={} milliseconds",
//                request.getMethod(), request.getRequestURI(), requestBody, response.getStatus(), responseBody,
//                timeTaken);
//
//        AuditTrail auditTrail = new AuditTrail();
//        auditTrail.setRemoteUrl(request.getRequestURI());
//        auditTrail.setRequestAddr(request.getRemoteAddr());
//        auditTrail.setRequestMethod(request.getMethod());
//        auditTrail.setRequestIp(request.getRemoteHost());
//        auditTrail.setRemotePort(request.getRemotePort());
//        auditTrail.setRemoteUser(SecurityContextHolder.getContext().getAuthentication() != null ?
//                SecurityContextHolder.getContext().getAuthentication().getName() : null);
//        auditTrail.setRequestParameters(new Gson().toJson(extractParameters(request)));
//
//        if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {
//            auditTrailsRepository.save(auditTrail);
//        }
//
//        responseWrapper.copyBodyToResponse();
//    }
//
//    private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
//        try {
//            return new String(contentAsByteArray, characterEncoding);
//        } catch (UnsupportedEncodingException e) {
//            log.error(e.getMessage());
//        }
//        return "";
//    }
//
//    private Map<String, String> extractHeaders(HttpServletRequest request) {
//        Map<String, String> headers = new HashMap<>();
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            headers.put(headerName, request.getHeader(headerName));
//        }
//        return headers;
//    }
//
//    private Map<String, String[]> extractParameters(HttpServletRequest request) {
//        Map<String, String[]> parameters = new HashMap<>();
//        Enumeration<String> parameterNames = request.getParameterNames();
//        while (parameterNames.hasMoreElements()) {
//            String paramName = parameterNames.nextElement();
//            parameters.put(paramName, request.getParameterValues(paramName));
//        }
//        return parameters;
//    }
//
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class RequestInfo {
//        private String method;
//        private String requestURI;
//        private String remoteAddr;
//        private Map<String, String> headers;
//        private Map<String, String[]> parameters;
//    }
//}
