package com.github.tvbox.osc.bean;

public class SearchResultWrapper {
    private String wd;
    private AbsXml data;
    /**
     * @return the wd
     */
    public String getWd() {
        return wd;
    }
    /**
     * @param wd the wd to set
     */
    public void setWd(String wd) {
        this.wd = wd;
    }
    /**
     * @return the data
     */
    public AbsXml getData() {
        return data;
    }
    /**
     * @param data the data to set
     */
    public void setData(AbsXml data) {
        this.data = data;
    }    

    
}