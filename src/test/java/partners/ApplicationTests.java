package partners;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import partners.repository.PartnerRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests {

	private String uri = "/partners";

	private String testName = "amazon";
	private String testIndustry = "IT and Web Services";
	private Integer testSince = 2000;

	private String testPartner = "{\"name\": \"" + testName + "\", " +
			"\"industry\":\"" + testIndustry + "\", " +
			"\"partnerSince\":\"" + testSince + "\"}";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PartnerRepository partnerRepository;

	@Before
	public void deleteAllBeforeTests() throws Exception {
		partnerRepository.deleteAll();
	}

	@Test
	public void shouldReturnRepositoryIndex() throws Exception {
		mockMvc.perform(get("/"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._links.partners").exists());
	}

	@Test
	public void shouldCreateEntity() throws Exception {
		mockMvc.perform(post(uri).content(testPartner))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("partners/")));
	}

	@Test
	public void shouldRetrieveEntity() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(post(uri).content(testPartner))
				.andExpect(status().isCreated())
				.andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(get(location))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(testName))
				.andExpect(jsonPath("$.industry").value(testIndustry))
				.andExpect(jsonPath("$.partnerSince").value(testSince));
	}

	@Test
	public void shouldQueryEntity() throws Exception {
		mockMvc.perform(post(uri).content(testPartner))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/partners/search/findByName?name={name}", testName))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.partners[0].name").value(testName));
	}

	@Test
	public void shouldUpdateEntity() throws Exception {

		String updatedPartner = "{\"name\": \"amazon\", \"industry\":\"Ecommerce\", \"partnerSince\":\"2010\"}";

		MvcResult mvcResult = mockMvc
				.perform(post(uri).content(testPartner))
				.andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(put(location)
				.content(updatedPartner))
				.andExpect(status().isNoContent());

		mockMvc.perform(get(location))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.industry").value("Ecommerce"))
				.andExpect(jsonPath("$.partnerSince").value(2010));
	}

	@Test
	public void shouldPartiallyUpdateEntity() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(post(uri).content(testPartner))
				.andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(patch(location).content("{\"partnerSince\":\"2010\"}"))
				.andExpect(status().isNoContent());

		mockMvc.perform(get(location))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.industry").value(testIndustry))
				.andExpect(jsonPath("$.partnerSince").value(2010));
	}

	@Test
	public void shouldDeleteEntity() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(post(uri).content(testPartner))
				.andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}
}