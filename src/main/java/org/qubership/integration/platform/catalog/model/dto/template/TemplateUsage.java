package org.qubership.integration.platform.catalog.model.dto.template;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TemplateUsage {
    private String templateId;
    private String chainId;
    private String chainName;
    private String elementId;
    private String elementName;
    private String tabId;
    private String tabName;
}
