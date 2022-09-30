package com.noble.noble.data;

import lombok.Data;

@Data
public class Log {
    private int idx = -1;
    private String nickname = "";
    private String what = "";
    private String reason = "";
    private String who = "";
    private String createDt = "";
}
