package org.qubership.integration.platform.catalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.qubership.integration.platform.catalog.model.library.KameletDTO;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class KameletStorageService {
    private static final String KAMELETS_PATH = "classpath*:kamelets/*.kamelet.yaml";
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Getter
    private final Map<String, KameletDTO> kamelets = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadKamelets() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(KAMELETS_PATH);
            for (Resource resource : resources) {
                KameletDTO kameletDTO = yamlMapper.readValue(resource.getInputStream(), KameletDTO.class);
                kamelets.put(String.valueOf(kameletDTO.getMetadata().get("name")).replaceAll("^\"|\"$", ""), kameletDTO);
                log.info("Loaded kamelet: " + String.valueOf(kameletDTO.getMetadata().get("name")));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while Kamelets load", e);
        }
    }

    public String getKameletByName(String name) {
        KameletDTO kamelet = kamelets.get(name);
        if (kamelet == null) {
            log.warn("Kamelet '{}' hasn't found", name);
        }
        try {
            return (kamelet != null) ? jsonMapper.writeValueAsString(kamelet) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getKameletSpec(String name) {
        KameletDTO kamelet = kamelets.get(name);
        try {
            return (kamelet != null) ? jsonMapper.writeValueAsString(kamelet.getSpec().get("definition")) : null;
        } catch (IOException e) {
            throw new RuntimeException("Error serializing Kamelet Definition to JSON", e);
        }
    }

    public String getKameletTemplate(String name) {
        KameletDTO kamelet = kamelets.get(name);
        try {
            return (kamelet != null) ? jsonMapper.writeValueAsString(kamelet.getSpec().get("template")) : null;
        } catch (IOException e) {
            throw new RuntimeException("Error serializing Kamelet Template to JSON", e);
        }
    }
}