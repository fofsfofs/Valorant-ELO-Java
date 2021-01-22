package elo;

public enum Agents {
    BREACH("5F8D3A7F-467B-97F3-062C-13ACF203C006", "Breach"),
    RAZE("F94C3B30-42BE-E959-889C-5AA313DBA261", "Raze"),
    SKYE("6F2A04CA-43E0-BE17-7F36-B3908627744D", "Skye"),
    CYPHER("117ED9E3-49F3-6512-3CCF-0CADA7E3823B", "Cypher"),
    SOVA("320B2A48-4D9B-A075-30F1-1F93A9B638FA", "Sova"),
    KILLJOY("1E58DE9C-4950-5125-93E9-A0AEE9F98746", "Killjoy"),
    VIPER("707EAB51-4836-F488-046A-CDA6BF494859", "Viper"),
    PHOENIX("EB93336A-449B-9C1B-0A54-A891F7921D69", "Phoenix"),
    BRIMSTONE("9F0D8BA9-4140-B941-57D3-A7AD57C6B417", "Brimstone"),
    YORU("7F94D92C-4234-0A36-9646-3A87EB8B5C89", "Yoru"),
    SAGE("569FDD95-4D10-43AB-CA70-79BECC718B46", "Sage"),
    REYNA("A3BFB853-43B2-7238-A4F1-AD90E9E46BCC", "Reyna"),
    OMEN("8E253930-4C05-31DD-1B6C-968525494517", "Omen"),
    JETT("ADD6443A-41BD-E414-F6AD-E58D267F4E95", "Jett");

    private String cn;
    private String rn;

    Agents(String codeName, String realName) {
        this.cn = codeName;
        this.rn = realName;
    }

    public String getCodeName() {
        return cn;
    }

    public String getRealName() {
        return rn;
    }
}
