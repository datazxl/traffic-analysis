package com.zxl.parser.dataobject.dim;

/**
 * 来源信息类
 *  https://www.baidu.com/link?url=fRlsUPZu1cJZ88BfW725L_c81B1G_roiDbQ9oNnhlDF3VfjE6FGAfSa1PLOx7ialgsgvAmftuGN7s6pbUpP0BK&wd=&eqid=ead342b60003ccd3000000035b3b492a
 */
public class ReferrerInfo {
    private String url;
    private String domain;
    private String query;
    private String urlWithoutQuery;
    private String channel;
    private String referType;
    private String searchEngineName;
    private String keyword;
    private String eqId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getReferType() {
        return referType;
    }

    public void setReferType(String referType) {
        this.referType = referType;
    }

    public String getSearchEngineName() {
        return searchEngineName;
    }

    public void setSearchEngineName(String searchEngineName) {
        this.searchEngineName = searchEngineName;
    }

    public String getEqId() {
        return eqId;
    }

    public void setEqId(String eqId) {
        this.eqId = eqId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUrlWithoutQuery() {
        return urlWithoutQuery;
    }

    public void setUrlWithoutQuery(String urlWithoutQuery) {
        this.urlWithoutQuery = urlWithoutQuery;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
