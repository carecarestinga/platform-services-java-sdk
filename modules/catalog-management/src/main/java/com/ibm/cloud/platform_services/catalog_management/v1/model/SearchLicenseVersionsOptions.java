/*
 * (C) Copyright IBM Corp. 2020.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.ibm.cloud.platform_services.catalog_management.v1.model;

import com.ibm.cloud.sdk.core.service.model.GenericModel;

/**
 * The searchLicenseVersions options.
 */
public class SearchLicenseVersionsOptions extends GenericModel {

  protected String q;

  /**
   * Builder.
   */
  public static class Builder {
    private String q;

    private Builder(SearchLicenseVersionsOptions searchLicenseVersionsOptions) {
      this.q = searchLicenseVersionsOptions.q;
    }

    /**
     * Instantiates a new builder.
     */
    public Builder() {
    }

    /**
     * Instantiates a new builder with required properties.
     *
     * @param q the q
     */
    public Builder(String q) {
      this.q = q;
    }

    /**
     * Builds a SearchLicenseVersionsOptions.
     *
     * @return the new SearchLicenseVersionsOptions instance
     */
    public SearchLicenseVersionsOptions build() {
      return new SearchLicenseVersionsOptions(this);
    }

    /**
     * Set the q.
     *
     * @param q the q
     * @return the SearchLicenseVersionsOptions builder
     */
    public Builder q(String q) {
      this.q = q;
      return this;
    }
  }

  protected SearchLicenseVersionsOptions(Builder builder) {
    com.ibm.cloud.sdk.core.util.Validator.notNull(builder.q,
      "q cannot be null");
    q = builder.q;
  }

  /**
   * New builder.
   *
   * @return a SearchLicenseVersionsOptions builder
   */
  public Builder newBuilder() {
    return new Builder(this);
  }

  /**
   * Gets the q.
   *
   * query, for now only "q=entitlement_key:&lt;some-key&gt;" is supported.
   *
   * @return the q
   */
  public String q() {
    return q;
  }
}

