package org.qubership.integration.platform.catalog.service;

import lombok.RequiredArgsConstructor;
import org.qubership.integration.platform.catalog.persistence.configs.entity.actionlog.ActionLog;
import org.qubership.integration.platform.catalog.persistence.configs.entity.actionlog.EntityType;
import org.qubership.integration.platform.catalog.persistence.configs.entity.actionlog.LogOperation;
import org.qubership.integration.platform.catalog.persistence.configs.entity.template.Template;
import org.qubership.integration.platform.catalog.persistence.configs.repository.template.TemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TemplateBaseService {
    protected final TemplateRepository templateRepository;
    protected final ActionsLogService actionsLogger;

    @Transactional
    public Template create(Template template) {
        return create(template, false);
    }

    @Transactional
    public Template create(Template template, boolean isImport) {
        Template savedTemplate = templateRepository.save(template);
        logAction(savedTemplate, isImport ? LogOperation.CREATE_OR_UPDATE : LogOperation.CREATE);
        return savedTemplate;
    }

    @Transactional
    public Template update(Template template) {
        return update(template, true);
    }

    @Transactional
    public Template update(Template template, boolean logAction) {
        Template updatedTemplate = templateRepository.save(template);
        if (logAction) {
            logAction(updatedTemplate, LogOperation.UPDATE);
        }
        return updatedTemplate;
    }

    @Transactional
    public void delete(Template template) {
        templateRepository.delete(template);
        logAction(template, LogOperation.DELETE);
    }

    protected void logAction(Template template, LogOperation operation) {
        actionsLogger.logAction(ActionLog.builder()
                .entityType(EntityType.getTemplateType(template))
                .entityId(template.getId())
                .entityName(template.getName())
                .operation(operation)
                .build());
    }
}
