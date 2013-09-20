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
public class Email extends WsObject {
  private static final long serialVersionUID = 1L;

  public static final String EMAILS = "emails";
  public static final String EVENT_UUID = "event_uuid";

  public Email() {
    this(null);
  }

  /**
   * @param collection
   * @param uuid
   */
  public Email(String uuid) {
    super(EMAILS, uuid);
  }

  /**
   * @param team2
   * @param authUser
   * @return
   */
  public static Email newEvent(Email event) {
    return (Email) WsObject.newObject(event);
  }

  /**
   * @return
   */
  public String getEventUUID() {
    return (String) get(EVENT_UUID);
  }

}
