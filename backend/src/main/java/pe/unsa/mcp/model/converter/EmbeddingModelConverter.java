package pe.unsa.mcp.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import pe.unsa.mcp.model.EntityEmbedding.EmbeddingModel;

@Converter(autoApply = true)
public class EmbeddingModelConverter implements AttributeConverter<EmbeddingModel, String> {

    @Override
    public String convertToDatabaseColumn(EmbeddingModel attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public EmbeddingModel convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        for (EmbeddingModel model : EmbeddingModel.values()) {
            if (model.getValue().equals(dbData)) {
                return model;
            }
        }
        throw new IllegalArgumentException("Unknown database value: " + dbData);
    }
}

