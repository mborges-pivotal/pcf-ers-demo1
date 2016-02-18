package io.pivotal.pcf.sme.ers.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.pivotal.pcf.sme.ers.client.model.Attendee;
import io.pivotal.pcf.sme.ers.client.model.PagedAttendees;

@FeignClient(name = "attendees")
public interface AttendeeClient {

	@RequestMapping(method = RequestMethod.GET, value = "/attendees")
	PagedAttendees getAttendees();

	@RequestMapping(method = RequestMethod.POST, value = "/attendees", consumes = "application/json")
	void add(Attendee attendee);

	@RequestMapping(method = RequestMethod.GET, value = "/attendees/search/nameContains?q={firstName}")
	PagedAttendees searchName(@PathVariable("firstName") String firstName);

	/////////////////////

	/**
	 * findAll
	 * @return HATEOS Resources with Attendee contents
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/attendees")
	PagedResources<Resource<Attendee>> findAll();

}
