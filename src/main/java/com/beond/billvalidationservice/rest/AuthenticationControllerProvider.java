package com.beond.billvalidationservice.rest;

import javax.servlet.ServletContext;

import com.auth0.AuthenticationController;

public abstract class AuthenticationControllerProvider {

   public static AuthenticationController getInstance(ServletContext servletContext) {
      String domain = servletContext.getInitParameter("com.auth0.domain");
      String clientId = servletContext.getInitParameter("com.auth0.clientId");
      String clientSecret = servletContext.getInitParameter("com.auth0.clientSecret");
      return AuthenticationController.newBuilder(domain, clientId, clientSecret).withResponseType("code").build();
   }

}
