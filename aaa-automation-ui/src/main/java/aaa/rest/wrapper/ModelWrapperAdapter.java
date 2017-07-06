package aaa.rest.wrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import aaa.rest.IModel;
import toolkit.exceptions.IstfException;

public class ModelWrapperAdapter implements JsonDeserializer<AbstractModelWrapper> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public AbstractModelWrapper deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        AbstractModelWrapper result;
        Gson gson = new Gson();
        if (!(type instanceof Class))
            throw new IstfException("Invalid type");
		Annotation annotation = ((Class) type).getAnnotation(ModelWrapper.class);
        if (annotation != null) {
            if (!jsonElement.isJsonArray()) {
                if (((JsonObject) jsonElement).get("errorCode") != null) {
                    result = gson.fromJson(jsonElement, type);
                } else {
                    try {
                        result = (AbstractModelWrapper) ((Class) type).newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new IstfException("Unable to deserialize JSON element", e);
                    }
                    result.getModels().add((IModel) gson.fromJson(jsonElement, ((ModelWrapper) annotation).modelClass()));
                }
            } else {
                try {
                    result = (AbstractModelWrapper) ((Class) type).newInstance();
                    for (int i = 0; i < ((JsonArray) jsonElement).size(); i++) {
                        result.getModels().add((IModel) gson.fromJson(((JsonArray) jsonElement).get(i).getAsJsonObject().toString(), ((ModelWrapper) annotation).modelClass()));
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new IstfException("Unable to deserialize JSON element");
                }
            }
        } else {
            throw new IstfException("Deserialized model does not have supported annotation @ModelWrapper");
        }
        return result;
    }
}
