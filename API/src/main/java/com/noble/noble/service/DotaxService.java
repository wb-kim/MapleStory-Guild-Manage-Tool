package com.noble.noble.service;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noble.noble.data.Dotax;
import com.noble.noble.mapper.DotaxMapper;

@Service
@Transactional
public class DotaxService {
    
    @Autowired private DotaxMapper dotaxMapper;
    
    public boolean insertDotax(Dotax dotax) {
        return dotaxMapper.insertDotax(dotax) > 0 ? true : false;
    }

    public List<String> getDotaxListFromMain(String mainChar) {
        return dotaxMapper.getDotaxListFromMain(mainChar);
    }

    public List<String> getDotaxUpperListFromMain(String mainChar) {
        return dotaxMapper.getDotaxUpperListFromMain(mainChar);
    }

    public List<String> getMainCharFromDotax(String nickname) {
        return dotaxMapper.getMainCharFromDotax(nickname);
    }

    public List<Dotax> getDotaxList(Map<String, Object> param) {
        return dotaxMapper.getDotaxList(param);
    }

    public boolean deleteDotax(int idx) {
        return dotaxMapper.deleteDotax(idx) > 0 ? true : false;        
    }

    public Dotax getDotax(int idx) {
        return dotaxMapper.getDotax(idx);
    }

    public boolean updateDotax(Dotax dotax) {
        return dotaxMapper.updateDotax(dotax) > 0 ? true : false;
    }

    public boolean deleteDotaxFromMain(String mainChar) {
        return dotaxMapper.deleteDotaxFromMain(mainChar) > 0 ? true : false;        
    }

    public List<String> getDotaxNickname() {
        return dotaxMapper.getDotaxNickname();
    }

    public int getDotaxCount() {
        return dotaxMapper.getDotaxCount();
    }
}
