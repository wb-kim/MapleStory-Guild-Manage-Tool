package com.noble.noble.service;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noble.noble.data.Noble;
import com.noble.noble.mapper.NobleMapper;

@Service
@Transactional
public class NobleService {
    
    @Autowired private NobleMapper nobleMapper;
    
    public boolean insertNoble(Noble noble) {
        return nobleMapper.insertNoble(noble) > 0 ? true : false;
    }

    public List<Noble> getNobleListForTotal(Map<String, Object> param) {
        return nobleMapper.getNobleListForTotal(param);
    }

    public List<String> getNobleSubList(String mainChar) {
        return nobleMapper.getNobleSubList(mainChar);
    }

    public List<String> getMainCharFromNoble(String nickname) {
        return nobleMapper.getMainCharFromNoble(nickname);
    }

    public List<Noble> getNobleList(Map<String, Object> param) {
        return nobleMapper.getNobleList(param);
    }

    public boolean deleteNoble(int idx) {
        return nobleMapper.deleteNoble(idx) > 0 ? true : false;        
    }

    public Noble getNoble(int idx) {
        return nobleMapper.getNoble(idx);
    }

    public boolean updateNoble(Noble noble) {
        return nobleMapper.updateNoble(noble) > 0 ? true : false;
    }

    public boolean useExempt(Map<String, Object> param) {
        return nobleMapper.useExempt(param) > 0 ? true : false;
    }

    public boolean warning(int idx) {
        return nobleMapper.warning(idx) > 0 ? true : false;
    }

    public boolean deleteNobleFromMain(String mainChar) {
        return nobleMapper.deleteNobleFromMain(mainChar) > 0 ? true : false;        
    }

    public List<Noble> getNobleForDojang() {
        return nobleMapper.getNobleForDojang();
    }

    public List<String> getNobleNickname() {
        return nobleMapper.getNobleNickname();
    }

    public List<String> getAdmin() {
        return nobleMapper.getAdmin();
    }

    public int getNobleCount() {
        return nobleMapper.getNobleCount();
    }
}
