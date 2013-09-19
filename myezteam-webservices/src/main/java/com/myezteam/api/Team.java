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
public class Team extends WsObject {
  private static final long serialVersionUID = 1L;

  public static final String TEAMS = "teams";
  public static final String NAME = "name";
  public static final String OWNER_UUID = "owner_uuid";

  public Team() {
    this(null);
  }

  /**
   * @param collection
   * @param uuid
   */
  public Team(String uuid) {
    super(TEAMS, uuid);
  }

  public String getName() {
    return (String) super.get(NAME);
  }

  public String getOwnerUUID() {
    return (String) super.get(OWNER_UUID);
  }

  public User getOwner() {
    return new User(getOwnerUUID());
  }

  /**
   * @param team2
   * @param authUser
   * @return
   */
  public static Team newTeam(Team team, User owner) {
    team = (Team) WsObject.newObject(team);
    team.put(OWNER_UUID, owner.getUUID());
    return team;
  }

}
