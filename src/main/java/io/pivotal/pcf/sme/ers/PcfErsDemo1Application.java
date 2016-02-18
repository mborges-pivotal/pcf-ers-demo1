package io.pivotal.pcf.sme.ers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * PcfErsDemo1Application
 * 
 * @TODO:
 * - Git versioning (maven plugin) 
 * - concourse CI/CD
 * - 
 * 
 * @author mborges
 *
 */
@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker
@EnableHystrixDashboard
public class PcfErsDemo1Application {

	public static void main(String[] args) {
		SpringApplication.run(PcfErsDemo1Application.class, args);
	}
}
