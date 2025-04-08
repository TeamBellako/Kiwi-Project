package com.kiwi.integration;

import com.kiwi.repository.HelloRepository;
import com.kiwi.utils.HelloBaseTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/setUpH2DB.sql")
@ActiveProfiles("test")
public class HelloIntegrationTest extends HelloBaseTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HelloRepository helloRepository;

    @Test
    public void GetMessageByID_ReturnsMessage_IfItExists() throws Exception {
        super.setUp(); // calling super here because SpringRunner doesn't automatically do it so

        helloRepository.save(helloDB);

        mockMvc.perform(get("/api/hello/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello World!"));
    }
}
