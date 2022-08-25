package com.noble.noble.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommonService {
    
    public int offset(int page) {
        return (page - 1) * 20;
    }
}
