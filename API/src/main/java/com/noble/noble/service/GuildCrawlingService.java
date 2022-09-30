package com.noble.noble.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

@Service
public class GuildCrawlingService {

    public List<String> crawlingNobleNickname() throws IOException {
        List<String> nicknameList = new ArrayList<>();

        for (int i = 1; i < 11; i++) {
            String url = "https://maplestory.nexon.com/Common/Guild?gid=8693&wid=46&orderby=1&page=" + i;

            Document doc = Jsoup.connect(url).get();
            Elements ele = doc.select(".guild_user_list");
            String nameResult = ele.select(".left a").text();
            String[] arrayNickname = nameResult.split(" ");
            if (arrayNickname != null) {
                for (int j = 0; j < arrayNickname.length; j++) {
                    nicknameList.add(arrayNickname[j]);
                }
            } else {
                break;
            }
        }
        
        return nicknameList;
    }

    public List<String> crawlingCenturyNickname() throws IOException {
        List<String> nicknameList = new ArrayList<>();

        for (int i = 1; i < 11; i++) {
            String url = "https://maplestory.nexon.com/Common/Guild?gid=5881&wid=46&orderby=1&page=" + i;

            Document doc = Jsoup.connect(url).get();
            Elements ele = doc.select(".guild_user_list");
            String nameResult = ele.select(".left a").text();
            String[] arrayNickname = nameResult.split(" ");
            if (arrayNickname != null) {
                for (int j = 0; j < arrayNickname.length; j++) {
                    nicknameList.add(arrayNickname[j]);
                }
            } else {
                break;
            }
        }
        
        return nicknameList;
    }

    public List<String> crawlingDotaxNickname() throws IOException {
        List<String> nicknameList = new ArrayList<>();

        for (int i = 1; i < 11; i++) {
            String url = "https://maplestory.nexon.com/Common/Guild?gid=1015&wid=46&orderby=1&page=" + i;

            Document doc = Jsoup.connect(url).get();
            Elements ele = doc.select(".guild_user_list");
            String nameResult = ele.select(".left a").text();
            String[] arrayNickname = nameResult.split(" ");
            if (arrayNickname != null) {
                for (int j = 0; j < arrayNickname.length; j++) {
                    nicknameList.add(arrayNickname[j]);
                }
            } else {
                break;
            }
        }
        
        return nicknameList;
    }

    public Map<String, Object> getUserInfo(String nickname) throws IOException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        String url = "https://maple.gg/u/" + nickname;
        Document doc = Jsoup.connect(url).get();
        Elements status = doc.select(".user-summary-item");
        String[] levelArray = status.get(0).text().split("\\(");
        String[] level = levelArray[0].split("\\.");
        Element dojang = doc.select(".user-summary-box").first();
        if (dojang.toString().contains("user-summary-floor")) {
            String dojangResult = dojang.select(".user-summary-floor").text();
            String[] dojangArray = dojangResult.split(" ");
            response.put("level", Integer.valueOf(level[1]));
            response.put("nickname", nickname);
            response.put("job", status.get(1).text());
            response.put("dojang", Integer.valueOf(dojangArray[0]));
        } else {
            response.put("level", Integer.valueOf(level[1]));
            response.put("nickname", nickname);
            response.put("job", status.get(1).text());
            response.put("dojang", 0);
        }

        //System.out.println(response);
        return response;
    }
}