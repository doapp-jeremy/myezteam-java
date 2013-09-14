/**
 * CollectionReader.java
 * myezteam-webservices
 * 
 * Created by jeremy on Sep 13, 2013
 * DoApp, Inc. owns and reserves all rights to the intellectual
 * property and design of the following application.
 *
 * Copyright 2013 - All rights reserved.  Created by DoApp, Inc.
 */
package com.myezteam.application;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.myezteam.api.WsObject;


/**
 * @author jeremy
 * 
 */
public class CollectionMapper {
  private final AwsConfiguration awsConfiguration;
  private final AmazonDynamoDBAsync dynamoDB;
  private final String tableName;

  private final LoadingCache<WsObject, WsObject> objectCache = CacheBuilder.newBuilder()
      .maximumSize(100000)
      .build(new CacheLoader<WsObject, WsObject>() {
        @Override
        public WsObject load(WsObject object) throws Exception {
          return loadWsObject(object);
        };
      });

  public CollectionMapper(AwsConfiguration awsConfiguration, String tableName) {
    this.awsConfiguration = awsConfiguration;
    this.dynamoDB = new AmazonDynamoDBAsyncClient(awsConfiguration.getAWSCredentials());
    this.tableName = tableName;
  }

  private <T extends WsObject> String getTableName(T object) {
    String tableName = object.getTableName();
    if (tableName == null) {
      tableName = this.tableName;
    }
    return awsConfiguration.getTablePrefix() + tableName;
  }

  private static Map<String, AttributeValue> getKey(String collectionKey, String collection, String uuidKey, String uuid) {
    Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
    key.put(collectionKey, new AttributeValue(collection));
    key.put(uuidKey, new AttributeValue(uuid));
    return key;
  }

  private static <T extends WsObject> T getObjectFromItem(Class<T> klass, String collectionKey, String uuidKey,
      Map<String, AttributeValue> item) throws InstantiationException, IllegalAccessException {
    AttributeValue collectionValue = checkNotNull(item.get(collectionKey), "Collection not set");
    AttributeValue uuidValue = checkNotNull(item.get(uuidKey), "UUID not set");
    String collection = collectionValue.getS();
    String uuid = uuidValue.getS();
    checkArgument(!Strings.isNullOrEmpty(checkNotNull(collection)));
    checkArgument(!Strings.isNullOrEmpty(checkNotNull(uuid)));

    T instance = klass.newInstance();

    for (Entry<String, AttributeValue> entry : item.entrySet()) {
      instance.put(entry.getKey(), entry.getValue().getS());
    }
    return instance;
  }

  private <T extends WsObject> T load(Class<T> klass, String tableName, String collectionKey, String collection, String uuidKey,
      String uuid) throws InstantiationException, IllegalAccessException, AmazonServiceException, AmazonClientException,
      InterruptedException, ExecutionException {
    GetItemResult result = dynamoDB.getItemAsync(new GetItemRequest().withTableName(tableName).withKey(
        getKey(collectionKey, collection, uuidKey, uuid))).get();
    try {
      return getObjectFromItem(klass, collectionKey, uuidKey, result.getItem());
    } catch (NullPointerException e) {
      return null;
    }
  }

  private <T extends WsObject> T loadWsObject(T object) throws InstantiationException, IllegalAccessException,
      AmazonServiceException, AmazonClientException, InterruptedException, ExecutionException {
    String collectionKey = object.getCollectionKey();
    String collection = object.getCollection();
    String uuidKey = object.getUUIDKey();
    String uuid = object.getUUID();

    checkArgument(!Strings.isNullOrEmpty(collectionKey), "Collection key is empty");
    checkArgument(!Strings.isNullOrEmpty(collection), "Collection is empty");
    checkArgument(!Strings.isNullOrEmpty(uuidKey), "UUID key is empty");
    checkArgument(!Strings.isNullOrEmpty(uuid), "UUID is empty");

    // TODO: perform scan request if uuid is not set
    @SuppressWarnings("unchecked")
    T result = (T) load(object.getClass(), getTableName(object), collectionKey, collection, uuidKey, uuid);
    return result;
  }

  public <T extends WsObject> T get(T object) {
    try {
      @SuppressWarnings("unchecked")
      T result = (T) objectCache.get(object);
      return result;
    } catch (Exception e) {
      return null;
    }
  }

  private Map<String, AttributeValue> getAttributeValues(WsObject wsObject) {
    Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
    for (Entry<String, String> entry : wsObject.entrySet()) {
      item.put(entry.getKey(), new AttributeValue(entry.getValue()));
    }
    return item;
  }

  public <T extends WsObject> T create(T object) throws AmazonServiceException, AmazonClientException, InterruptedException,
      ExecutionException {
    String collection = object.getCollection();
    String uuid = object.getUUID();

    checkArgument(!Strings.isNullOrEmpty(collection), "Collection is empty");
    checkArgument(!Strings.isNullOrEmpty(uuid), "UUID is empty");

    PutItemRequest request = new PutItemRequest().withTableName(getTableName(object));
    request.withItem(getAttributeValues(object));
    dynamoDB.putItemAsync(request).get();
    objectCache.put(object, object);
    return object;
  }

}
