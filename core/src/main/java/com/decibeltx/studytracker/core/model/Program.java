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

package com.decibeltx.studytracker.core.model;

import com.decibeltx.studytracker.core.storage.StorageFolder;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "programs")
@Data
public class Program implements Persistable<String> {

  @Id
  private String id;

  @NotNull(message = "Program code must not be empty")
  private String code;

  @Indexed(unique = true)
  @NotNull(message = "Program name must not be empty")
  private String name;

  private StorageFolder storageFolder;

  private boolean active = true;

  private Map<String, Object> attributes = new LinkedHashMap<>();

  @Override
  public boolean isNew() {
    return id == null;
  }

}
