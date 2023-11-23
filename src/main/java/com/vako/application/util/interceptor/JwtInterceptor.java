package com.vako.application.util.interceptor;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.vako.exception.JellyException;
import com.vako.exception.JellyExceptionHandler;
import com.vako.exception.JellyExceptionType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

   private FirebaseToken decodedToken;

   private JellyExceptionHandler jellyExceptionHandler;

   @Override
   public boolean preHandle(
           HttpServletRequest request, HttpServletResponse response, Object handler) {
      try {
         String tokenId  = request.getHeader(HttpHeaders.AUTHORIZATION);
         FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(tokenId);
         request.setAttribute("FirebaseToken", decodedToken);
         return true;
      } catch (FirebaseAuthException e) {
         System.out.println(e);
         throw new JellyException(JellyExceptionType.NOT_AUTHORIZED);
      }
   }
}