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

package com.ibm.cloud.platform_services.configuration_governance.v1;

import com.ibm.cloud.platform_services.configuration_governance.v1.model.Attachment;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.AttachmentList;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.AttachmentRequest;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.CreateAttachmentsOptions;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.CreateAttachmentsResponse;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.CreateRuleRequest;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.CreateRulesOptions;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.CreateRulesResponse;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.DeleteAttachmentOptions;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.DeleteRuleOptions;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.EnforcementAction;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.GetAttachmentOptions;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.GetRuleOptions;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.ListAttachmentsOptions;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.ListRulesOptions;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.Rule;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.RuleConditionSingleProperty;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.RuleList;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.RuleRequest;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.RuleRequiredConfigMultiplePropertiesConditionAnd;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.RuleRequiredConfigSingleProperty;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.RuleScope;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.TargetResource;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.UpdateAttachmentOptions;
import com.ibm.cloud.platform_services.configuration_governance.v1.model.UpdateRuleOptions;
import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.service.exception.ServiceResponseException;
import com.ibm.cloud.sdk.core.util.CredentialUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

public class ConfigurationGovernanceExamples {
  private static final Logger logger = LoggerFactory.getLogger(ConfigurationGovernanceExamples.class);
  protected ConfigurationGovernanceExamples() { }

  static boolean verbose = true;

