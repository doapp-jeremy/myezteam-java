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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import com.codahale.dropwizard.auth.Auth;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;
import com.myezteam.api.Event;
import com.myezteam.api.User;
import com.myezteam.application.AwsConfiguration;
import com.myezteam.application.CollectionMapper;


/**
 * @author jeremy
 * 
 */
@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {
  static final String UUID = "uuid";
  static final String UUID_PATH = "/{" + UUID + "}";

  private final CollectionMapper collectionMapper;

  public EventResource(AwsConfiguration awsConfiguration) {
    this.collectionMapper = new CollectionMapper(awsConfiguration);
  }

  @Timed
  @GET
  @Path(UUID_PATH)
  public Event get(@Auth User authUser, @PathParam(UUID) String eventUUID) {
    try {
      checkArgument(!Strings.isNullOrEmpty(eventUUID), "Event UUID is empty");
      return collectionMapper.get(new Event(eventUUID));
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @GET
  @Path("/team/{team_uuid}")
  public List<Event> list(@Auth User authUser, @PathParam("team_uuid") String teamUUID) {
    Map<String, String> conditions = new HashMap<String, String>();
    conditions.put(Event.TEAM_UUID, teamUUID);
    try {
      return collectionMapper.list(Event.class, conditions);
    } catch (ExecutionException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @POST
  public Event create(@Auth User authUser, @Valid Event event) {
    try {
      if (null == event.getUUID()) {
        event = Event.newEvent(event);
      }
      return collectionMapper.save(event);
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

}
