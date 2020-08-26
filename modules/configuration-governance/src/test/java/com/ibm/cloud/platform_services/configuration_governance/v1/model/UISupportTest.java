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

package com.ibm.cloud.platform_services.configuration_governance.v1.model;

import com.ibm.cloud.platform_services.configuration_governance.v1.model.UISupport;
import com.ibm.cloud.platform_services.configuration_governance.v1.utils.TestUtilities;
import com.ibm.cloud.sdk.core.service.model.FileWithMetadata;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit test class for the UISupport model.
 */
public class UISupportTest {
  final HashMap<String, InputStream> mockStreamMap = TestUtilities.createMockStreamMap();
  final List<FileWithMetadata> mockListFileWithMetadata = TestUtilities.creatMockListFileWithMetadata();

  @Test
  public void testUISupport() throws Throwable {
    UISupport uiSupportModel = new UISupport.Builder()
      .displayName("testString")
      .description("testString")
      .build();
    assertEquals(uiSupportModel.displayName(), "testString");
    assertEquals(uiSupportModel.description(), "testString");

    String json = TestUtilities.serialize(uiSupportModel);

    UISupport uiSupportModelNew = TestUtilities.deserialize(json, UISupport.class);
    assertTrue(uiSupportModelNew instanceof UISupport);
    assertEquals(uiSupportModelNew.displayName(), "testString");
    assertEquals(uiSupportModelNew.description(), "testString");
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testUISupportError() throws Throwable {
    new UISupport.Builder().build();
  }

}