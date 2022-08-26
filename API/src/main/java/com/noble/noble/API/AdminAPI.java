package com.noble.noble.API;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.noble.noble.data.Century;
import com.noble.noble.data.Dotax;
import com.noble.noble.data.Log;
import com.noble.noble.data.Noble;
import com.noble.noble.service.CenturyService;
import com.noble.noble.service.CommonService;
import com.noble.noble.service.DotaxService;
import com.noble.noble.service.GuildCrawlingService;
import com.noble.noble.service.LogService;
import com.noble.noble.service.NobleService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class AdminAPI {
    @Autowired private CommonService commonService;
    @Autowired private NobleService nobleService;
    @Autowired private CenturyService centuryService;
    @Autowired private DotaxService dotaxService;
    @Autowired private LogService logService;
    @Autowired private GuildCrawlingService crawlingService;

    @PostMapping("/Admin/getAdmin")
    public List<String> getAdmin() {
        return nobleService.getAdmin();
    }

    @PostMapping("/Admin/getLogList")
    public List<Log> getLogList(@RequestBody Map<String, Object> param) {
        Map<String, Object> searchParam = new HashMap<>();
        
        String searchStr = param.get("searchStr") != null ? (String)param.get("searchStr") : "";
        String order = param.get("order") != null ? (String)param.get("order") : "idx ASC";
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        searchParam.put("searchStr", searchStr);
        searchParam.put("order", order);
        searchParam.put("offset", commonService.offset(page));

        return logService.getLogList(searchParam);
    }

    @PostMapping("/Admin/getNobleList")
    public List<Noble> getNobleList(@RequestBody Map<String, Object> param) {
        Map<String, Object> searchParam = new HashMap<>();
        
        String searchStr = param.get("searchStr") != null ? (String)param.get("searchStr") : "";
        String order = param.get("order") != null ? (String)param.get("order") : "idx ASC";
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        searchParam.put("searchStr", searchStr);
        searchParam.put("order", order);
        searchParam.put("offset", commonService.offset(page));

        List<Noble> nobleList = nobleService.getNobleList(searchParam);

        return nobleList;
    }

    @PostMapping("/Admin/getNoble")
    public Noble getNoble(@RequestBody Map<String, Object> param) {
        int idx = param.get("idx") != null ? (int)param.get("idx") : 0;

        return nobleService.getNoble(idx);
    }

    @PostMapping("/Admin/insertNoble")
    public String insertNoble(@RequestBody Noble noble) throws IOException, InterruptedException {
        String response = "ERROR";

        Map<String, Object> userInfo = crawlingService.getUserInfo(noble.getNickname());
        noble.setDojang((int)userInfo.get("dojang"));
        noble.setLevel((int)userInfo.get("level"));
        noble.setJob((String)userInfo.get("job"));

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Log log = new Log();
        log.setNickname(noble.getNickname());
        log.setWhat("노블 길드 가입");
        log.setWhen((String)now.format(formatter));
        log.setWho(noble.getGrantor());
        
        if (nobleService.insertNoble(noble) && logService.insertLog(log)) {
            response = "SUCCESS";
        }

        return response;
    }

    @PostMapping("/Admin/updateNoble")
    public String updateNoble(@RequestBody Noble noble) throws IOException, InterruptedException {
        String response = "ERROR";

        Map<String, Object> userInfo = crawlingService.getUserInfo(noble.getNickname());
        noble.setDojang((int)userInfo.get("dojang"));
        noble.setLevel((int)userInfo.get("level"));
        noble.setJob((String)userInfo.get("job"));

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Log log = new Log();
        log.setNickname(noble.getNickname());
        log.setWhat("정보 수정");
        log.setWhen((String)now.format(formatter));
        log.setWho(noble.getGrantor());
        
        if (nobleService.updateNoble(noble) && logService.insertLog(log)) {
            response = "SUCCESS";
        }

        return response;
    }

    @PostMapping("/Admin/deleteNoble")
    public String deleteNoble(@RequestBody Map<String, Object> param) {
        String response = "ERROR";

        int idx = param.get("idx") != null ? (int)param.get("idx") : -1;
        String reason = param.get("reason") != null ? (String)param.get("reason") : "";

        Noble noble = nobleService.getNoble(idx);
        String nickname = new String();

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Log log = new Log();
        log.setNickname(noble.getNickname());
        log.setWhen((String)now.format(formatter));
        log.setWho(noble.getGrantor());
        log.setReason(reason);

        if (noble.getMainChar() == null) {
            log.setWhat("전체 길드 탈퇴");
            nickname = noble.getNickname();
            if (dotaxService.deleteDotaxFromMain(nickname)) {
                if (centuryService.deleteCenturyFromMain(nickname)) {
                    if (nobleService.deleteNobleFromMain(nickname)) {
                        if (nobleService.deleteNoble(idx) && logService.insertLog(log)) {            
                            response = "SUCCESS";
                        }
                    }
                }
            }
        } else {
            if (nobleService.deleteNoble(idx) && logService.insertLog(log)) {
                log.setWhat("노블 길드 탈퇴");
                response = "SUCCESS";
            }
        }
        return response;
    }

    @PostMapping("/Admin/updateAllDojang")
    public String updateAllDojang() throws IOException, InterruptedException {
        String response = "ERROR";

        List<Noble> nobleList = nobleService.getNobleForDojang();

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Log log = new Log();
        log.setWhat("무릉 갱신");
        log.setWhen((String)now.format(formatter));

        for (Noble noble : nobleList) {
            Map<String, Object> userInfo = crawlingService.getUserInfo(noble.getNickname());
            noble.setDojang((int)userInfo.get("dojang"));
            noble.setLevel((int)userInfo.get("level"));
            noble.setJob((String)userInfo.get("job"));
            nobleService.updateNoble(noble);
            if (noble.getNickname() == nobleList.get(nobleList.size() - 1).getNickname() && logService.insertLog(log)) {
                response = "SUCCESS";
            }
            Thread.sleep(500);
        }

        return response;
    }

    @PostMapping("/Admin/getCenturyList")
    public List<Century> getCenturyList(@RequestBody Map<String, Object> param) {
        Map<String, Object> searchParam = new HashMap<>();
        
        String searchStr = param.get("searchStr") != null ? (String)param.get("searchStr") : "";
        String order = param.get("order") != null ? (String)param.get("order") : "idx ASC";
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        searchParam.put("searchStr", searchStr);
        searchParam.put("order", order);
        searchParam.put("offset", commonService.offset(page));

        List<Century> centuryList = centuryService.getCenturyList(searchParam);

        return centuryList;
    }

    @PostMapping("/Admin/getCentury")
    public Century getCentury(@RequestBody Map<String, Object> param) {
        int idx = param.get("idx") != null ? (int)param.get("idx") : 0;

        return centuryService.getCentury(idx);
    }

    @PostMapping("/Admin/insertCentury")
    public String insertCentury(@RequestBody Century century) throws IOException, InterruptedException {
        String response = "ERROR";

        Map<String, Object> userInfo = crawlingService.getUserInfo(century.getNickname());
        century.setLevel((int)userInfo.get("level"));
        century.setJob((String)userInfo.get("job"));
        
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Log log = new Log();
        log.setNickname(century.getNickname());
        log.setWhat("20세기 길드 가입");
        log.setWhen((String)now.format(formatter));
        log.setWho(century.getGrantor());

        if (centuryService.insertCentury(century) && logService.insertLog(log)) {
            response = "SUCCESS";
        }

        return response;
    }

    @PostMapping("/Admin/updateCentury")
    public String updateCentury(@RequestBody Century century) throws IOException, InterruptedException {
        String response = "ERROR";

        Map<String, Object> userInfo = crawlingService.getUserInfo(century.getNickname());
        century.setLevel((int)userInfo.get("level"));
        century.setJob((String)userInfo.get("job"));

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Log log = new Log();
        log.setNickname(century.getNickname());
        log.setWhat("정보 수정");
        log.setWhen((String)now.format(formatter));
        log.setWho(century.getGrantor());
        
        if (centuryService.updateCentury(century) && logService.insertLog(log)) {
            response = "SUCCESS";
        }

        return response;
    }

    @PostMapping("/Admin/deleteCentury")
    public String deleteCentury(@RequestBody Map<String, Object> param) {
        String response = "ERROR";

        int idx = param.get("idx") != null ? (int)param.get("idx") : -1;
        String reason = param.get("reason") != null ? (String)param.get("reason") : "";

        Century century = centuryService.getCentury(idx);
        
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        Log log = new Log();
        log.setNickname(century.getNickname());
        log.setWhat("20세기 길드 탈퇴");
        log.setReason(reason);
        log.setWhen((String)now.format(formatter));
        log.setWho(century.getGrantor());

        if (centuryService.deleteCentury(idx) && logService.insertLog(log)) {
            response = "SUCCESS";
        }

        return response;
    }

    @PostMapping("/Admin/getDotaxList")
    public List<Dotax> getDotaxList(@RequestBody Map<String, Object> param) {
        Map<String, Object> searchParam = new HashMap<>();
        
        String searchStr = param.get("searchStr") != null ? (String)param.get("searchStr") : "";
        String order = param.get("order") != null ? (String)param.get("order") : "idx ASC";
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        searchParam.put("searchStr", searchStr);
        searchParam.put("order", order);
        searchParam.put("offset", commonService.offset(page));

        List<Dotax> dotaxList = dotaxService.getDotaxList(searchParam);

        return dotaxList;
    }

    @PostMapping("/Admin/getDotax")
    public Dotax getDotax(@RequestBody Map<String, Object> param) {
        int idx = param.get("idx") != null ? (int)param.get("idx") : 0;

        return dotaxService.getDotax(idx);
    }

    @PostMapping("/Admin/insertDotax")
    public String insertDotax(@RequestBody Dotax dotax) throws IOException, InterruptedException {
        String response = "ERROR";

        Map<String, Object> userInfo = crawlingService.getUserInfo(dotax.getNickname());
        dotax.setLevel((int)userInfo.get("level"));
        dotax.setJob((String)userInfo.get("job"));
        
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        Log log = new Log();
        log.setNickname(dotax.getNickname());
        log.setWhat("도탁스 길드 가입");
        log.setWhen((String)now.format(formatter));
        log.setWho(dotax.getGrantor());

        if (dotaxService.insertDotax(dotax) && logService.insertLog(log)) {
            response = "SUCCESS";
        }

        return response;
    }

    @PostMapping("/Admin/updateDotax")
    public String updateDotax(@RequestBody Dotax dotax) throws IOException, InterruptedException {
        String response = "ERROR";

        Map<String, Object> userInfo = crawlingService.getUserInfo(dotax.getNickname());
        dotax.setLevel((int)userInfo.get("level"));
        dotax.setJob((String)userInfo.get("job"));

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        Log log = new Log();
        log.setNickname(dotax.getNickname());
        log.setWhat("정보 수정");
        log.setWhen((String)now.format(formatter));
        log.setWho(dotax.getGrantor());
        
        if (dotaxService.updateDotax(dotax) && logService.insertLog(log)) {
            response = "SUCCESS";
        }

        return response;
    }

    @PostMapping("/Admin/deleteDotax")
    public String deleteDotax(@RequestBody Map<String, Object> param) {
        String response = "ERROR";

        int idx = param.get("idx") != null ? (int)param.get("idx") : -1;
        String reason = param.get("reason") != null ? (String)param.get("reason") : "";

        Dotax dotax = dotaxService.getDotax(idx);

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        Log log = new Log();
        log.setNickname(dotax.getNickname());
        log.setWhat("도탁스 길드 탈퇴");
        log.setReason(reason);
        log.setWhen((String)now.format(formatter));
        log.setWho(dotax.getGrantor());

        if (dotaxService.deleteDotax(idx) && logService.insertLog(log)) {
            response = "SUCCESS";
        }
        
        return response;
    }

    @PostMapping("/Admin/manageList")
    public Map<String, Object> manageList() throws IOException {
        Map<String, Object> response = new HashMap<>();

        List<String> nobleList = nobleService.getNobleNickname();
        List<String> centuryList = centuryService.getCenturyNickname();
        List<String> dotaxList = dotaxService.getDotaxNickname();
        
        List<String> backUpNoble = nobleService.getNobleNickname();
        List<String> backUpCentury = centuryService.getCenturyNickname();
        List<String> backUpDotax = dotaxService.getDotaxNickname();
        
        List<String> nobleRealList = crawlingService.crawlingNobleNickname();
        List<String> centuryRealList = crawlingService.crawlingCenturyNickname();
        List<String> dotaxRealList = crawlingService.crawlingDotaxNickname();

        nobleList.removeAll(nobleRealList);
        centuryList.removeAll(centuryRealList);
        dotaxList.removeAll(dotaxRealList);

        nobleRealList.removeAll(backUpNoble);
        centuryRealList.removeAll(backUpCentury);
        dotaxRealList.removeAll(backUpDotax);
        
        response.put("nobleSheet", nobleList);
        response.put("centurySheet", centuryList);
        response.put("dotaxSheet", dotaxList);
        response.put("nobleGame", nobleRealList);
        response.put("centuryGame", centuryRealList);
        response.put("dotaxGame", dotaxRealList);
        
        return response;
    }
}
