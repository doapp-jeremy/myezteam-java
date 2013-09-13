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


public class WsEmailAuth implements Authenticator<String, String> {
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

  private static final LoadingCache<String, Boolean> authTokens = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.DAYS)
      .maximumSize(30)
      .build(new CacheLoader<String, Boolean>() {
        @Override
        public Boolean load(String token) throws Exception {
          return false;
        }
      }
      );

  public static String validateEmail(String email) throws ExecutionException {
    String token = authTokensByEmail.get(email);
    authTokens.put(token, true);
    return token;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.yammer.dropwizard.auth.Authenticator#authenticate(java.lang.Object)
   */
  @Override
  public Optional<String> authenticate(String credentials) throws AuthenticationException {
    // api key used by UI code
    if ("c0f5248a-79e8-4443-a2fd-f2bf26ec683a".equals(credentials)) { return Optional.of(credentials); }

    try {
      // check if we have a valid user token
      if (authTokens.get(credentials)) { return Optional.of(credentials); }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Optional.absent();
  }

}