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
public class Player extends WsObject {
  private static final long serialVersionUID = 1L;

  public static final String PLAYERS = "players";
  public static final String TEAM_UUID = "team_uuid";
  public static final String NAME = "name";
  public static final String USER_UUID = "user_uuid";

  public Player() {
    this(null);
  }

  /**
   * @param collection
   * @param uuid
   */
  public Player(String uuid) {
    super(PLAYERS, uuid);
  }

  public String getName() {
    return (String) super.get(NAME);
  }

  /**
   * @param team2
   * @param authUser
   * @return
   */
  public static Player newEvent(Player event) {
    return (Player) WsObject.newObject(event);
  }

  /**
   * @return
   */
  public String getTeamUUID() {
    return (String) get(TEAM_UUID);
  }

  public String getUserUUID() {
    return (String) get(USER_UUID);
  }

}
