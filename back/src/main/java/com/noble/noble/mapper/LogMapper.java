package com.noble.noble.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.noble.noble.data.Log;

@Mapper
public interface LogMapper {
    public int insertLog(Log log);
    public List<Log> getLogList(Map<String, Object> param);
}
