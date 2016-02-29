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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import io.pivotal.pcf.sme.ers.client.AttendeeClient;
import io.pivotal.pcf.sme.ers.client.model.Attendee;
import io.pivotal.pcf.sme.ers.client.model.PagedAttendees;

/**
 * AttendeeController
 * 
 * This is the MVC controller for the application. All UI HTTP requests get
 * here. We're using Thymeleaf as the template engine.
 * 
 * 
 * @author mborges
 *
 */
@Controller
public class AttendeeController {

	private Log log = LogFactory.getLog(AttendeeController.class);

	@Autowired
	private AttendeeClient attendeeClient;

	@Autowired
	private AttendeeService attendeeService;

	/**
	 * INDEX
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/")
	public String index(Model model) throws Exception {
		addAppEnv(model);
		return "index";
	}

	/**
	 * BASICS
	 * 
	 * Action to initiate shutdown of the system. In CF, the application
	 * <em>should</em> restart. In other environments, the application runtime
	 * will be shut down.
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/basics", method = RequestMethod.GET)
	public String kill(@RequestParam(value = "doit", required = false) boolean doit, Model model) throws Exception {

		addAppEnv(model);

		if (doit) {
			model.addAttribute("killed", true);
			log.warn("*** The system is shutting down. ***");
			Runnable killTask = () -> {
				try {
					String name = Thread.currentThread().getName();
					log.warn("killing shortly " + name);
					TimeUnit.SECONDS.sleep(5);
					log.warn("killed " + name);
					System.exit(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
			new Thread(killTask).start();
		}

		return "basics";

	}

	/**
	 * SERVICES
	 * 
	 * @param model
	 *            The model for this action.
	 * @return The path to the view.
	 */
	@HystrixCommand(fallbackMethod = "defaultAttendees")
	@RequestMapping(value = "/services", method = RequestMethod.GET)
	public String attendees(Model model) throws Exception {

		PagedAttendees attendees = attendeeClient.getAttendees();

		model.addAttribute("attendees", attendees);
		addAppEnv(model);
		return "services";
	}

	// hystrix fallback
	// MMB: find a way to exercise this
	public String defaultAttendees(Model model) throws Exception {
		addAppEnv(model);
		return "services";
	}

	/**
	 * SERVICES - Add Attendee
	 * 
	 * NOTE: this method chains (calls) the "attendees" method so it returns the
	 * services template with the updated attendees list.
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

		// MMB: chaining methods - IMHO, this is confusing so think about fixing it.
		return attendees(model);
	}

	/**
	 * SERVICES - searchAttendees
	 * 
	 * This is an interesting method that returns a page component based on a
	 * thymeleaf fragment. We expect this to be an async call from the HTML
	 * page.
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
	 * BLUEGREEN
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/bluegreen")
	public String bluegreen(Model model) throws Exception {

		for (String key : System.getenv().keySet()) {
			System.out.println(key + ":" + System.getenv(key));
		}

		addAppEnv(model);

		return "bluegreen";
	}

	///////////////////////////////////////
	// Helper Methods
	///////////////////////////////////////

	private void addAppEnv(Model model) throws Exception {

		Map<String, Object> modelMap = attendeeService.addAppEnv();
		model.addAllAttributes(modelMap);
	}

}
