/**
 * User.java
 * myezteam-webservices
 * 
 * Created by jeremy on Sep 13, 2013
 * DoApp, Inc. owns and reserves all rights to the intellectual
 * property and design of the following application.
 *
 * Copyright 2013 - All rights reserved.  Created by DoApp, Inc.
 */
package com.myezteam.api;

/**
 * @author jeremy
 * 
 */
public class Response extends WsObject {
  private static final long serialVersionUID = 1L;

  public static final String RESPONSES = "responses";
  public static final String EVENT_UUID = "event_uuid";

  public Response() {
    this(null);
  }

  /**
   * @param collection
   * @param uuid
   */
  public Response(String uuid) {
    super(RESPONSES, uuid);
  }

  /**
   * @param team2
   * @param authUser
   * @return
   */
  public static Response newEvent(Response event) {
    return (Response) WsObject.newObject(event);
  }

  /**
   * @return
   */
  public String getEventUUID() {
    return (String) get(EVENT_UUID);
  }

}
