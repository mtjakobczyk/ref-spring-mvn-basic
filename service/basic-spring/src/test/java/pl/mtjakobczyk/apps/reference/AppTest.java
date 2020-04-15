package pl.mtjakobczyk.apps.reference;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(properties = {"GENERATOR_NAME=GEN1"})
@AutoConfigureMockMvc
public class AppTest
{
	@Autowired
	private MockMvc mockMvc;
	
	@Test
    public void shouldReturnOK() throws Exception
    {
        this.mockMvc.perform(get("/uuid")).andDo(print()).andExpect(status().isOk());
    }

	@Test
    public void shouldReturnGeneratorName() throws Exception
    {
        this.mockMvc.perform(get("/uuid")).andDo(print()).andExpect(jsonPath("$.generatorName", is("GEN1")));
    }

	
}
