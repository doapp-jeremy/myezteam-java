/**
 * UserResource.java
 * myezteam-webservices
 * 
 * Created by jeremy on Sep 12, 2013
 * DoApp, Inc. owns and reserves all rights to the intellectual
 * property and design of the following application.
 *
 * Copyright 2013 - All rights reserved.  Created by DoApp, Inc.
 */
package com.myezteam.resource;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.http.Body;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;


/**
 * @author jeremy
 * 
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

  @GET
  @Timed
  public List<String> list() {
    return Lists.newArrayList();
  }

  @Path("/login.js")
  @GET
  @Timed
  @Produces(MediaType.TEXT_PLAIN)
  public String loginJS() {
    return "console.log('loaded');\n"
        + "var signinLink = document.getElementById('signin');\n"
        + "if (signinLink) { \nsigninLink.onclick = function() { navigator.id.request(); }; }\n"
        + "var signoutLink = document.getElementById('signout');\n"
        + "if (signoutLink) { \nsignoutLink.onclick = function() { navigator.id.logout(); }; }\n"
        + "";
  }

  @Path("/login")
  @GET
  @Timed
  @Produces(MediaType.TEXT_HTML)
  public String login() {
    return "<html>"
        + "<head>"
        + "<script src='//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js'></script>"
        + "<script src='https://login.persona.org/include.js'></script>"
        + "</head>"
        + "<body>"
        + "<button id='signin'>Login</button>"
        + "<button id='signout'>Logout</button>"
        + "<script src='http://static.mobilelocalnews.com/persona/login.js'></script>"
        + "</body>"
        + ""
        + ""
        + "</html>";
  }

  private interface PersonaService {
    @retrofit.http.POST("/verify")
    public Map<String, String> verify(@Body Map<String, String> body);
  }

  @Path("/login")
  @POST
  @Timed
  public Map<String, String> login(Map<String, String> data) {
    try {
      String assertion = checkNotNull(data).get("assertion");
      checkArgument(!Strings.isNullOrEmpty(assertion));

      Map<String, String> request = new HashMap<String, String>();
      request.put("assertion", assertion);
      request.put("audience", "http://localhost:8080");

      PersonaService personaService = new RestAdapter.Builder()
          .setServer("https://verifier.login.persona.org")
          .build()
          .create(PersonaService.class);

      Map<String, String> response = personaService.verify(request);
      System.out.println("Users email: " + response.get("email"));

      return response;
    } catch (RetrofitError e) {
      Object body = e.getBody();
      System.err.println(body);
      throw new WebApplicationException(e);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  @Path("/logout")
  @POST
  @Timed
  public String logout() {
    try {
      return "logout";
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }
}
