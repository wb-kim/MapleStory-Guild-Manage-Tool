package com.noble.noble.data;

import lombok.Data;

@Data
public class Log {
    private int idx;
    private String nickname;
    private String what;
    private String reason;
    private String who;
    private String when;
}
