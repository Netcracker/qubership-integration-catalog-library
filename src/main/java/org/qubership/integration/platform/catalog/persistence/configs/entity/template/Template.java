package org.qubership.integration.platform.catalog.persistence.configs.entity.template;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.qubership.integration.platform.catalog.persistence.configs.entity.AbstractEntity;
import org.qubership.integration.platform.catalog.service.difference.DifferenceMember;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity(name = "templates")
public class Template extends AbstractEntity {
    @Builder.Default
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @DifferenceMember
    private Map<String, Object> properties = new LinkedHashMap<>();

    @Column
    @Enumerated(EnumType.STRING)
    private TemplateType type;
}
