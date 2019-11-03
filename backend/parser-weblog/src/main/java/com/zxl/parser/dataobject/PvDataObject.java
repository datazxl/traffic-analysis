package com.zxl.parser.dataobject;

import com.zxl.parser.dataobject.dim.AdInfo;
import com.zxl.parser.dataobject.dim.BrowserInfo;
import com.zxl.parser.dataobject.dim.ReferrerInfo;
import com.zxl.parser.dataobject.dim.SiteResourceInfo;

public class PvDataObject extends BaseDataObject {
    private SiteResourceInfo siteResourceInfo;//网页信息
    private ReferrerInfo referrerInfo;//来源信息
    private BrowserInfo browserInfo;//浏览器信息
    private AdInfo adInfo;//广告信息
    private int duration;//当前页面的停留时间，单位为s

    /**
     * 判断当前PV是不是广告进来的
     * @return
     */
    public boolean isPaid(){
        return adInfo.isPaid();
    }
    /**
     * 判断当前PV的其他PV是否相同（是否是刷新的产生的）
     * @param other
     * @return
     */
    public boolean isDifferentFrom(PvDataObject other) {
        if (other == null){
            return true;
        }
        //如果当前PV和另一个PV的url以及referUrl都相同，那么后一个Pv是刷新来的。
        return !(this.siteResourceInfo.getUrl() == other.siteResourceInfo.getUrl() &&
                this.referrerInfo.getUrl() == other.referrerInfo.getUrl());
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public SiteResourceInfo getSiteResourceInfo() {
        return siteResourceInfo;
    }

    public void setSiteResourceInfo(SiteResourceInfo siteResourceInfo) {
        this.siteResourceInfo = siteResourceInfo;
    }

    public ReferrerInfo getReferrerInfo() {
        return referrerInfo;
    }

    public void setReferrerInfo(ReferrerInfo referrerInfo) {
        this.referrerInfo = referrerInfo;
    }

    public BrowserInfo getBrowserInfo() {
        return browserInfo;
    }

    public void setBrowserInfo(BrowserInfo browserInfo) {
        this.browserInfo = browserInfo;
    }

    public AdInfo getAdInfo() {
        return adInfo;
    }

    public void setAdInfo(AdInfo adInfo) {
        this.adInfo = adInfo;
    }
}
