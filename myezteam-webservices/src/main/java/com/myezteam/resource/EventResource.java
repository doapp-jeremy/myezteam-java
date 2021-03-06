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
import com.myezteam.api.Email;
import com.myezteam.api.Event;
import com.myezteam.api.Response;
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
    this.collectionMapper = CollectionMapper.getInstance(awsConfiguration);
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
  @POST
  public Event create(@Auth User authUser, @Valid Event event) {
    try {
      checkArgument(!Strings.isNullOrEmpty(event.getName()), "Name required");
      checkArgument(!Strings.isNullOrEmpty(event.getTeamUUID()), "Team UUID required");
      if (null == event.getUUID()) {
        event = Event.newEvent(event);
      }
      return collectionMapper.save(event);
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

  @Timed
  @GET
  @Path(UUID_PATH + "/responses")
  public List<Response> events(@Auth User authUser, @PathParam(UUID) String uuid) {
    Map<String, String> conditions = new HashMap<String, String>();
    conditions.put(Response.EVENT_UUID, uuid);
    try {
      return collectionMapper.list(Response.class, conditions);
    } catch (ExecutionException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @GET
  @Path(UUID_PATH + "/emails")
  public List<Email> emails(@Auth User authUser, @PathParam(UUID) String uuid) {
    Map<String, String> conditions = new HashMap<String, String>();
    conditions.put(Response.EVENT_UUID, uuid);
    try {
      return collectionMapper.list(Email.class, conditions);
    } catch (ExecutionException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @GET
  @Path(UUID_PATH + "/responses/me")
  public Response response(@Auth User authUser, @PathParam(UUID) String uuid) {
    try {
      return collectionMapper.get(new Response(uuid, authUser.getUUID()));
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @POST
  @Path(UUID_PATH + "/responses")
  public Response rsvp(@Auth User authUser, @PathParam(UUID) String uuid, @Valid Response response) {
    try {
      return collectionMapper.save(Response.newResponse(uuid, response));
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }
}
