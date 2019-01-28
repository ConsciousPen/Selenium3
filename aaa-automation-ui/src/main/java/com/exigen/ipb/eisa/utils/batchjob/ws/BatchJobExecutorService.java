/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.exigen.ipb.eisa.utils.batchjob.ws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.*;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.eisa.base.app.CSAAApplicationFactory;
import toolkit.config.ClassConfigurator;
import toolkit.config.PropertyProvider;

@WebServiceClient(name = "BatchJobExecutorService", targetNamespace = "http://batchjob.integration.eis.exigen.com/", wsdlLocation = "")
public class BatchJobExecutorService extends Service {
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(BatchJobExecutorService.class);
	private static final URL BATCH_JOB_EXECUTOR_SERVICE_WSDL_LOCATION;
	private static final WebServiceException BATCH_JOB_EXECUTOR_SERVICE_EXCEPTION;
	private static final QName BATCH_JOB_EXECUTOR_SERVICE_QNAME = new QName("http://batchjob.integration.eis.exigen.com/", "BatchJobExecutorService");
	@ClassConfigurator.Configurable(byClassName = true)
	private static HTTPConduitConfigurer configurer = new ETCSAHTTPConduitConfigurer();

	static {
		ClassConfigurator configurator = new ClassConfigurator(BatchJobExecutorService.class);
		configurator.applyConfiguration();

		URL url = null;
		WebServiceException e = null;
		try {
			url = getURL();
			Bus bus = BusFactory.getThreadDefaultBus();

			bus.setExtension(configurer, HTTPConduitConfigurer.class);
			bus.getInInterceptors().add(getLogInInterceptor());
			bus.getOutInterceptors().add(getLoggingOutInterceptor());
		} catch (MalformedURLException ex) {
			e = new WebServiceException(ex);
		}
		BATCH_JOB_EXECUTOR_SERVICE_WSDL_LOCATION = url;
		BATCH_JOB_EXECUTOR_SERVICE_EXCEPTION = e;
	}

	public BatchJobExecutorService() {
		super(__getWsdlLocation(), BATCH_JOB_EXECUTOR_SERVICE_QNAME);
	}

	public BatchJobExecutorService(WebServiceFeature... features) {
		super(__getWsdlLocation(), BATCH_JOB_EXECUTOR_SERVICE_QNAME, features);
	}

	public BatchJobExecutorService(URL wsdlLocation) {
		super(wsdlLocation, BATCH_JOB_EXECUTOR_SERVICE_QNAME);
	}

	public BatchJobExecutorService(URL wsdlLocation, WebServiceFeature... features) {
		super(wsdlLocation, BATCH_JOB_EXECUTOR_SERVICE_QNAME, features);
	}

	public BatchJobExecutorService(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public BatchJobExecutorService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
		super(wsdlLocation, serviceName, features);
	}

	/**
	 * @return returns BatchJobTrigger
	 */
	@WebEndpoint(name = "BatchJobExecutorPort")
	public BatchJobTrigger getBatchJobExecutorPort() {
		return getPort(new QName("http://batchjob.integration.eis.exigen.com/", "BatchJobExecutorPort"), BatchJobTrigger.class);
	}

	private static URL getURL() throws MalformedURLException {
		String urlString = CSAAApplicationFactory.get().adminApp().getUrl().replace("/admin", "").concat("/services/BatchJobTrigger?wsdl");
		return new URL(PropertyProvider.getProperty("soap.batchjob.endpoint", urlString));
	}

	private static LoggingInInterceptor getLogInInterceptor() {
		LoggingInInterceptor inInterceptor = new LoggingInInterceptor() {
			@Override
			protected void log(Logger logger, String message) {
				message = transform(message);
				if (writer != null) {
					writer.println(message);
					// Flushing the writer to make sure the message is written
					writer.flush();
				} else if (logger.isLoggable(Level.INFO)) {
					LogRecord lr = new LogRecord(Level.INFO, message);
					lr.setSourceClassName(logger.getName());
					lr.setSourceMethodName(null);
					lr.setLoggerName(logger.getName());
					LOG.info(lr.getMessage());
				}
			}
		};
		inInterceptor.setPrettyLogging(true);
		return inInterceptor;
	}

	private static LoggingOutInterceptor getLoggingOutInterceptor() {
		LoggingOutInterceptor outInterceptor = new LoggingOutInterceptor() {
			@Override
			protected void log(Logger logger, String message) {
				message = transform(message);
				if (writer != null) {
					writer.println(message);
					// Flushing the writer to make sure the message is written
					writer.flush();
				} else if (logger.isLoggable(Level.INFO)) {
					LogRecord lr = new LogRecord(Level.INFO, message);
					lr.setSourceClassName(logger.getName());
					lr.setSourceMethodName(null);
					lr.setLoggerName(logger.getName());
					LOG.info(lr.getMessage());
				}
			}
		};
		outInterceptor.setPrettyLogging(true);
		return outInterceptor;
	}

	private static URL __getWsdlLocation() {
		if (BATCH_JOB_EXECUTOR_SERVICE_EXCEPTION != null) {
			throw BATCH_JOB_EXECUTOR_SERVICE_EXCEPTION;
		}
		return BATCH_JOB_EXECUTOR_SERVICE_WSDL_LOCATION;
	}

	/**
	 * @param features A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
	 * @return returns BatchJobTrigger
	 */
	@WebEndpoint(name = "BatchJobExecutorPort")
	public BatchJobTrigger getBatchJobExecutorPort(WebServiceFeature... features) {
		return getPort(new QName("http://batchjob.integration.eis.exigen.com/", "BatchJobExecutorPort"), BatchJobTrigger.class, features);
	}

	private static class ETCSAHTTPConduitConfigurer implements HTTPConduitConfigurer {

		@Override
		public void configure(String name, String address, HTTPConduit c) {
			AuthorizationPolicy ap = new AuthorizationPolicy();
			ap.setUserName(PropertyProvider.getProperty("app.user"));
			ap.setPassword(PropertyProvider.getProperty("app.password"));
			c.setAuthorization(ap);

			HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
			httpClientPolicy.setConnectionTimeout(Integer.parseInt(PropertyProvider.getProperty("soap.connection.timeout", "36000")));
			httpClientPolicy.setReceiveTimeout(Integer.parseInt(PropertyProvider.getProperty("test.batchjob.timeout", "1200000")));
			c.setClient(httpClientPolicy);
		}
	}

}