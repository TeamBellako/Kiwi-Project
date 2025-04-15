package com.kiwi.service;

import com.kiwi.entity.HelloDB;
import com.kiwi.repository.HelloRepository;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloServiceTest {
    private HelloRepository helloRepository = Mockito.mock(HelloRepository.class);
    
    protected HelloService helloService = new HelloService(helloRepository);
    
    @Test
    public void GetMessageByID_ReturnsMessage_IfItExists() {
        HelloDB helloDB = new HelloDB(1, "Hello World!");
        Mockito.when(helloRepository.findById(1)).thenReturn(Optional.of(helloDB));
        
        String message = helloService.getMessageById(1).get().getMessage();

        assertEquals("Hello World!", message);
    }
}
