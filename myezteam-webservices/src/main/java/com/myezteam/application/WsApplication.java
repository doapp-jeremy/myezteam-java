/**
 * WsApplication.java
 * ws
 * 
 * Created by jeremy on Aug 23, 2013
 * DoApp, Inc. owns and reserves all rights to the intellectual
 * property and design of the following application.
 *
 * Copyright 2013 - All rights reserved.  Created by DoApp, Inc.
 */
package com.myezteam.application;

import java.util.EnumSet;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.ws.rs.ext.ExceptionMapper;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import com.codahale.dropwizard.Application;
import com.codahale.dropwizard.auth.oauth.OAuthProvider;
import com.codahale.dropwizard.jersey.setup.JerseyEnvironment;
import com.codahale.dropwizard.setup.Bootstrap;
import com.codahale.dropwizard.setup.Environment;
import com.google.common.collect.ImmutableSet;
import com.myezteam.api.User;
import com.myezteam.exception.WsExceptionMapper;
import com.myezteam.resource.PersonaAuthResource;
import com.myezteam.resource.UserResource;


/**
 * @author jeremy
 * 
 */
public class WsApplication extends Application<WsConfiguration> {
  private static final String TABLE_NAME = "objects";

  public static void main(String[] args) throws Exception {
    new WsApplication().run(args);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.codahale.dropwizard.Application#initialize(com.codahale.dropwizard.setup.Bootstrap)
   */
  @Override
  public void initialize(Bootstrap<WsConfiguration> bootstrap) {

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.codahale.dropwizard.Application#run(com.codahale.dropwizard.Configuration,
   * com.codahale.dropwizard.setup.Environment)
   */
  @Override
  public void run(WsConfiguration configuration, Environment environment) throws Exception {
    AwsConfiguration awsConfiguration = configuration.getAwsConfiguration();

    WsEmailAuth auth = new WsEmailAuth(awsConfiguration, TABLE_NAME);

    final JerseyEnvironment jerseyEnv = environment.jersey();
    jerseyEnv.register(new OAuthProvider<User>(auth, "Auth"));
    jerseyEnv.register(new PersonaAuthResource(auth));
    jerseyEnv.register(new UserResource(auth));

    // 0.6.2 - Allow CORS:
    // https://groups.google.com/forum/#!msg/dropwizard-user/QYknyWOZmns/6YA8SmHSGu8J
    // and http://wiki.eclipse.org/Jetty/Feature/Cross_Origin_Filter
    // 0.7.0 -
    // https://groups.google.com/forum/#!searchin/dropwizard-user/0.7.0$20crossoriginfilter/dropwizard-user/xl5dc_i8V24/tbE9geZkJTcJ
    Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
    filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    filter.setInitParameter("allowedOrigins", "*");
    filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
    filter.setInitParameter("allowedMethods", "GET,PUT,POST,DELETE,OPTIONS");
    filter.setInitParameter("preflightMaxAge", "5184000"); // 2 months
    filter.setInitParameter("allowCredentials", "true");

    /** START of handling exceptions always as JSON ***/
    Set<Object> dwSingletons = ImmutableSet.copyOf(jerseyEnv.getResourceConfig().getSingletons());
    for (Object s : dwSingletons) {
      if (s instanceof ExceptionMapper) {
        jerseyEnv.getResourceConfig().getSingletons().remove(s);
      }
    }

    // Register the custom ExceptionMapper(s)
    jerseyEnv.register(new WsExceptionMapper());
    /** END of handling exceptions always as JSON ***/

  }
}
