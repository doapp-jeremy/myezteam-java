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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import com.codahale.dropwizard.auth.Auth;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;
import com.myezteam.api.Team;
import com.myezteam.api.User;
import com.myezteam.application.AwsConfiguration;
import com.myezteam.application.CollectionMapper;


/**
 * @author jeremy
 * 
 */
@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamResource {
  static final String UUID = "uuid";
  static final String UUID_PATH = "/{" + UUID + "}";

  private final CollectionMapper collectionMapper;

  public TeamResource(AwsConfiguration awsConfiguration) {
    this.collectionMapper = new CollectionMapper(awsConfiguration);
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
  @Path(UUID_PATH)
  public Team get(@Auth User authUser, @PathParam(UUID) String teamUUID) {
    try {
      checkArgument(!Strings.isNullOrEmpty(teamUUID), "Team UUID is empty");
      return collectionMapper.get(new Team(teamUUID));
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @GET
  public List<Team> list(@Auth User authUser) {
    Map<String, String> conditions = new HashMap<String, String>();
    conditions.put(Team.OWNER_UUID, authUser.getUUID());
    try {
      return collectionMapper.list(Team.class, conditions);
    } catch (ExecutionException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @POST
  public Team create(@Auth User authUser, @Valid Team team) {
    try {
      if (null == team.getUUID()) {
        team = Team.newTeam(team, authUser);
      }
      return collectionMapper.save(team);
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @PUT
  @Path(UUID_PATH)
  public Team update(@Auth User authUser, @PathParam(UUID) String teamUUID, @Valid Team team) {
    try {
      checkArgument(!Strings.isNullOrEmpty(teamUUID), "Team UUID required");
      checkArgument(teamUUID.equals(team.getUUID()), "Team UUID's do not match");
      checkArgument(!Strings.isNullOrEmpty(team.getOwnerUUID()), "Team Owner UUID required");
      checkArgument(!Strings.isNullOrEmpty(team.getName()), "Team name required");

      return collectionMapper.save(team);
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }
}
