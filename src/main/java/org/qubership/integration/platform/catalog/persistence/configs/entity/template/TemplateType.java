package org.qubership.integration.platform.catalog.persistence.configs.entity.template;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Environment type")
public enum TemplateType {
    MAPPING,
    SCRIPT
}
