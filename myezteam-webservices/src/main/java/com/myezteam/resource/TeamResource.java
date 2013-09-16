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

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.codahale.dropwizard.auth.Auth;
import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import com.myezteam.api.Team;
import com.myezteam.api.User;


/**
 * @author jeremy
 * 
 */
@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamResource {

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
        + "<body ng-controller='TeamCtrl'>"
        + "<h1>Teams</h1>"
        + "<ul class='teams'>"
        + "<li ng-repeat='team in teams' class='thumbnail'>"
        + "{{team.name}}"
        + "</li>"
        + "</ul>"
        + "</body>"
        + "</html>";
  }

  @Timed
  @GET
  public List<Team> list(@Auth User authUser) {
    return Lists.newArrayList(new Team(UUID.randomUUID().toString()));
  }

  @Timed
  @POST
  public Team create(@Valid Team team) {
    return team;
  }

}
