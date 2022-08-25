package com.noble.noble.service;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noble.noble.data.Log;
import com.noble.noble.mapper.LogMapper;

@Service
@Transactional
public class LogService {
    
    @Autowired private LogMapper logMapper;
    
    public boolean insertLog(Log log) {
        return logMapper.insertLog(log) > 0 ? true : false;
    }

    public List<Log> getLogList(Map<String, Object> param) {
        return logMapper.getLogList(param);
    }
}
