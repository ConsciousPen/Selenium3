package aaa.modules.regression.service.helper.wiremock;

import java.lang.reflect.Field;
import java.util.UUID;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import aaa.helpers.config.CustomTestProperties;
import aaa.modules.regression.service.helper.wiremock.dto.WireMockTemplateData;
import toolkit.config.PropertyProvider;

public class WireMockStub {

	private static final String TEMPLATE_URL_FORMAT = "%s/templates/%s";
	private static final String POST_MOCK_MAPPING_URL_FORMAT = "%s/__admin/mappings";
	private static final String DELETE_MOCK_MAPPING_URL_FORMAT = "%s/__admin/mappings/%s";
	private static final String REPLACEABLE_PROPERTY_FORMAT = "${%s}";
	private static final String REPLACEABLE_NULL_PROPERTY_FORMAT = "\"${%s}\"";
	private static final String ID_PROPERTY = "${id}";

	private static  final String ENVIRONMENT_PATH = PropertyProvider.getProperty(CustomTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE);
	private final String templateName;
	private final String id;
	private final WireMockTemplateData templateData;

	private WireMockStub(String templateName, WireMockTemplateData templateData) {
		this.templateName = templateName;
		this.id = UUID.randomUUID().toString();
		this.templateData = templateData;
	}

	/**
	 * templateName - last-payment-200 - hardcoded value for SuccessCase. If error case will be required, then new value will be added
	 */
	public static WireMockStub create(String templateName, WireMockTemplateData templateData) {
		return new WireMockStub(templateName, templateData);
	}

	public WireMockStub mock() throws IllegalAccessException {
		String template = getTemplate();
		for (Field field : templateData.getClass().getFields()) {
			final String value = (String) field.get(templateData);
			if (StringUtils.isNotEmpty(value)) {
				template = template.replace(String.format(REPLACEABLE_PROPERTY_FORMAT, field.getName()), (String) field.get(templateData));
			} else {
				template = template.replace(String.format(REPLACEABLE_NULL_PROPERTY_FORMAT, field.getName()), "null");
			}
		}
		template = template.replace(ID_PROPERTY, id);
		executePost(template);
		return this;
	}

	public void cleanUp() {
		executeDelete();
	}

	private String getTemplate() {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			WebTarget target = client.target(String.format(TEMPLATE_URL_FORMAT, ENVIRONMENT_PATH, templateName));
			response = target
					.request()
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.get();
			return response.readEntity(String.class);
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	private void executePost(String request) {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			WebTarget target = client.target(String.format(POST_MOCK_MAPPING_URL_FORMAT, ENVIRONMENT_PATH));
			response = target
					.request()
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.post(Entity.json(request));
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	private void executeDelete() {
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
			WebTarget target = client.target(String.format(DELETE_MOCK_MAPPING_URL_FORMAT, ENVIRONMENT_PATH, id));
			response = target
					.request()
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.delete();
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}
}
