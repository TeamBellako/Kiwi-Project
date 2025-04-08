package com.kiwi.utils;

import com.kiwi.entity.HelloDB;
import com.kiwi.repository.HelloRepository;
import com.kiwi.service.HelloService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class HelloBaseTest {

    @Mock
    protected HelloRepository helloRepository;

    @InjectMocks
    protected HelloService helloService;
    
    protected HelloDB helloDB;
    
    @BeforeEach
    public void setUp() {
        helloDB = new HelloDB(1, "Hello World!");
    }
}

