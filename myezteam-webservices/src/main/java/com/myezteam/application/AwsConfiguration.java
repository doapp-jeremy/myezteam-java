package com.myezteam.application;

import static com.google.common.base.Preconditions.checkNotNull;
import org.hibernate.validator.constraints.NotEmpty;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.fasterxml.jackson.annotation.JsonProperty;


public class AwsConfiguration {
  @NotEmpty
  @JsonProperty
  private String accessKey;

  @NotEmpty
  @JsonProperty
  private String secretKey;

  @NotEmpty
  @JsonProperty
  private String tablePrefix;

  private AWSCredentials awsCredentials;

  public AwsConfiguration() {}

  public String getSecretKey() {
    return secretKey;
  }

  public String getAccessKey() {
    return accessKey;
  }

  /**
   * @return the tablePrefix
   */
  public String getTablePrefix() {
    return tablePrefix;
  }

  public AwsConfiguration withAccessKey(String accessKey) {
    this.accessKey = accessKey;
    return this;
  }

  public AwsConfiguration withSecretKey(String secretKey) {
    this.secretKey = secretKey;
    return this;
  }

  public AWSCredentials getAWSCredentials() {
    if (null == this.awsCredentials) {
      checkNotNull(this.accessKey, "AWS access key is null");
      checkNotNull(this.secretKey, "AWS secret key is null");

      this.awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
    }

    return this.awsCredentials;
  }
}
