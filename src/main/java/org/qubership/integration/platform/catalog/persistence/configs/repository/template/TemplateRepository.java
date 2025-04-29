package org.qubership.integration.platform.catalog.persistence.configs.repository.template;

import org.qubership.integration.platform.catalog.persistence.configs.entity.template.Template;
import org.qubership.integration.platform.catalog.persistence.configs.entity.template.TemplateType;
import org.qubership.integration.platform.catalog.persistence.configs.repository.common.CommonRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemplateRepository extends CommonRepository<Template>, JpaRepository<Template, String> {
    List<Template> findAllByType(TemplateType type);

    boolean existsByName(String name);

    boolean existsByNameAndIdIsNot(String name, String id);
}
