package org.qubership.integration.platform.catalog.model.library;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Kamelet definition representing an integration template in Apache Camel.")
public class KameletDescriptionDTO {
    @Schema(description = "Kamelet name", example = "http-sender-sink")
    private String name;
    @Schema(description = "Kamelet title", example = "Http Sender")
    private String title;
    //TODO Try to add oneOf property
    @Schema(description = "Kamelet type", example = "sink")
    private String type;

}
