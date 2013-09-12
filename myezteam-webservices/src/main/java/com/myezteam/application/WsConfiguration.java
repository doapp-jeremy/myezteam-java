/**
 * WsConfiguration.java
 * ws
 * 
 * Created by jeremy on Aug 23, 2013
 * DoApp, Inc. owns and reserves all rights to the intellectual
 * property and design of the following application.
 *
 * Copyright 2013 - All rights reserved.  Created by DoApp, Inc.
 */
package com.myezteam.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.codahale.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author jeremy
 * 
 */
public class WsConfiguration extends Configuration {
  @Valid
  @NotNull
  @JsonProperty
  private final AwsConfiguration aws = new AwsConfiguration();

  /**
   * @return the aws
   */
  public AwsConfiguration getAwsConfiguration() {
    return aws;
  }

}
