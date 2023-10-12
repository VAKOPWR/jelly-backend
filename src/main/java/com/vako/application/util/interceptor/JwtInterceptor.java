package com.vako.application.util.interceptor;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

   private FirebaseToken decodedToken;


   @Override
   public boolean preHandle(
           HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getHeader(HttpHeaders.AUTHORIZATION));
      request.setAttribute("FirebaseToken", decodedToken);
      return true;
   }
}