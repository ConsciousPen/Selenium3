package aaa.rest.wrapper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import toolkit.exceptions.IstfException;

@Provider
@Consumes({"application/json", "application/xml"})
public class ModelWrapperMessageBodyReader implements MessageBodyReader<AbstractModelWrapper> {

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass.getAnnotation(ModelWrapper.class) != null;
    }

    @Override
    public AbstractModelWrapper readFrom(Class<AbstractModelWrapper> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap,
            InputStream inputStream) throws IOException, WebApplicationException {
        AbstractModelWrapper result;
        Annotation annotation = aClass.getAnnotation(ModelWrapper.class);
        if (annotation != null) {
            Gson gson = new GsonBuilder().registerTypeAdapter(aClass, new ModelWrapperAdapter()).create();
            result = gson.fromJson(IOUtils.toString(inputStream, Charsets.UTF_8), aClass);
        } else {
            throw new IstfException(String.format("Class [%1$s] does not have supported annotation [@ModelWrapper]", aClass.getName()));
        }
        return result;
    }
}
