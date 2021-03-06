/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.core.test.service;

import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.ExternalLink;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.service.StudyExternalLinkService;
import com.decibeltx.studytracker.core.service.StudyService;
import com.decibeltx.studytracker.core.test.TestConfiguration;
import java.net.URL;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles({"example"})
public class StudyExternalLinkServiceTests {

  @Autowired
  private StudyService studyService;

  @Autowired
  private StudyExternalLinkService externalLinkService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void addExternalLinkTest() throws Exception {
    Study study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertTrue(study.getExternalLinks().isEmpty());

    ExternalLink link = new ExternalLink();
    link.setUrl(new URL("http://google.com"));
    link.setLabel("Google");
    externalLinkService.addStudyExternalLink(study, link);
    Assert.assertNotNull(link.getId());
    String id = link.getId();

    study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertFalse(study.getComments().isEmpty());

    Optional<ExternalLink> optional = externalLinkService.findStudyExternalLinkById(study, id);
    Assert.assertTrue(optional.isPresent());
    link = optional.get();
    Assert.assertEquals("Google", link.getLabel());

  }

  @Test
  public void updateExternalLinkTest() throws Exception {
    addExternalLinkTest();
    Study study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    ExternalLink link = study.getExternalLinks().get(0);
    String id = link.getId();
    String url = link.getUrl().toString();
    link.setUrl(new URL("https://maps.google.com"));
    externalLinkService.updateStudyExternalLink(study, link);
    link = externalLinkService.findStudyExternalLinkById(study, id)
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertNotEquals(url, link.getUrl().toString());
  }

  @Test
  public void deleteExternalLinkTest() throws Exception {
    addExternalLinkTest();
    Study study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    ExternalLink link = study.getExternalLinks().get(0);
    String id = link.getId();
    externalLinkService.deleteStudyExternalLink(study, link.getId());
    Exception exception = null;
    try {
      link = externalLinkService.findStudyExternalLinkById(study, id)
          .orElseThrow(RecordNotFoundException::new);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof RecordNotFoundException);
  }

}
