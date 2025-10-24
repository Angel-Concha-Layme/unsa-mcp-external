package pe.unsa.mcp.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import pe.unsa.mcp.model.EntityEmbedding.EmbeddingField;

@Converter(autoApply = true)
public class EmbeddingFieldConverter implements AttributeConverter<EmbeddingField, String> {

    @Override
    public String convertToDatabaseColumn(EmbeddingField attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public EmbeddingField convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        for (EmbeddingField field : EmbeddingField.values()) {
            if (field.getValue().equals(dbData)) {
                return field;
            }
        }
        throw new IllegalArgumentException("Unknown database value: " + dbData);
    }
}

