package org.qubership.integration.platform.catalog.model.library;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "Kamelet definition representing an integration template in Apache Camel.")
public class KameletDTO {
    @Schema(description = "API version of the Kamelet", example = "camel.apache.org/v1alpha1")
    private String apiVersion;

    @Schema(description = "Kind of the resource", example = "Kamelet")
    private String kind;

    @Schema(description = "Metadata containing the name and labels of the Kamelet")
    private JsonNode metadata;

    @Schema(description = "Labels of the Kamelet")
    private JsonNode labels;

    @Schema(description = "Specification of the Kamelet")
    private JsonNode spec;
}