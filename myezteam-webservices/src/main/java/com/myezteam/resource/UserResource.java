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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.codahale.dropwizard.auth.Auth;
import com.codahale.metrics.annotation.Timed;
import com.myezteam.api.User;
import com.myezteam.application.WsEmailAuth;


/**
 * @author jeremy
 * 
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
  private final WsEmailAuth auth;

  public UserResource(WsEmailAuth auth) {
    this.auth = auth;
  }

  @GET
  @Timed
  public User me(@Auth User user) {
    return new User("blah@gmail.com");
  }

  @Timed
  @GET
  @Path("/index")
  @Produces(MediaType.TEXT_HTML)
  public String index() {
    return "<html ng-app>"
        + "<head>"
        + "<script src='https://ajax.googleapis.com/ajax/libs/angularjs/1.0.8/angular.min.js'></script>"
        + "<script src='https://ajax.googleapis.com/ajax/libs/angularjs/1.0.8/angular-resource.min.js'></script>"
        + "<script src='http://s3.amazonaws.com/static.myezteam.com/js/persona/controllers.js'></script>"
        + "</head>"
        + "<body ng-controller='MeCtrl'>"
        + "<ul>"
        + "<li>{{user.uuid}}</li>"
        + "</ul>"
        + "</body>"
        + "</html>";
  }

  @GET
  @Path("/{token}")
  @Timed
  public User token(@PathParam("token") String token) {
    return auth.getUser(token);
  }

}
