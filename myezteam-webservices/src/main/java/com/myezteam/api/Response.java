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

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Strings;


/**
 * @author jeremy
 * 
 */
public class Response extends WsObject {
  private static final long serialVersionUID = 1L;

  public static final String RESPONSES = "responses";
  public static final String EVENT_UUID = "event_uuid";
  public static final String USER_UUID = "user_uuid";

  public Response() {
    this(null, null);
  }

  /**
   * @param collection
   * @param uuid
   */
  public Response(String eventUUID, String userUUID) {
    super(RESPONSES, eventUUID + "-" + userUUID);
    put(EVENT_UUID, eventUUID);
    put(USER_UUID, userUUID);
  }

  /**
   * @return
   */
  public String getEventUUID() {
    return (String) get(EVENT_UUID);
  }

  public String getUserUUID() {
    return (String) get(USER_UUID);
  }

  /**
   * @param uuid
   * @param response
   * @return
   */
  public static Response newResponse(String eventUUID, Response response) {
    checkArgument(!Strings.isNullOrEmpty(response.getUserUUID()), "User UUID must be set");
    Response rsvp = new Response(eventUUID, response.getUserUUID());
    for (java.util.Map.Entry<String, Object> entry : response.entrySet()) {
      Object value = entry.getValue();
      if (value != null) {
        rsvp.put(entry.getKey(), value);
      }
    }
    return rsvp;
  }

}
