package com.noble.noble.data;

import lombok.Data;

@Data
public class Noble {
    private int idx = -1;
    private String nickname = "";
    private int admin = -1;
    private String mainChar = "";
    private int level = -1;
    private String job = "";
    private int dojang = -1;
    private String exemptedDate;
    private int dotax = 0;
    private int warn = 0;
    private int dojangAgree = -1;
    private String grantor = "";
}
