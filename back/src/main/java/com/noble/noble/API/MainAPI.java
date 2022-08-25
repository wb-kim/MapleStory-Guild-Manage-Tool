package com.noble.noble.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.noble.noble.data.Noble;
import com.noble.noble.service.CenturyService;
import com.noble.noble.service.CommonService;
import com.noble.noble.service.DotaxService;
import com.noble.noble.service.NobleService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class MainAPI {
    private final String NOBLE_USER_KEY = "noble127";
    private final String NOBLE_ADMIN_KEY = "nobleherohibi";    

    @Autowired private CommonService commonService;
    @Autowired private NobleService nobleService;
    @Autowired private CenturyService centuryService;
    @Autowired private DotaxService dotaxService;
    

    @PostMapping("/Main/login")
    public String login(@RequestBody Map<String, Object> param, HttpServletRequest request) {
        String response = "ERROR";
        
        String code = param.get("code") != null ? (String)param.get("code") : "";

        HttpSession session = request.getSession();

        try {
            if (code.equals(NOBLE_USER_KEY)) {
                session.setAttribute("data", "USER");
                response = "SUCCESS";
            } else if (code.equals(NOBLE_ADMIN_KEY)) {
                session.setAttribute("data", "ADMIN");
                response = "SUCCESS";
            } else {
                response = "ERROR";
            }
        } catch (Exception e) {
            response = "ERROR";
        }

        return response;
    }

    @PostMapping("/Main/session")
    public String session(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (String)session.getAttribute("data");
    }

    @PostMapping("/Main/logout")
    public String logout(HttpSession session) {
        String response = "ERROR";
        
        session.invalidate();
        response = "SUCCESS";
        
        return response;
    }

    @PostMapping("/Main/getTotalList")
    public List<Map<String, Object>> getTotalList(@RequestBody Map<String, Object> param) {
        List<Map<String, Object>> response = new ArrayList<>();

        Map<String, Object> searchParam = new HashMap<>();
        
        String searchStr = param.get("searchStr") != null ? (String)param.get("searchStr") : "";
        String order = param.get("order") != null ? (String)param.get("order") : "idx ASC";
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        searchParam.put("searchStr", searchStr);
        searchParam.put("order", order);
        searchParam.put("offset", commonService.offset(page));

        List<Noble> nobleList = nobleService.getNobleListForTotal(searchParam);
        List<String> nobleNicknameList = new ArrayList<>();
        for (Noble noble : nobleList) {
            nobleNicknameList.add(noble.getNickname());
        }
        
        if (searchStr != "") {
            List<String> nobleSubList = nobleService.getMainCharFromNoble(searchStr);
            for (String mainChar : nobleSubList) {
                System.out.println(nobleNicknameList.indexOf(mainChar) + " " + mainChar);
                if (nobleNicknameList.indexOf(mainChar) < 0) {
                    Noble noble = new Noble();
                    noble.setNickname(mainChar);
                    nobleNicknameList.add(mainChar);
                    nobleList.add(noble);
                }
            }

            List<String> centuryList = centuryService.getMainCharFromCentury(searchStr);
            for (String mainChar : centuryList) {
                System.out.println(nobleNicknameList.indexOf(mainChar) + " " + mainChar);
                if (nobleNicknameList.indexOf(mainChar) < 0) {
                    Noble noble = new Noble();
                    noble.setNickname(mainChar);
                    nobleNicknameList.add(mainChar);
                    nobleList.add(noble);
                }
            }

            List<String> dotaxList = dotaxService.getMainCharFromDotax(searchStr);
            for (String mainChar : dotaxList) {
                System.out.println(nobleNicknameList.indexOf(mainChar) + " " + mainChar);
                if (nobleNicknameList.indexOf(mainChar) < 0) {
                    Noble noble = new Noble();
                    noble.setNickname(mainChar);
                    nobleNicknameList.add(mainChar);
                    nobleList.add(noble);
                }
            }
        }

        for (Noble noble : nobleList) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("main", noble.getNickname());
            temp.put("sub", nobleService.getNobleSubList(noble.getNickname()));
            temp.put("century", centuryService.getCenturyListFromMain(noble.getNickname()));
            temp.put("centuryUpper", centuryService.getCenturyUpperListFromMain(noble.getNickname()));
            temp.put("dotax", dotaxService.getDotaxListFromMain(noble.getNickname()));
            temp.put("dotaxUpper", dotaxService.getDotaxUpperListFromMain(noble.getNickname()));
            temp.put("exemptedDate", noble.getExemptedDate());

            response.add(temp);
        }

        return response;
    }

    @PostMapping("/Main/getDojangList")
    public List<Noble> getDojangList(@RequestBody Map<String, Object> param) {
        List<Noble> response = new ArrayList<>();

        Map<String, Object> searchParam = new HashMap<>();
        
        String searchStr = param.get("searchStr") != null ? (String)param.get("searchStr") : "";
        String order = param.get("order") != null ? (String)param.get("order") : "idx ASC";
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        searchParam.put("searchStr", searchStr);
        searchParam.put("order", order);
        searchParam.put("offset", commonService.offset(page));

        List<Noble> nobleList = nobleService.getNobleList(searchParam);

        for (Noble noble : nobleList) {
            if (noble.getDojangAgree() == 1) {
                response.add(noble);
            }
        }

        return response;
    }
}
