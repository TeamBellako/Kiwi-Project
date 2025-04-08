package com.kiwi.service;

import com.kiwi.utils.HelloBaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class HelloServiceTest extends HelloBaseTest {
    @Test
    public void GetMessageByID_ReturnsMessage_IfItExists() {
        super.setUp(); // calling super here because MockitoJUnitRunner doesn't automatically do it so
        
        Mockito.when(helloRepository.findById(1)).thenReturn(Optional.of(helloDB));
        
        String message = helloService.getMessageById(1);

        assertEquals("Hello World!", message);
    }
}
