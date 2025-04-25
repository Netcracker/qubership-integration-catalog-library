/*
 * Copyright 2024-2025 NetCracker Technology Corporation
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

package org.qubership.integration.platform.catalog.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.qubership.integration.platform.catalog.model.library.KameletDescriptionDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class KameletStorageService {
    private static final String KAMELETS_PATH = "classpath*:kamelets/*.kamelet.yaml";
    private static final String KAMELET_WITH_NAME_NOT_FOUND_MESSAGE = "Can't find kamelet with name: ";
    private static final String FAILED_TO_PARSE_KAMELET_DEFINITION_MESSAGE = "Failed to parse Kamelet definition from specification: {} {}";
    private static final String LOADED_KAMELET_MESSAGE = "Loaded kamelet: + {} ";
    private static final String ERROR_READING_KAMELET_CATALOG_MESSAGE = "Error while reading kamelet catalog ";
    private static final String KAMELET_LIBRARY_LOADING_COMPLETED_MESSAGE = "Kamelet library loading completed. Loaded {} kamelets";
    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper = new ObjectMapper();

    private final Map<String, org.apache.camel.v1.Kamelet> kameletsDefinitions = new ConcurrentHashMap<>();

    public KameletStorageService(@Qualifier("kameletYamlMapper") ObjectMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
    }

    @PostConstruct
    public void loadKamelets() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(KAMELETS_PATH);
            for (Resource resource : resources) {
                try (InputStream kameletSpecInputStream =  resource.getInputStream()) {
                    org.apache.camel.v1.Kamelet kameletDefinition = yamlMapper.readValue(kameletSpecInputStream, org.apache.camel.v1.Kamelet.class);
                    String kameletName = kameletDefinition.getMetadata().getName();
                    kameletsDefinitions.put(kameletName, kameletDefinition);
                    log.info(LOADED_KAMELET_MESSAGE, kameletName);
                 } catch (JsonParseException e) {
                    log.warn(FAILED_TO_PARSE_KAMELET_DEFINITION_MESSAGE, resource.getFilename(), e.getMessage());
                }
            }
        } catch (IOException e) {
                throw new RuntimeException(ERROR_READING_KAMELET_CATALOG_MESSAGE, e);
        }
        log.info(KAMELET_LIBRARY_LOADING_COMPLETED_MESSAGE, kameletsDefinitions.size());
    }

    public List<KameletDescriptionDTO> getKameletsDefinitions() {
        return kameletsDefinitions
                .values()
                .stream()
                .map(kamelet -> {
                    KameletDescriptionDTO kameletDescriptionDTO = new KameletDescriptionDTO();
                    kameletDescriptionDTO.setName(kamelet.getMetadata().getName());
                    kameletDescriptionDTO.setTitle(kamelet.getSpec().getDefinition().getTitle());
                    kameletDescriptionDTO.setType(kamelet.getMetadata().getLabels().getOrDefault("camel.apache.org/kamelet.type", "action"));
                    return kameletDescriptionDTO;
                }).toList();
    }

    public String getKameletByName(String kameletName) {
        org.apache.camel.v1.Kamelet kamelet = getKamelet(kameletName);
        try {
            return (kamelet != null) ? yamlMapper.writeValueAsString(kamelet) : null;
        } catch (JsonProcessingException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    public String getKameletSpec(String kameletName) {
        org.apache.camel.v1.Kamelet kamelet = getKamelet(kameletName);
        try {
            return jsonMapper.writeValueAsString(kamelet.getSpec().getDefinition());
        } catch (IOException e) {
            //TODO
            throw new RuntimeException("Error serializing Kamelet Definition to JSON", e);
        }
    }

    public String getKameletTemplate(String kameletName) {
        org.apache.camel.v1.Kamelet kamelet = getKamelet(kameletName);
        try {
            return yamlMapper.writeValueAsString(kamelet.getSpec().getTemplate());
        } catch (IOException e) {
            //TODO
            throw new RuntimeException("Error serializing Kamelet Template to JSON", e);
        }
    }

    private org.apache.camel.v1.Kamelet getKamelet(String kameletName) {
        if (kameletsDefinitions.containsKey(kameletName)) {
            return kameletsDefinitions.get(kameletName);
        }
        throw new EntityNotFoundException(KAMELET_WITH_NAME_NOT_FOUND_MESSAGE + kameletName);
    }
}
