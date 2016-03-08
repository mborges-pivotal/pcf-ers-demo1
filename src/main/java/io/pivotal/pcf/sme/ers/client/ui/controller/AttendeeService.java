package io.pivotal.pcf.sme.ers.client.ui.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.pivotal.pcf.sme.ers.server.model.Attendee;
import io.pivotal.pcf.sme.ers.server.repo.AttendeeRepository;

/**
 * AttendeeServices
 * 
 * The purpose of this service class is to have a clear separation from the UI
 * to the client.
 * 
 * This is a RestController and all the UI stuff is done by the MVC controller.
 * 
 * @see AttendeeController
 * 
 * @author mborges
 *
 */
@RestController
public class AttendeeService {

	private Log log = LogFactory.getLog(AttendeeService.class);
	
	/// Polluting with server code
	// http://blog.zenika.com/2012/06/15/hateoas-paging-with-spring-mvc-and-spring-data-jpa/
	
	@Autowired
	private AttendeeRepository attendeeRepository;
	
	void add(io.pivotal.pcf.sme.ers.client.model.Attendee a1) {
		Attendee a2 = new Attendee();
		a2.setFirstName(a1.getFirstName());
		a2.setLastName(a1.getLastName());
		a2.setEmailAddress(a1.getEmailAddress());
		attendeeRepository.saveAndFlush(a2);
	}
	
	// returning server object
	Iterable<Attendee> getAttendees() {
		return attendeeRepository.findAll();
	}
	
	// returning server object
	Iterable<Attendee> searchName(String firstName) {
		return attendeeRepository.findByFirstNameContainsIgnoreCase(firstName, new PageRequest(0,100));
	}
	

	/**
	 * bluegreenRequest - It really just returns the app name.
	 * 
	 * The purpose is to show PCF load balancing between applications that share
	 * routes
	 * 
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/bluegreen-check")
	public String bluegreenRequest() throws Exception {
		return (String) getVcapApplicationMap().getOrDefault("application_name", "no name environment variable");
	}

	/**
	 * addAppEnv - Retrieve information about the application
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	Map<String, Object> addAppEnv() throws Exception {

		Map<String, Object> modelMap = new HashMap<String, Object>();

		String instanceIndex = getVcapApplicationMap().getOrDefault("instance_index", "no index environment variable")
				.toString();
		modelMap.put("instanceIndex", instanceIndex);

		String instanceAddr = System.getenv("CF_INSTANCE_ADDR");
		if (instanceAddr == null) {
			instanceAddr = "running locally";
		}
		modelMap.put("instanceAddr", instanceAddr);

		String applicationName = (String) getVcapApplicationMap().getOrDefault("application_name",
				"no name environment variable");
		modelMap.put("applicationName", applicationName);

		@SuppressWarnings("rawtypes")
		Map services = getVcapServicesMap();
		modelMap.put("applicationServices", services);

		return modelMap;
	}

	///////////////////////////////////////
	// Helper Methods
	///////////////////////////////////////

	@SuppressWarnings("rawtypes")
	private Map getVcapApplicationMap() throws Exception {
		return getEnvMap("VCAP_APPLICATION");
	}

	@SuppressWarnings("rawtypes")
	private Map getVcapServicesMap() throws Exception {
		return getEnvMap("VCAP_SERVICES");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map getEnvMap(String vcap) throws Exception {
		String vcapEnv = System.getenv(vcap);
		ObjectMapper mapper = new ObjectMapper();

		if (vcapEnv != null) {
			Map<String, ?> vcapMap = mapper.readValue(vcapEnv, Map.class);
			return vcapMap;
		}

		log.warn(vcap + " not defined, returning empty Map");
		return new HashMap<String, String>();
	}

}
