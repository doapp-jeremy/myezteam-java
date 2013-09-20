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
import com.myezteam.api.Event;
import com.myezteam.api.Player;
import com.myezteam.api.User;
import com.myezteam.application.AwsConfiguration;
import com.myezteam.application.CollectionMapper;


/**
 * @author jeremy
 * 
 */
@Path("/players")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlayerResource {
  static final String UUID = "uuid";
  static final String UUID_PATH = "/{" + UUID + "}";

  private final CollectionMapper collectionMapper;

  public PlayerResource(AwsConfiguration awsConfiguration) {
    this.collectionMapper = CollectionMapper.getInstance(awsConfiguration);
  }

  @Timed
  @GET
  @Path(UUID_PATH)
  public Player get(@Auth User authUser, @PathParam(UUID) String uuid) {
    try {
      checkArgument(!Strings.isNullOrEmpty(uuid), "UUID is empty");
      return collectionMapper.get(new Player(uuid));
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @POST
  public Player create(@Auth User authUser, @Valid Player player) {
    try {
      checkArgument(!Strings.isNullOrEmpty(player.getPlayerType()), "Player Type required");
      checkArgument(!Strings.isNullOrEmpty(player.getUserUUID()), "User UUID required");
      checkArgument(!Strings.isNullOrEmpty(player.getTeamUUID()), "Team UUID required");
      if (null == player.getUUID()) {
        player = Player.newPlayer(player);
      }

      // // look up user's email
      // User user = collectionMapper.get(player.getUser());
      // if (user == null) {
      // // create the user
      // user = player.getUser();
      // user.setName(player.getFirstName(), player.getLastName());
      // collectionMapper.save(user);
      // }

      return collectionMapper.save(player);
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @PUT
  @Path(UUID_PATH)
  public Event update(@Auth User authUser, @PathParam(UUID) String uuid, @Valid Event event) {
    try {
      checkArgument(!Strings.isNullOrEmpty(uuid), "Event UUID required");
      checkArgument(uuid.equals(event.getUUID()), "Event UUID's do not match");
      checkArgument(!Strings.isNullOrEmpty(event.getName()), "Name required");

      return collectionMapper.save(event);
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }
}
