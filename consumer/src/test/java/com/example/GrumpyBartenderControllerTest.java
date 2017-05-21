package com.example;

import org.assertj.core.api.BDDAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Marcin Grzejszczak
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
//remove::start[]
// example of usage with fixed port
@AutoConfigureStubRunner(workOffline = true, ids = "com.example:beer-api-producer-advanced")
//remove::end[]
@DirtiesContext
public class GrumpyBartenderControllerTest extends AbstractTest {

	@Autowired MockMvc mockMvc;
	@Autowired GrumpyBartenderController controller;
	//remove::start[]
	@Autowired StubFinder stubFinder;

	@Value("${stubrunner.runningstubs.beer-api-producer-advanced.port}") int producerPort;

	@Before
	public void setupPort() {
		// either one or the other option
		int port1 = stubFinder.findStubUrl("beer-api-producer-advanced").getPort();
		int port2 = producerPort;
		BDDAssertions.then(port1).isEqualTo(port2);
		controller.port = port1;
	}
	//remove::end[]

	//tag::tests[]
	@Test public void should_fail_to_sell_beer() throws Exception {
		//remove::start[]
		mockMvc.perform(MockMvcRequestBuilders.post("/grumpy")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json.write(new Person("marcin", 22)).getJson()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.whatTheBartenderSaid").value("You're drunk [marcin]. Go home!"))
				.andExpect(jsonPath("$.whatDoWeDo").value("Let's go to another bar"));
		//remove::end[]
	}
	//end::tests[]
}