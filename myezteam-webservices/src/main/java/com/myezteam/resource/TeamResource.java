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
import java.util.ArrayList;
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
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.codahale.dropwizard.auth.Auth;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;
import com.myezteam.api.Event;
import com.myezteam.api.Player;
import com.myezteam.api.Team;
import com.myezteam.api.User;
import com.myezteam.api.WsObject;
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
    this.collectionMapper = CollectionMapper.getInstance(awsConfiguration);
  }

  @Timed
  @GET
  @Path("/list/{uuids: .*}")
  public Map<String, Team> getList(@Auth User authUser, @PathParam("uuids") String uuids) {
    try {
      // checkArgument(uuids.length > 0, "UUID list is empty");
      Map<String, Condition> conditions = new HashMap<String, Condition>();
      conditions.put(Team.COLLECTION,
          new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue(Team.TEAMS)));
      List<AttributeValue> uuidsAttributeValues = new ArrayList<AttributeValue>();
      for (String uuid : uuids.split("/")) {
        uuidsAttributeValues.add(new AttributeValue(uuid));
      }
      conditions.put(Team.UUID,
          new Condition().withComparisonOperator(ComparisonOperator.IN).withAttributeValueList(uuidsAttributeValues));

      Map<String, Team> teamsById = new HashMap<String, Team>();
      List<Team> teams = collectionMapper.listWithConditions(Team.class, conditions);
      for (Team team : teams) {
        teamsById.put(team.getUUID(), team);
      }
      return teamsById;
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
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
    conditions.put(WsObject.COLLECTION, Team.TEAMS);
    conditions.put(Team.OWNER_UUID, authUser.getUUID());
    try {
      return collectionMapper.list(Team.class, conditions);
    } catch (ExecutionException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @GET
  @Path(UUID_PATH + "/events")
  public List<Event> events(@Auth User authUser, @PathParam(UUID) String uuid) {
    Map<String, String> conditions = new HashMap<String, String>();
    conditions.put(Event.TEAM_UUID, uuid);
    try {
      return collectionMapper.list(Event.class, conditions);
    } catch (ExecutionException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @GET
  @Path(UUID_PATH + "/players")
  public List<Player> players(@Auth User authUser, @PathParam(UUID) String uuid) {
    Map<String, String> conditions = new HashMap<String, String>();
    conditions.put(Player.TEAM_UUID, uuid);
    try {
      return collectionMapper.list(Player.class, conditions);
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
