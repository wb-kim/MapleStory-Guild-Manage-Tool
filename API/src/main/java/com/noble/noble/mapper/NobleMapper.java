package com.noble.noble.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.noble.noble.data.Noble;

@Mapper
public interface NobleMapper {
    public int insertNoble(Noble noble);
    public List<Noble> getNobleListForTotal(Map<String, Object> param);
    public List<String> getNobleSubList(String mainChar);
    public List<String> getMainCharFromNoble(String nickname);
    public List<Noble> getNobleList(Map<String, Object> param);
    public int updateNoble(Noble noble);
    public int deleteNoble(int idx);
    public Noble getNoble(int idx);
    public int deleteNobleFromMain(String mainChar);
    public List<Noble> getNobleForDojang();
    public List<String> getNobleNickname();
    public List<String> getAdmin();
    public int getNobleCount();
}
