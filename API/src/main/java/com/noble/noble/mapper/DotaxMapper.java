package com.noble.noble.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.noble.noble.data.Dotax;

@Mapper
public interface DotaxMapper {
    public int insertDotax(Dotax dotax);
    public List<String> getDotaxListFromMain(String mainChar);
    public List<String> getDotaxUpperListFromMain(String mainChar);
    public List<String> getMainCharFromDotax(String nickname);
    public List<Dotax> getDotaxList(Map<String, Object> param);
    public int updateDotax(Dotax dotax);
    public int deleteDotax(int idx);
    public Dotax getDotax(int idx);
    public int deleteDotaxFromMain(String mainChar);
    public List<String> getDotaxNickname();
    public int getDotaxCount();
}
