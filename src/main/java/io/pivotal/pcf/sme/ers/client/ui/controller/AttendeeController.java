package io.pivotal.pcf.sme.ers.client.ui.controller;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import io.pivotal.pcf.sme.ers.client.AttendeeClient;
import io.pivotal.pcf.sme.ers.client.model.Attendee;
import io.pivotal.pcf.sme.ers.client.model.PagedAttendees;

@Controller
public class AttendeeController {

	private Log log = LogFactory.getLog(AttendeeController.class);

	@Autowired
	private AttendeeClient attendeeClient;

	@RequestMapping("/")
	public String index(Model model) throws Exception {
		
		addAppInstanceIndex(model);
		return "index";
	}

	// Blue and Green
	@RequestMapping("/bluegreen")
	public String bluegreen(Model model) throws Exception {

		for (String key : System.getenv().keySet()) {
			System.out.println(key + ":" + System.getenv(key));
		}

		addAppInstanceIndex(model);

		return "bluegreen";
	}


	/**
	 * Action to get a list of all attendees.
	 * 
	 * @param model
	 *            The model for this action.
	 * @return The path to the view.
	 */
	@HystrixCommand(fallbackMethod = "defaultAttendees")
	@RequestMapping(value = "/list-attendees", method = RequestMethod.GET)
	public String attendees(Model model) throws Exception {

		PagedAttendees attendees = attendeeClient.getAttendees();

		model.addAttribute("attendees", attendees);
		addAppInstanceIndex(model);
		return "attendees";
	}

	public String defaultAttendees(Model model) throws Exception {
		addAppInstanceIndex(model);
		return "attendees";
	}

	/**
	 * Action to got the the add attendee page
	 * 
	 * @return
	 */
	@RequestMapping(value = "/add-attendee", method = RequestMethod.GET)
	public String addAttendee() {
		return "addAttendee";
	}

	/**
	 * Action to add attendee
	 * 
	 * @param firstName
	 * @param lastName
	 * @param emailAddress
	 * @param model
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/add-attendee", method = RequestMethod.POST)
	public String addAttendee(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
			@RequestParam("emailAddress") String emailAddress, Model model) throws Exception {

		Attendee attendee = new Attendee();
		attendee.setFirstName(firstName);
		attendee.setLastName(lastName);
		attendee.setEmailAddress(emailAddress);

		attendeeClient.add(attendee);

		return attendees(model);
	}

	/**
	 * Action to initiate shutdown of the system. In CF, the application
	 * <em>should</em> restart. In other environments, the application runtime
	 * will be shut down.
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/kill", method = RequestMethod.GET)
	public String kill(Model model) throws Exception {

		log.warn("*** The system is shutting down. ***");
		addAppInstanceIndex(model);

		Runnable killTask = () -> {
		    try {
		        String name = Thread.currentThread().getName();
		        log.warn("killing shortly " + name);
		        TimeUnit.SECONDS.sleep(5);
		        log.warn("killed " + name);
				System.exit(0);
		    }
		    catch (InterruptedException e) {
		        e.printStackTrace();
		    }
		};
		new Thread(killTask).start();

		return "kill";

	}

	/**
	 * searchAttendees - returns result model for a thymeleaf fragment
	 * 
	 * @param firstName
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/search-attendees-fn", method = RequestMethod.GET)
	public String searchAttendees(@RequestParam("firstName") String firstName, Model model) throws Exception {

		PagedAttendees attendees = attendeeClient.searchName(firstName);

		model.addAttribute("attendees", attendees);
		return "fragments/list :: attendeeList";
	}

	/**
	 * Action to got the the add attendee page
	 * 
	 * @return
	 */
	@RequestMapping(value = "/search-attendees", method = RequestMethod.GET)
	public String searchAttendees() {
		return "searchAttendees";
	}

	///////////////////////////////////////
	// Helper Methods
	///////////////////////////////////////

	private void addAppInstanceIndex(Model model) throws Exception {

		String instanceIndex = System.getenv("CF_INSTANCE_INDEX");

		if (instanceIndex == null) {
			log.info("No CF_INSTANCE_INDEX, going to VCAP_APPLICATION");
			if (getVCAPMap() != null) {
				instanceIndex = Integer.toString((Integer) getVCAPMap().get("instance_index"));
			} else {
				instanceIndex = "no index environment variable";
			}
		}

		String instanceAddr = System.getenv("CF_INSTANCE_ADDR");
		if (instanceAddr == null) {
			instanceAddr = "running locally";
		}

		model.addAttribute("instanceIndex", instanceIndex);
		model.addAttribute("instanceAddr", instanceAddr);
	}

	@SuppressWarnings("rawtypes")
	private Map getVCAPMap() throws Exception {
		String vcapApplication = System.getenv("VCAP_APPLICATION");
		ObjectMapper mapper = new ObjectMapper();
		if (vcapApplication != null) {
			Map vcapMap = mapper.readValue(vcapApplication, Map.class);
			return vcapMap;
		}

		return null;
	}

}
