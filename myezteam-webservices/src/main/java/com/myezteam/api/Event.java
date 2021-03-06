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
public class Event extends WsObject {
  private static final long serialVersionUID = 1L;

  public static final String EVENTS = "events";
  public static final String TEAM_UUID = "team_uuid";
  public static final String NAME = "name";

  public Event() {
    this(null);
  }

  /**
   * @param collection
   * @param uuid
   */
  public Event(String uuid) {
    super(EVENTS, uuid);
  }

  public String getName() {
    return (String) super.get(NAME);
  }

  /**
   * @param team2
   * @param authUser
   * @return
   */
  public static Event newEvent(Event event) {
    return (Event) WsObject.newObject(event);
  }

  /**
   * @return
   */
  public String getTeamUUID() {
    return (String) get(TEAM_UUID);
  }

}
