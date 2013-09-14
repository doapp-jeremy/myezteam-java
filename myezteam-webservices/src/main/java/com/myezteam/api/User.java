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
public class User extends WsObject {
  private static final long serialVersionUID = 1L;

  public static final String USER = "user";
  public static final String EMAIL = WsObject.UUID;

  /**
   * @param collection
   */
  public User(String email) {
    super(USER, email);
  }

  public String getEmail() {
    return super.getUUID();
  }

}
