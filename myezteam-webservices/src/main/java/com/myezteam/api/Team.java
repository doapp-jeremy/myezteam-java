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

  public static final String TEAM = "team";
  public static final String NAME = "name";
  public static final String OWNER_UUID = "owner_uuid";

  /**
   * @param collection
   * @param uuid
   */
  public Team(String uuid) {
    super(TEAM, uuid);
  }

  public String getName() {
    return super.get(NAME);
  }

  public String getOwnerUUID() {
    return super.get(OWNER_UUID);
  }

  public User getOwner() {
    return new User(getOwnerUUID());
  }

}