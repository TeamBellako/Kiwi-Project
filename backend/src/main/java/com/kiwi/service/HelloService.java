package com.kiwi.service;

import com.kiwi.entity.HelloDB;
import com.kiwi.repository.HelloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class HelloService {
    @Autowired
    private HelloRepository helloRepository;

    @Transactional
    public String getMessageById(int id) {
        Optional<HelloDB> result = helloRepository.findById(id);
        
        HelloDB message;
        if (result.isPresent())
        {
            message = result.get();
        }
        else {
            throw new RuntimeException("Message not found");
        }
        
        return message.getMessage();
    }
}
