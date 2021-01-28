package elo;

public enum Maps {
    Ascent("Ascent", "Ascent"),
    Bind("Duality", "Bind"),
    Icebox("Port", "Icebox"),
    Haven("Triad", "Haven"),
    Split("Bonsai", "Split");

    private String cn;
    private String rn;

    Maps(String codeName, String realName) {
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
