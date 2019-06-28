package com.beond.billvalidationservice.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.AuthenticationController;
import com.auth0.IdentityVerificationException;
import com.auth0.SessionUtils;
import com.auth0.Tokens;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/time")
@Api(value = "/time", description = "Get the time", tags = "time")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldEndpoint {

   private static final Logger LOG = LoggerFactory.getLogger(HelloWorldEndpoint.class);

   @GET
   @Path("/now")
   @ApiOperation(value = "Get the current time",
   notes = "Returns the time as a string",
   response = String.class
)
   @Produces(MediaType.APPLICATION_JSON)
   public String doGet(@Context HttpServletRequest req, @Context HttpHeaders headers) {
      List<String> requestHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
      LOG.warn("maybe this {}", requestHeader);

      Enumeration<String> headerNames = req.getHeaderNames();
      while (headerNames.hasMoreElements()) {
         LOG.warn("header {}", headerNames.nextElement());
      }

      LOG.warn("req {}", req.getRequestedSessionId());
      String[] strings = req.getParameterMap().get("state");
      for (String string : strings) {
         System.out.println("parameters: " + string);
         System.out.println("parameters: " + string);
      }

      try {
      JwkProvider urlJwkProvider = new UrlJwkProvider(
            new URL("https://dev-s4g920ra.eu.auth0.com/.well-known/jwks.json"));
      Jwk jwk = urlJwkProvider.get("MkM3NzNGRjM3MTgzREFFMzJEQzk0Nzg3OEY3QUZGRUUxMTlGQzI3Ng");
      RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();

      if (SessionUtils.get(req, "accessToken") != null) {
         LOG.warn("access token already exists {}", req.getRequestedSessionId());
         LOG.warn("access token {}", SessionUtils.get(req, "accessToken"));
         LOG.warn("access token {}", SessionUtils.get(req, "accessToken"));
      }
      else {
         LOG.warn("no access token");
      }

         final AuthenticationController authenticationController = AuthenticationControllerProvider.getInstance(req.getServletContext());

         Tokens tokens = authenticationController.handle(req);
         System.out.println("expires in " + tokens.getExpiresIn());

         System.out.println("shuld be bloody same " + req.getParameter("id_token"));

         SessionUtils.set(req, "accessToken", tokens.getAccessToken());
         SessionUtils.set(req, "idToken", tokens.getIdToken());

         String idtoken = tokens.getIdToken();
         try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("https://dev-s4g920ra.eu.auth0.com/")
                  .acceptLeeway(10).build();
            DecodedJWT jwt = verifier.verify(idtoken);
            String msg = new String(Base64.getDecoder().decode(jwt.getPayload()));
            String header = new String(Base64.getDecoder().decode(jwt.getHeader()));

            LOG.warn("Token decoded {}", new String(Base64.getDecoder().decode(tokens.getAccessToken())));
            final ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(msg);
            String firstName = rootNode.path("given_name").asText();
            String lastName = rootNode.path("family_name").asText();
            LOG.warn(idtoken);


            LOG.warn("expires at {}", jwt.getExpiresAt());
            LOG.warn("expires at {}", jwt.getClaims());
            LOG.warn("issued by {}", jwt.getIssuer());
            LOG.warn("issued by {}", jwt.getIssuer());

            LOG.warn("Header {}", header);
            LOG.warn("Payload {}", msg);
            LOG.warn("JWT dec {} {}", jwt.getIssuer(), jwt.getClaims());
            return String.format("{\"value\" : \"Hello %s %s\"}", firstName, lastName);
         } catch (JWTVerificationException | IOException e) {
            e.printStackTrace();
         }

      } catch (IdentityVerificationException e) {
         LOG.error("couldnt handle request");
         e.printStackTrace();
      } catch (MalformedURLException | JwkException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }



      return String.format("{\"value\" : \"The time is %s\"}", LocalDateTime.now());
   }

   @POST
   @Path("/now")
   public void doPost() {
      LOG.warn("here I am");
   }
}
