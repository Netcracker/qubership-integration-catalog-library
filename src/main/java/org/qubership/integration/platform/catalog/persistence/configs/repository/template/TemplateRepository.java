package org.qubership.integration.platform.catalog.persistence.configs.repository.template;

import org.qubership.integration.platform.catalog.model.dto.template.TemplateUsage;
import org.qubership.integration.platform.catalog.persistence.configs.entity.template.Template;
import org.qubership.integration.platform.catalog.persistence.configs.entity.template.TemplateType;
import org.qubership.integration.platform.catalog.persistence.configs.repository.common.CommonRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface TemplateRepository extends CommonRepository<Template>, JpaRepository<Template, String> {
    List<Template> findAllByType(TemplateType type);

    boolean existsByName(String name);

    boolean existsByNameAndIdIsNot(String name, String id);

    @Query(nativeQuery = true, value = """
            select usages.templateId templateId,
                ch.id chainId,
                ch.name chainName,
                el.id elementId,
                el.name elementName,
                usages.tabId tabId,
                usages.tabName tabName
            from elements el
            join chains ch on el.chain_id = ch.id
            join (select usages."mappingTemplateId" templateId, elements.id el_id, 'handle-response' tabId, 'Handle Response' tabName
                from elements, jsonb_to_recordset(properties -> 'after') as usages(type varchar, "mappingTemplateId" varchar)
                where usages.type = 'mapper-2'
                and elements.type = 'service-call'
                and usages."mappingTemplateId" in :mappingTemplateIds
            union all
                select usages."mappingTemplateId" templateId, elements.id el_id, 'prepare-request' tabId, 'Prepare Request' tabName
                from elements, jsonb_to_record(properties -> 'before') as usages(type varchar, "mappingTemplateId" varchar)
                where usages.type = 'mapper-2'
                and elements.type = 'service-call'
                and usages."mappingTemplateId" in :mappingTemplateIds
            union all
                select usages."mappingTemplateId" templateId, elements.id el_id, 'handle-validation-failure' tabId, 'Handle Validation Failure' tabName
                from elements, jsonb_to_record(properties -> 'handlerContainer') as usages(type varchar, "mappingTemplateId" varchar)
                where elements.type = 'service-call'
                and usages."mappingTemplateId" in :mappingTemplateIds
            union all
                select elements.properties ->> 'mappingTemplateId' templateId, elements.id el_id, null tabId, null tabName
                from elements
                where elements.type = 'mapper-2'
                and elements.properties ->> 'mappingTemplateId' in :mappingTemplateIds
            ) usages
                on el.id = usages.el_id
                order by usages.templateId, ch.name, el.name""")
    List<TemplateUsage> getMappingTemplateUsages(Collection<String> mappingTemplateIds);

    @Query("select t from templates t where t.id in :ids")
    List<Template> findByIds(Collection<String> ids);
}
