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

package com.decibeltx.studytracker.idbs.eln;

import com.decibeltx.studytracker.idbs.eln.entities.IdbsNotebookEntry;
import java.util.List;

public interface IdbsElnOperations {

  /**
   * Gets an entity by it's ID.
   *
   * @param entityId
   * @return
   */
  IdbsNotebookEntry findEntityById(String entityId);

  List<IdbsNotebookEntry> findEntityChildren(String entityId);

  /**
   * Creates a new study folder in a program directory.
   *
   * @param studyName
   * @param programEntityId
   * @return
   */
  String createStudyFolder(String studyName, String programEntityId);

}
