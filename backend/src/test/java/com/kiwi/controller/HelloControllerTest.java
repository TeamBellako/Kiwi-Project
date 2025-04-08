package com.kiwi.controller;

import com.kiwi.entity.HelloDB;
import com.kiwi.service.HelloService;
import com.kiwi.utils.HelloBaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(HelloController.class)
public class HelloControllerTest extends HelloBaseTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HelloService helloService;
    
    @Test
    public void GetMessageByID_ReturnsMessage_IfItExists() throws Exception {
        Mockito.when(helloService.getMessageById(1)).thenReturn("Hello World!");
        
        mockMvc.perform(get("/api/hello/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello World!"));
    }
    
}
