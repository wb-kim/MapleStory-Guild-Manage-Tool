package com.noble.noble.API;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        String order = param.get("order") != null ? (String)param.get("order") : "create_dt DESC";
        int type = param.get("type") != null ? (int)param.get("type") : 1;
        searchParam.put("searchStr", searchStr);
        searchParam.put("order", order);
        searchParam.put("type", type);

        return logService.getLogList(searchParam);
    }

    @PostMapping("/Admin/getNobleList")
    public List<Noble> getNobleList(@RequestBody Map<String, Object> param) {
        Map<String, Object> searchParam = new HashMap<>();
        
        String searchStr = param.get("searchStr") != null ? (String)param.get("searchStr") : "";
        String order = param.get("order") != null ? (String)param.get("order") : "idx ASC";

        searchParam.put("searchStr", searchStr);
        searchParam.put("type", 1);
        searchParam.put("order", order);

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

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Log log = new Log();
        log.setNickname(noble.getNickname());
        Dotax dotax = new Dotax();
        if (noble.getDotax() == 1) {
            log.setWhat("도탁스 길드 가입(본캐)");  
            dotax.setNickname(noble.getNickname());
            dotax.setLevel((int)userInfo.get("level"));
            dotax.setJob((String)userInfo.get("job"));
            dotax.setGrantor(noble.getGrantor());
            dotaxService.insertDotax(dotax);  
        } else {
            log.setWhat("노블 길드 가입");
        }
        log.setCreateDt((String)now.format(formatter));
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

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Log log = new Log();
        log.setNickname(noble.getNickname());
        log.setWhat("정보 수정");
        log.setCreateDt((String)now.format(formatter));
        log.setWho(noble.getGrantor());
        
        if (nobleService.updateNoble(noble) && logService.insertLog(log)) {
            response = "SUCCESS";
        }

        return response;
    }

    @PostMapping("/Admin/useExempt")
    public String useExempt(@RequestBody Map<String, Object> param) throws IOException, InterruptedException {
        String response = "ERROR";

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        param.put("exemptedDate", ((String)now.format(formatter)));

        Noble noble = nobleService.getNoble((int)param.get("idx"));

        Log log = new Log();
        log.setWhat("면제권 사용");
        log.setNickname(noble.getNickname());
        log.setCreateDt((String)now.format(formatter));
        log.setWho((String)param.get("grantor"));
        
        if (nobleService.useExempt(param) && logService.insertLog(log)) {
            response = "SUCCESS";
        }

        return response;
    }

    @PostMapping("/Admin/warning")
    public String warning(@RequestBody Map<String, Object> param) throws IOException, InterruptedException {
        String response = "ERROR";

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Noble noble = nobleService.getNoble((int)param.get("idx"));

        Log log = new Log();
        log.setWhat("경고");
        log.setNickname(noble.getNickname());
        log.setCreateDt((String)now.format(formatter));
        log.setWho((String)param.get("grantor"));
        log.setReason((String)param.get("reason"));
        
        if (nobleService.warning((int)param.get("idx")) && logService.insertLog(log)) {
            response = "SUCCESS";
        }

        return response;
    }

    @PostMapping("/Admin/deleteNoble")
    public String deleteNoble(@RequestBody Map<String, Object> param) {
        String response = "ERROR";

        int idx = param.get("idx") != null ? (int)param.get("idx") : -1;
        String reason = param.get("reason") != null ? (String)param.get("reason") : "";
        String grantor = param.get("grantor") != null ? (String)param.get("grantor") : "";

        Noble noble = nobleService.getNoble(idx);

        if (noble.getAdmin() == 1) {
            response = "ADMIN";
        } else {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            Log log = new Log();
            log.setNickname(noble.getNickname());
            log.setCreateDt((String)now.format(formatter));
            log.setWho(grantor);
            log.setReason(reason);

            log.setWhat("노블 길드 탈퇴");
            if (logService.insertLog(log) && nobleService.deleteNoble(idx)) {
                response = "SUCCESS";
            }
        }
        
        return response;
    }

    @PostMapping("/Admin/updateAllDojang")
    public String updateAllDojang() throws IOException, InterruptedException {
        String response = "ERROR";

        List<Noble> nobleList = nobleService.getNobleForDojang();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Log log = new Log();
        log.setWhat("무릉 갱신");
        log.setCreateDt((String)now.format(formatter));

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
        

        searchParam.put("searchStr", searchStr);
        searchParam.put("order", order);

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
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Log log = new Log();
        log.setNickname(century.getNickname());
        log.setWhat("20세기 길드 가입");
        log.setCreateDt((String)now.format(formatter));
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

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Log log = new Log();
        log.setNickname(century.getNickname());
        log.setWhat("정보 수정");
        log.setCreateDt((String)now.format(formatter));
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
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        Log log = new Log();
        log.setNickname(century.getNickname());
        log.setWhat("20세기 길드 탈퇴");
        log.setReason(reason);
        log.setCreateDt((String)now.format(formatter));
        log.setWho((String)param.get("grantor"));

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
        

        searchParam.put("searchStr", searchStr);
        searchParam.put("order", order);

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
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        Log log = new Log();
        log.setNickname(dotax.getNickname());
        log.setWhat("도탁스 길드 가입");
        log.setCreateDt((String)now.format(formatter));
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
        System.out.println((String)userInfo.get("job"));
        dotax.setLevel((int)userInfo.get("level"));
        dotax.setJob((String)userInfo.get("job"));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        Log log = new Log();
        log.setNickname(dotax.getNickname());
        log.setWhat("정보 수정");
        log.setCreateDt((String)now.format(formatter));
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

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        Log log = new Log();
        log.setNickname(dotax.getNickname());
        log.setWhat("도탁스 길드 탈퇴");
        log.setReason(reason);
        log.setCreateDt((String)now.format(formatter));
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

    @PostMapping("/Admin/getNoNobleList")
    public List<Map<String, Object>> getNoNobleList() {
        List<Map<String, Object>> response = new ArrayList<>();

        Map<String, Object> searchParam = new HashMap<>();

        searchParam.put("searchStr", "");
        searchParam.put("order", "idx ASC");

        List<Noble> nobleList = nobleService.getNobleListForTotal(searchParam);
        List<String> nobleNicknameList = new ArrayList<>();
        for (Noble noble : nobleList) {
            if (noble.getDotax() == 1) {
                nobleNicknameList.add(noble.getNickname() + "(도탁스)");        
            } else {
                nobleNicknameList.add(noble.getNickname());
            }
        }

        for (Noble noble : nobleList) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("main", noble.getNickname());
            temp.put("admin", noble.getAdmin());
            temp.put("sub", nobleService.getNobleSubList(noble.getNickname()));
            List<String> centuryList = centuryService.getCenturyListFromMain(noble.getNickname());
            temp.put("century", centuryList);
            List<String> centuryUpperList = centuryService.getCenturyUpperListFromMain(noble.getNickname());
            temp.put("centuryUpper", centuryUpperList);
            List<String> dotaxList = dotaxService.getDotaxListFromMain(noble.getNickname());
            temp.put("dotax", dotaxList);
            List<String> dotaxUpperList = dotaxService.getDotaxUpperListFromMain(noble.getNickname());
            temp.put("dotaxUpper", dotaxUpperList);
            temp.put("exemptedDate", noble.getExemptedDate());
            if ((centuryList.size() < 3 && centuryUpperList.size() > 0) || (dotaxList.size() < 1 && dotaxUpperList.size() > 0)) {
                response.add(temp);
            }
        }

        return response;
    }
}
