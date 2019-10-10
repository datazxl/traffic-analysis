package com.zxl.parser.dataobject.dim;


import static com.zxl.parser.utils.ParseUtils.isNullOrEmptyOrDash;

/**
 * 广告信息类
 */
public class AdInfo {
    private String utmCampaign = "-";
    private String utmContent = "-";
    private String utmTerm = "-";
    private String utmSource = "-";
    private String utmMedium = "-";
    //额外加的两个广告参数
    private String utmAdGroup = "-";
    private String utmChannel = "-";

    /**
     *  判断是否为付费流量
     *  七大广告参数如果有一个值不为空的话，则是付费流量
     * @return
     */
    public boolean isPaid() {
        return !isNullOrEmptyOrDash(utmCampaign) || !isNullOrEmptyOrDash(utmContent) ||
                !isNullOrEmptyOrDash(utmTerm) || !isNullOrEmptyOrDash(utmSource) ||
                !isNullOrEmptyOrDash(utmAdGroup) || !isNullOrEmptyOrDash(utmChannel) ||
                !isNullOrEmptyOrDash(utmMedium);
    }

    public String getUtmCampaign() {
        return utmCampaign;
    }

    public void setUtmCampaign(String utmCampaign) {
        this.utmCampaign = utmCampaign;
    }

    public String getUtmContent() {
        return utmContent;
    }

    public void setUtmContent(String utmContent) {
        this.utmContent = utmContent;
    }

    public String getUtmTerm() {
        return utmTerm;
    }

    public void setUtmTerm(String utmTerm) {
        this.utmTerm = utmTerm;
    }

    public String getUtmSource() {
        return utmSource;
    }

    public void setUtmSource(String utmSource) {
        this.utmSource = utmSource;
    }

    public String getUtmAdGroup() {
        return utmAdGroup;
    }

    public void setUtmAdGroup(String utmAdGroup) {
        this.utmAdGroup = utmAdGroup;
    }

    public String getUtmChannel() {
        return utmChannel;
    }

    public void setUtmChannel(String utmChannel) {
        this.utmChannel = utmChannel;
    }

    public String getUtmMedium() {
        return utmMedium;
    }

    public void setUtmMedium(String utmMedium) {
        this.utmMedium = utmMedium;
    }
}
