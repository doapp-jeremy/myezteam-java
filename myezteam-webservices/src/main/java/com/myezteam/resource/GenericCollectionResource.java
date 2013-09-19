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
import java.util.Map;
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
import com.myezteam.api.User;
import com.myezteam.api.WsObject;
import com.myezteam.application.AwsConfiguration;
import com.myezteam.application.CollectionMapper;


/**
 * @author jeremy
 * 
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GenericCollectionResource {
  static final String UUID = "uuid";
  static final String UUID_PATH = "/{" + UUID + "}";
  static final String COLLECTION = "collection";
  static final String COLLECTION_PATH = "/{" + COLLECTION + "}";

  private final CollectionMapper collectionMapper;

  public GenericCollectionResource(AwsConfiguration awsConfiguration) {
    this.collectionMapper = CollectionMapper.getInstance(awsConfiguration);
  }

  @Timed
  @GET
  @Path(COLLECTION_PATH + UUID_PATH)
  public WsObject get(@Auth User authUser, @PathParam(COLLECTION) String collection, @PathParam(UUID) String uuid) {
    try {
      checkArgument(!Strings.isNullOrEmpty(uuid), "UUID is empty");
      return collectionMapper.get(new WsObject(collection, uuid));
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @POST
  @Path(COLLECTION_PATH)
  public WsObject create(@Auth User authUser, @PathParam(COLLECTION) String collection, @Valid Map<String, Object> object) {
    try {
      checkArgument(!Strings.isNullOrEmpty(collection), "Collection required");
      WsObject wsObject;
      if (null == object.get(WsObject.UUID)) {
        wsObject = WsObject.newObject(collection, object);
      }
      else {
        wsObject = new WsObject(collection, (String) object.get(WsObject.UUID));
        wsObject.putAll(object);
      }
      return collectionMapper.save(wsObject);
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  @Timed
  @PUT
  @Path(COLLECTION_PATH + UUID_PATH)
  public WsObject update(@Auth User authUser, @PathParam(COLLECTION) String collection, @PathParam(UUID) String uuid,
      @Valid WsObject event) {
    try {
      checkArgument(!Strings.isNullOrEmpty(collection), "Collection required");
      checkArgument(!Strings.isNullOrEmpty(uuid), "UUID required");
      checkArgument(uuid.equals(event.getUUID()), "UUID's do not match");

      return collectionMapper.save(event);
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }
}
