/**
 * WsObject.java
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
import java.util.HashMap;
import com.google.common.base.Strings;


/**
 * @author jeremy
 * 
 */
public class WsObject extends HashMap<String, Object> {
  private static final long serialVersionUID = 1L;
  public static final String COLLECTION = "collection";
  public static final String UUID = "uuid";

  private final String tableName;

  protected WsObject(String collection, String uuid, String tableName) {
    super();
    put(COLLECTION, collection);
    put(UUID, uuid);
    this.tableName = tableName;
  }

  protected WsObject(String collection, String uuid) {
    this(collection, uuid, null);
  }

  /**
   * @return the tableName
   */
  public String getTableName() {
    return tableName;
  }

  public String getCollectionKey() {
    return COLLECTION;
  }

  public String getUUIDKey() {
    return UUID;
  }

  /**
   * @return the collection
   */
  public String getCollection() {
    return (String) super.get(COLLECTION);
  }

  /**
   * @return the uuid
   */
  public String getUUID() {
    return (String) super.get(UUID);
  }

  /**
   * 
   */
  private void generateUUID() {
    checkArgument(Strings.isNullOrEmpty(getUUID()), "UUID is already set");
    super.put(UUID, java.util.UUID.randomUUID().toString());
  }

  /**
   * @param key
   * @return
   */
  public boolean isPartOfKey(String key) {
    return COLLECTION.equals(key) || UUID.equals(key);
  }

  public void setKey(String collection, String uuid) {
    super.put(COLLECTION, collection);
    super.put(UUID, uuid);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.AbstractMap#hashCode()
   */
  @Override
  public int hashCode() {
    return 9 + 11 * getCollection().hashCode() + 13 * getUUID().hashCode();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.AbstractMap#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object object) {
    if (object instanceof WsObject) { return hashCode() == object.hashCode(); }
    return false;
  }
}