  @SuppressWarnings("checkstyle:methodlength")
  public static void main(String[] args) throws Exception {
    ConfigurationGovernance service = ConfigurationGovernance.newInstance();

    // Load up our test-specific config properties.
    Map<String, String> config = CredentialUtils.getServiceProperties(ConfigurationGovernance.DEFAULT_SERVICE_NAME);

    final String testLabel = "JavaSDKExamples";
    final String accountId = config.get("ACCOUNT_ID");
    final String serviceName = config.get("TEST_SERVICE_NAME");
    final String enterpriseScopeId = config.get("ENTERPRISE_SCOPE_ID");
    final String subacctScopeId = config.get("SUBACCT_SCOPE_ID");

    // Globlal variables to hold link values
    String attachmentId = null;
    String attachmentEtag = null;
    String ruleId = null;
    String ruleEtag = null;

    cleanRules(service, accountId, testLabel);

    try {
      // begin-create_rules
      TargetResource targetResourceModel = new TargetResource.Builder()
        .serviceName(serviceName)
        .resourceKind("bucket") //("service")
        .build();
      RuleConditionSingleProperty ruleConditionModel = new RuleConditionSingleProperty.Builder()
        .property("allowed_gb")
        .operator(RuleConditionSingleProperty.Operator.NUM_LESS_THAN_EQUALS)
        .value("20")
        .build();
      RuleRequiredConfigMultiplePropertiesConditionAnd ruleRequiredConfigModel = new RuleRequiredConfigMultiplePropertiesConditionAnd.Builder()
        .description("Bucket check")
        .and(Arrays.asList(ruleConditionModel))
        .build();
      RuleRequest ruleRequestModel = new RuleRequest.Builder()
        .accountId(accountId)
        .name("Bucket size rule")
        .description("Ensure that buckets dont grow too large.")
        .target(targetResourceModel)
        .requiredConfig(ruleRequiredConfigModel)
        .addEnforcementAction(new EnforcementAction.Builder().action("disallow").build())
        .addEnforcementAction(new EnforcementAction.Builder().action("audit_log").build())
        .labels(Arrays.asList(testLabel))
        .build();
      CreateRuleRequest createRuleRequestModel = new CreateRuleRequest.Builder()
        .requestId("3cebc877-58e7-44a5-a292-32114fa73558")
        .rule(ruleRequestModel)
        .build();
      CreateRulesOptions createRulesOptions = new CreateRulesOptions.Builder()
        .rules(Arrays.asList(createRuleRequestModel))
        .build();

      Response<CreateRulesResponse> response = service.createRules(createRulesOptions).execute();
      CreateRulesResponse createRulesResponse = response.getResult();

      System.out.println(createRulesResponse);
      // end-create_rules

      ruleId = createRulesResponse.getRules().get(0).getRule().getRuleId();
    } catch (ServiceResponseException e) {
        logger.error(String.format("Service returned status code %s: %s\nError details: %s",
          e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
    }

    try {
      // begin-create_attachments
      RuleScope enterpriseScope = new RuleScope.Builder()
              .note("enterprise")
              .scopeId(enterpriseScopeId)
              .scopeType("enterprise")
              .build();
      RuleScope ruleScopeModel = new RuleScope.Builder()
        .note("leaf account")
        .scopeId(subacctScopeId)
        .scopeType("enterprise.account")
        .build();
      AttachmentRequest attachmentRequestModel = new AttachmentRequest.Builder()
        .accountId(accountId)
        .includedScope(enterpriseScope)
        .excludedScopes(Arrays.asList(ruleScopeModel))
        .build();
      CreateAttachmentsOptions createAttachmentsOptions = new CreateAttachmentsOptions.Builder()
        .ruleId(ruleId)
        .attachments(Arrays.asList(attachmentRequestModel))
        .build();

      Response<CreateAttachmentsResponse> response = service.createAttachments(createAttachmentsOptions).execute();
      CreateAttachmentsResponse createAttachmentsResponse = response.getResult();

      System.out.println(createAttachmentsResponse);
      // end-create_attachments

      attachmentId = createAttachmentsResponse.getAttachments().get(0).getAttachmentId();
    } catch (ServiceResponseException e) {
        logger.error(String.format("Service returned status code %s: %s\nError details: %s",
          e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
    }

    try {
      // begin-list_rules
      ListRulesOptions listRulesOptions = new ListRulesOptions.Builder()
        .accountId(accountId)
        .build();

      Response<RuleList> response = service.listRules(listRulesOptions).execute();
      RuleList ruleList = response.getResult();

      System.out.println(ruleList);
      // end-list_rules
    } catch (ServiceResponseException e) {
        logger.error(String.format("Service returned status code %s: %s\nError details: %s",
          e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
    }

    try {
      // begin-get_rule
      GetRuleOptions getRuleOptions = new GetRuleOptions.Builder()
        .ruleId(ruleId)
        .build();

      Response<Rule> response = service.getRule(getRuleOptions).execute();
      Rule rule = response.getResult();

      System.out.println(rule);
      // end-get_rule

      ruleEtag = response.getHeaders().values("Etag").get(0);
    } catch (ServiceResponseException e) {
        logger.error(String.format("Service returned status code %s: %s\nError details: %s",
          e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
    }

    try {
      // begin-update_rule
      TargetResource targetResourceModel = new TargetResource.Builder()
        .serviceName(serviceName)
        .resourceKind("bucket")
        .build();
      RuleRequiredConfigSingleProperty ruleRequiredConfigModel = new RuleRequiredConfigSingleProperty.Builder()
        .property("location")
        .operator(RuleConditionSingleProperty.Operator.IS_NOT_EMPTY)
        .build();
       UpdateRuleOptions updateRuleOptions = new UpdateRuleOptions.Builder()
        .ruleId(ruleId)
        .ifMatch(ruleEtag)
        .name("More bucket rules")
        .description("Ensure that location is not empty.")
        .target(targetResourceModel)
        .requiredConfig(ruleRequiredConfigModel)
        .addEnforcementAction(new EnforcementAction.Builder().action("audit_log").build())
        .accountId(accountId)
        .version("1.0.1")
        .labels(Arrays.asList(testLabel))
        .build();

      Response<Rule> response = service.updateRule(updateRuleOptions).execute();
      Rule rule = response.getResult();

      System.out.println(rule);
      // end-update_rule
    } catch (ServiceResponseException e) {
        logger.error(String.format("Service returned status code %s: %s\nError details: %s",
          e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
    }

    try {
      // begin-list_attachments
      ListAttachmentsOptions listAttachmentsOptions = new ListAttachmentsOptions.Builder()
        .ruleId(ruleId)
        .build();

      Response<AttachmentList> response = service.listAttachments(listAttachmentsOptions).execute();
      AttachmentList attachmentList = response.getResult();

      System.out.println(attachmentList);
      // end-list_attachments
    } catch (ServiceResponseException e) {
        logger.error(String.format("Service returned status code %s: %s\nError details: %s",
          e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
    }

    try {
      // begin-get_attachment
      GetAttachmentOptions getAttachmentOptions = new GetAttachmentOptions.Builder()
        .ruleId(ruleId)
        .attachmentId(attachmentId)
        .build();

      Response<Attachment> response = service.getAttachment(getAttachmentOptions).execute();
      Attachment attachment = response.getResult();

      System.out.println(attachment);
      // end-get_attachment

      attachmentEtag = response.getHeaders().values("Etag").get(0);
    } catch (ServiceResponseException e) {
        logger.error(String.format("Service returned status code %s: %s\nError details: %s",
          e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
    }

    try {
      // begin-update_attachment
      RuleScope includedScope = new RuleScope.Builder()
              .note("enterprise - updated")
              .scopeId(enterpriseScopeId)
              .scopeType("enterprise")
              .build();
      RuleScope excludedScope = new RuleScope.Builder()
        .scopeId(subacctScopeId)
        .scopeType("enterprise.account")
        .note("leaf account")
        .build();
       UpdateAttachmentOptions updateAttachmentOptions = new UpdateAttachmentOptions.Builder()
        .ruleId(ruleId)
        .attachmentId(attachmentId)
        .ifMatch(attachmentEtag)
        .accountId(accountId)
        .includedScope(includedScope)
        .excludedScopes(Arrays.asList(excludedScope))
        .build();

      Response<Attachment> response = service.updateAttachment(updateAttachmentOptions).execute();
      Attachment attachment = response.getResult();

      System.out.println(attachment);
      // end-update_attachment
    } catch (ServiceResponseException e) {
        logger.error(String.format("Service returned status code %s: %s\nError details: %s",
          e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
    }

    try {
      // begin-delete_attachment
      DeleteAttachmentOptions deleteAttachmentOptions = new DeleteAttachmentOptions.Builder()
        .ruleId(ruleId)
        .attachmentId(attachmentId)
        .build();

      service.deleteAttachment(deleteAttachmentOptions).execute();
      // end-delete_attachment
    } catch (ServiceResponseException e) {
        logger.error(String.format("Service returned status code %s: %s\nError details: %s",
          e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
    }

    try {
      // begin-delete_rule
      DeleteRuleOptions deleteRuleOptions = new DeleteRuleOptions.Builder()
        .ruleId(ruleId)
        .build();

      service.deleteRule(deleteRuleOptions).execute();
      // end-delete_rule
    } catch (ServiceResponseException e) {
        logger.error(String.format("Service returned status code %s: %s\nError details: %s",
          e.getStatusCode(), e.getMessage(), e.getDebuggingInfo()), e);
    }

    log("Configuration Governance example tests complete");
    System.exit(0);  // This is here because the test seems to hang on cleanup
  }

  static void cleanRules(ConfigurationGovernance service, String accountId, String label) {
    try {
      log("Cleaning rules...");

      // List any existing rules for this account with the specified label.
      ListRulesOptions listRulesOptions = new ListRulesOptions.Builder()
              .accountId(accountId)
              .labels(label)
              .offset(0)
              .limit(1000)
              .build();
      Response<RuleList> response = service.listRules(listRulesOptions).execute();

      RuleList ruleListResult = response.getResult();

      log(String.format("Found %d rule(s) to be cleaned", ruleListResult.getTotalCount()));

      // Now walk through the returned rules and delete each one.
      if (ruleListResult.getTotalCount() > 0) {
        for (Rule rule : ruleListResult.getRules()) {
          DeleteRuleOptions deleteRuleOptions = new DeleteRuleOptions.Builder()
                  .ruleId(rule.getRuleId())
                  .build();

          log(String.format("Deleting rule: name='%s' id='%s'", rule.getName(), rule.getRuleId()));

          Response<Void> deleteResponse = service.deleteRule(deleteRuleOptions).execute();
        }
      }
      log("Finished cleaning rules...");
    } catch (ServiceResponseException e) {
      throw e;
    }
  }

  static void log(String msg) {
    if (verbose) {
      System.out.println(msg);
    }
  }
}
