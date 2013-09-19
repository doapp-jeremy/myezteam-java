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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
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
  private static class ListRequest<T extends WsObject> {
    private final Class<T> klass;
    private final Map<String, Condition> scanFilter;

    public ListRequest(Class<T> klass, Map<String, Condition> scanFilter) {
      this.klass = klass;
      this.scanFilter = scanFilter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      return 17 + klass.hashCode() * 19 + scanFilter.hashCode() * 31;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
      if (object instanceof ListRequest) { return hashCode() == object.hashCode(); }
      return super.equals(object);
    }
  }

  private final AwsConfiguration awsConfiguration;
  private final AmazonDynamoDBAsync dynamoDB;
  private final String tableName;

  private final LoadingCache<WsObject, WsObject> objectCache = CacheBuilder.newBuilder()
      .maximumSize(10000)
      .build(new CacheLoader<WsObject, WsObject>() {
        @Override
        public WsObject load(WsObject object) throws Exception {
          return loadWsObject(object);
        };
      });

  @SuppressWarnings("rawtypes")
  private final LoadingCache<ListRequest, List<WsObject>> listCache = CacheBuilder.newBuilder()
      .maximumSize(1000)
      .build(new CacheLoader<ListRequest, List<WsObject>>() {
        @SuppressWarnings("unchecked")
        @Override
        public List<WsObject> load(ListRequest listRequest) throws Exception {
          return query(listRequest.klass, listRequest.scanFilter);
        };
      });

  public CollectionMapper(AwsConfiguration awsConfiguration, String tableName) {
    this.awsConfiguration = awsConfiguration;
    this.dynamoDB = new AmazonDynamoDBAsyncClient(awsConfiguration.getAWSCredentials());
    this.tableName = tableName;
  }

  private CollectionMapper(AwsConfiguration awsConfiguration) {
    this(awsConfiguration, "objects");
  }

  private static CollectionMapper instance;

  public static CollectionMapper getInstance(AwsConfiguration awsConfiguration) {
    if (instance == null) {
      instance = new CollectionMapper(awsConfiguration);
    }
    return instance;
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
    for (Entry<String, Object> entry : wsObject.entrySet()) {
      Object value = entry.getValue();
      if (value instanceof String) {
        item.put(entry.getKey(), new AttributeValue((String) value));
      }
      else {
        throw new RuntimeException("Need to get string value of object: " + value.getClass().getName());
      }
    }
    return item;
  }

  private <T extends WsObject> T getObjectFromItem(Class<T> klass, Map<String, AttributeValue> item)
      throws InstantiationException, IllegalAccessException {
    checkArgument(!Strings.isNullOrEmpty(checkNotNull(item.get(WsObject.COLLECTION).getS())));
    checkArgument(!Strings.isNullOrEmpty(checkNotNull(item.get(WsObject.UUID).getS())));
    T wsObject = klass.newInstance();
    for (Entry<String, AttributeValue> entry : item.entrySet()) {
      wsObject.put(entry.getKey(), entry.getValue().getS());
    }
    return wsObject;
  }

  /**
   * @param items
   * @return
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  private <T extends WsObject> List<T> getObjectFromItems(Class<T> klass, List<Map<String, AttributeValue>> items)
      throws InstantiationException, IllegalAccessException {
    List<T> objects = new ArrayList<T>();
    for (Map<String, AttributeValue> item : items) {
      objects.add(getObjectFromItem(klass, item));
    }
    return objects;
  }

  public <T extends WsObject> T save(T object) throws AmazonServiceException, AmazonClientException, InterruptedException,
      ExecutionException {
    String collection = object.getCollection();
    String uuid = object.getUUID();

    checkArgument(!Strings.isNullOrEmpty(collection), "Collection is empty");
    checkArgument(!Strings.isNullOrEmpty(uuid), "UUID is empty");

    PutItemRequest request = new PutItemRequest().withTableName(getTableName(object));
    request.withItem(getAttributeValues(object));
    dynamoDB.putItem(request);
    objectCache.put(object, object);
    // need to figure out how to only clear necessary objects
    listCache.invalidateAll();
    return object;
  }

  private <T extends WsObject> List<T> query(Class<T> klass, Map<String, Condition> scanFilter)
      throws InstantiationException,
      IllegalAccessException {
    T object = klass.newInstance();
    Map<String, Condition> keyConditions = new HashMap<String, Condition>();
    keyConditions.put(
        WsObject.COLLECTION,
        new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(
            new AttributeValue(object.getCollection())));
    Map<String, AttributeValue> exclusiveStartKey = null;
    List<T> items = new ArrayList<T>();
    do {
      QueryResult result = dynamoDB.query(new QueryRequest(getTableName(object)).withKeyConditions(keyConditions)
          .withExclusiveStartKey(exclusiveStartKey));
      exclusiveStartKey = result.getLastEvaluatedKey();
      items.addAll(getObjectFromItems(klass, result.getItems()));
    } while (exclusiveStartKey != null);

    return items;
  }

  @SuppressWarnings("unchecked")
  public <T extends WsObject> List<T> listWithConditions(Class<T> klass, Map<String, Condition> conditions)
      throws InstantiationException,
      IllegalAccessException, ExecutionException {
    return (List<T>) listCache.get(new ListRequest<T>(klass, conditions));
  }

  /**
   * @param class1
   * @return
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws ExecutionException
   */
  public <T extends WsObject> List<T> list(Class<T> klass, Map<String, String> conditions) throws InstantiationException,
      IllegalAccessException, ExecutionException {
    Map<String, Condition> filter = new HashMap<String, Condition>();
    for (Entry<String, String> condition : conditions.entrySet()) {
      filter.put(
          condition.getKey(),
          new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(
              new AttributeValue(condition.getValue())));
    }
    return listWithConditions(klass, filter);
  }

}
