package com.kiwi.service;

import com.kiwi.entity.HelloDB;
import com.kiwi.repository.HelloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class HelloService {
    private HelloRepository helloRepository;

    @Autowired
    public HelloService(HelloRepository helloRepository) {
        this.helloRepository = helloRepository;
    }

    @Transactional
    public Optional<HelloDB> getMessageById(Integer id) {
        return Optional.ofNullable(helloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found")));
    }
}
