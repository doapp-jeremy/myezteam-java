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
  public static final String FIRST_NAME = "first_name";
  public static final String LAST_NAME = "last_name";
  public static final String USER_UUID = "user_uuid";
  public static final Object PLAYER_TYPE = "player_type";

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

  public String getFirstName() {
    return (String) super.get(FIRST_NAME);
  }

  public String getLastName() {
    return (String) super.get(LAST_NAME);
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

  /**
   * @param player
   * @return
   */
  public static Player newPlayer(Player player) {
    return (Player) WsObject.newObject(player);
  }

  /**
   * @return
   */
  public String getPlayerType() {
    return (String) super.get(PLAYER_TYPE);
  }

  /**
   * @return
   */
  public User getUser() {
    return new User(getUserUUID());
  }

}
