/**
 * Authenticator.java
 * 
 * Created by jeremy, 2013
 * DoApp, Inc. owns and reserves all rights to the intellectual
 * property and design of the following application.
 *
 * Copyright 2013 - All rights reserved.  Created by DoApp, Inc.
 */
package com.myezteam.application;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import com.codahale.dropwizard.auth.AuthenticationException;
import com.codahale.dropwizard.auth.Authenticator;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.myezteam.api.User;


public class WsEmailAuth implements Authenticator<String, User> {
  private final CollectionMapper collectionMapper;

  public WsEmailAuth(AwsConfiguration awsConfiguration, String tableName) {
    collectionMapper = new CollectionMapper(awsConfiguration, tableName);
  }

  private static final LoadingCache<String, String> authTokensByEmail = CacheBuilder.newBuilder()
      .expireAfterAccess(1, TimeUnit.DAYS)
      .maximumSize(30)
      .build(new CacheLoader<String, String>() {
        @Override
        public String load(String email) throws Exception {
          return UUID.randomUUID().toString();
        }
      }
      );

  private static final LoadingCache<String, User> users = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.DAYS)
      .maximumSize(30)
      .build(new CacheLoader<String, User>() {
        @Override
        public User load(String token) throws Exception {
          return null;
        }
      }
      );

  public String validateEmail(String email) throws Exception {
    String token = authTokensByEmail.get(email);
    User user = collectionMapper.get(new User(email));
    if (user == null) {
      // new user, create it
      users.put(token, collectionMapper.save(new User(email)));
    }
    return token;
  }

  public User getUser(String token) {
    try {
      return users.get(token);
    } catch (ExecutionException e) {
      return null;
    }
  }

  public void removeEmail(String email) throws ExecutionException {
    String token = authTokensByEmail.get(email);
    authTokensByEmail.invalidate(email);
    users.invalidate(token);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.yammer.dropwizard.auth.Authenticator#authenticate(java.lang.Object)
   */
  @Override
  public Optional<User> authenticate(String token) throws AuthenticationException {
    try {
      User user = users.get(token);
      if (user != null) { return Optional.of(user); }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return Optional.absent();
  }

}