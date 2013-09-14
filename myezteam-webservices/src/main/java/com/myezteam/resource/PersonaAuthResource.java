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
import com.myezteam.application.WsEmailAuth;


/**
 * @author jeremy
 * 
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonaAuthResource {
  private final WsEmailAuth auth;

  public PersonaAuthResource(WsEmailAuth auth) {
    this.auth = auth;
  }

  @GET
  @Timed
  public List<String> list() {
    return Lists.newArrayList();
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
        + "<script src='http://s3.amazonaws.com/static.myezteam.com/js/persona/login.js'></script>"
        + "</body>"
        + ""
        + ""
        + "</html>";
  }

  private interface PersonaService {
    @retrofit.http.POST("/verify")
    public Map<String, String> verify(@Body Map<String, String> body);
  }

  @Path("/persona/login")
  @POST
  @Timed
  public Map<String, String> login(Map<String, String> data) {
    try {
      String assertion = checkNotNull(data).get("assertion");
      checkArgument(!Strings.isNullOrEmpty(assertion), "Assertion is empty or null");
      String audience = checkNotNull(data).get("audience");
      checkArgument(!Strings.isNullOrEmpty(audience), "Audience is empty or null");

      PersonaService personaService = new RestAdapter.Builder()
          .setServer("https://verifier.login.persona.org")
          .build()
          .create(PersonaService.class);

      Map<String, String> personaResponse = personaService.verify(data);
      String email = checkNotNull(personaResponse.get("email"), "Could not verify persona, email is empty");
      String token = auth.validateEmail(email);

      Map<String, String> response = new HashMap<String, String>();
      response.put("email", email);
      response.put("token", token);
      return response;
    } catch (RetrofitError e) {
      Object body = e.getBody();
      if (body instanceof String) { throw new WebApplicationException(new Exception((String) body)); }
      throw new WebApplicationException(e);
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Path("/logout")
  @POST
  @Timed
  public void logout(String email) {
    try {
      auth.removeEmail(email);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }
}