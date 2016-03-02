package io.pivotal.pcf.sme.ers.client;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ConfigurationBasedServerList;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

/**
 * AttendeeConfiguration
 * 
 * The purpose is to obtain the ribbon list of servers from PCF User Provided
 * Service. We're also relying on Spring Cloud Connectors to wire relation db
 * services.
 *
 * This is only used in the "cloud" profile
 *
 *
 * References - Will remove once code is all well documented.
 * 
 * https://spring.io/blog/2015/01/20/microservice-registration-and-discovery-
 * with-spring-cloud-and-netflix-s-eureka
 * https://github.com/Netflix/ribbon/blob/master/ribbon-loadbalancer/src/main/
 * java/com/netflix/loadbalancer/ConfigurationBasedServerList.java
 * https://github.com/spring-cloud/spring-cloud-netflix/issues/564
 * https://github.com/spring-cloud/spring-cloud-netflix
 * http://cloud.spring.io/spring-cloud-netflix/spring-cloud-netflix.html#spring-
 * cloud-ribbon-without-eureka
 * https://github.com/spring-cloud/spring-cloud-netflix/blob/master/spring-cloud
 * -netflix-core/src/main/java/org/springframework/cloud/netflix/ribbon/
 * RibbonClientConfiguration.java
 * 
 * Spring Cloud Connectors
 * https://spring.io/blog/2015/04/27/binding-to-data-services-with-spring-boot-
 * in-cloud-foundry
 * https://docs.cloudfoundry.org/buildpacks/java/spring-service-bindings.html
 * 
 * @author mborges
 *
 */
@Configuration
@Profile("cloud")
public class AttendeeConfiguration {

	private Log log = LogFactory.getLog(AttendeeConfiguration.class);

	// MMB: would expect to come from the RibbonClient annotation
	// @Value("${ribbon.client.name}")
	private String name = "attendees";

	///////////////////
	//// Datasource
	///////////////////

	@Bean
	// https://github.com/Pivotal-Field-Engineering/pcf-workspace-microservices/tree/master/micro/cities-client/src/main/java/com/example/cities/client/cloud
	public Cloud cloud() {
		CloudFactory cf = new CloudFactory();
		Cloud c = cf.getCloud();

		List<ServiceInfo> serviceInfos = c.getServiceInfos();
		for (ServiceInfo sf : serviceInfos) {
			log.info("************************** #List of services " + sf);
		}

		return c;
	}

	@Bean
	@ConfigurationProperties(DataSourceProperties.PREFIX)
	// https://spring.io/blog/2014/07/29/using-spring-cloud-programmatically
	public DataSource dataSource() {
		try {
			return cloud().getSingletonServiceConnector(DataSource.class, null);
		} catch (CloudException ce) {
			log.warn("Problem creating cloud based datasource, using embedded DB - " + ce);
		}

		return new EmbeddedDatabaseBuilder().setName("music").setType(EmbeddedDatabaseType.H2).build();

	}

	//////////////
	//// Ribbon
	//////////////

	@Bean
	public IClientConfig ribbonClientConfig() {
		DefaultClientConfigImpl config = new DefaultClientConfigImpl();
		config.loadProperties(name);
		return config;
	}

	@Bean
	public ServerList<Server> ribbonServerList() {
		ConfigurationBasedServerList serverList = new ConfigurationBasedServerList();
		serverList.initWithNiwsConfig(ribbonClientConfig());
		log.info("************************** #Ribbon ListOfServers " + serverList.getInitialListOfServers());
		return serverList;

		// return new StaticServerList<>(new Server("localhost", 8080));

	}

}
