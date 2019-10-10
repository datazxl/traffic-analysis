package com.zxl.parser.dataobjectbuilder;

import com.zxl.parser.dataobject.BaseDataObject;
import com.zxl.parser.dataobject.PvDataObject;
import com.zxl.parser.dataobject.TargetPageDataObject;
import com.zxl.parser.dataobject.dim.*;
import com.zxl.parser.searchengine.ReferUrlAndParams;
import com.zxl.parser.searchengine.SearchEngineConfig;
import com.zxl.parser.searchengine.service.SearchEngineConfigService;
import com.zxl.parser.searchengine.service.impl.FileSearchEngineConfigService;
import com.zxl.parser.targetpage.TargetPage;
import com.zxl.parser.targetpage.service.TargetPageConfigService;
import com.zxl.parser.targetpage.service.impl.MongoTargetPageConfigService;
import com.zxl.parser.utils.ColumnReader;
import com.zxl.parser.utils.ParseUtils;
import com.zxl.parser.utils.UrlInfo;
import com.zxl.parser.utils.UrlParseUtils;
import com.zxl.preparser.PreParsedLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.zxl.parser.utils.ParseUtils.isNullOrEmptyOrDash;
import static com.zxl.parser.utils.UrlParseUtils.getInfoFromUrl;

public class PvDataObjectBuilder extends AbstractDataObjectBuilder {
    private SearchEngineConfigService searchEngineConfigService = FileSearchEngineConfigService.getInstance();
    private TargetPageConfigService targetPageConfigService = MongoTargetPageConfigService.getInstance();

    @Override
    public String getCommond() {
        return "pv";
    }

    @Override
    public List<BaseDataObject> doBuildDataObjects(PreParsedLog preParsedLog) {
        List<BaseDataObject> baseDataObjects = new ArrayList<>();
        //1.解析并填充公共字段
        PvDataObject pvDataObject = new PvDataObject();
        ColumnReader columnReader = new ColumnReader(preParsedLog.getQueryString());
        fillCommonBaseDataObjectValue(pvDataObject, preParsedLog, columnReader);

        //2.解析并填充特有字段
        //2.1网站信息的解析
        pvDataObject.setSiteResourceInfo(createSiteResourceInfo(columnReader));
        //2.2广告信息的解析
        pvDataObject.setAdInfo(createAdInfo(pvDataObject.getSiteResourceInfo().getQuery()));
        //2.3浏览器信息的解析
        pvDataObject.setBrowserInfo(createBrowserInfo(columnReader));
        //2.4来源信息的解析
        pvDataObject.setReferrerInfo(createReferrerInfo(columnReader, pvDataObject.getAdInfo()));

        //3.目标页面的解析
        TargetPageDataObject targetPageDataObject = populateTargetPage(pvDataObject, preParsedLog, columnReader);

        baseDataObjects.add(pvDataObject);
        if (targetPageDataObject != null){
            baseDataObjects.add(targetPageDataObject);
        }
        return baseDataObjects;
    }

    private TargetPageDataObject populateTargetPage(PvDataObject pvDataObject,
                                                    PreParsedLog preParsedLog, ColumnReader columnReader) {
        TargetPageDataObject targetPageDataObject = new TargetPageDataObject();
        //获取该pv所有匹配到的目标页面
        List<TargetPage> targetPages = targetPageConfigService.getMatchedTargetPages(pvDataObject.getProfileId(), pvDataObject.getSiteResourceInfo().getUrl());
        if (targetPages.size() != 0){
            //1. 解析并填充公共字段
            fillCommonBaseDataObjectValue(targetPageDataObject, preParsedLog, columnReader);
            //2. 解析并填充特有字段
            List<TargetPageInfo> targetPageInfos = new ArrayList<>();
            targetPages.forEach(new Consumer<TargetPage>() {
                @Override
                public void accept(TargetPage targetPage) {
                    TargetPageInfo targetPageInfo = new TargetPageInfo(targetPage.getId(),targetPage.getName(),targetPage.isEnable());
                    targetPageInfos.add(targetPageInfo);
                }
            });

            targetPageDataObject.setTargetPagesInfo(targetPageInfos);
            targetPageDataObject.setPvDataObject(pvDataObject);

            return targetPageDataObject;
        }
        return null;
    }

    private ReferrerInfo createReferrerInfo(ColumnReader columnReader, AdInfo adInfo) {
        ReferrerInfo referrerInfo = new ReferrerInfo();
        String referUrl = columnReader.getStringValue("gsref");
        if (referUrl == "-") {
            referrerInfo.setChannel("-");
            referrerInfo.setDomain("-");
            referrerInfo.setEqId("-");
            referrerInfo.setSearchEngineName("-");
            referrerInfo.setUrl("-");
            referrerInfo.setQuery("-");
            referrerInfo.setUrlWithoutQuery("-");
            referrerInfo.setKeyword("-");
        } else {
            UrlInfo urlInfo = getInfoFromUrl(referUrl);
            referrerInfo.setDomain(urlInfo.getDomain());
            referrerInfo.setUrl(urlInfo.getFullUrl());
            referrerInfo.setQuery(urlInfo.getQuery());
            referrerInfo.setUrlWithoutQuery(urlInfo.getUrlWithoutQuery());

            //1. 搜索引擎和关键词的解析
            //1.1、匹配搜索引擎配置
            Map<String, String> referParams = UrlParseUtils.getQueryParams(urlInfo.getQuery());
            ReferUrlAndParams referUrlAndParams = new ReferUrlAndParams(urlInfo.getUrlWithoutQuery(), referParams);
            SearchEngineConfig searchEngineConfig = searchEngineConfigService.doMatch(referUrlAndParams);

            //1.2、设置搜索引擎和关键词
            if (searchEngineConfig != null) {
                referrerInfo.setSearchEngineName(searchEngineConfig.getSearchEngineName());
                //如果配置的搜索引擎的关键词的key不会空的话，则需要从query参数中根据这个key拿到关键词
                if (searchEngineConfig.getSearchKeywordKey() != null && !"null".equals(searchEngineConfig.getSearchKeywordKey())) {
                    String keyword = referParams.getOrDefault(searchEngineConfig.getSearchKeywordKey(), "-");
                    referrerInfo.setKeyword(keyword);
                }
            } else {
                referrerInfo.setSearchEngineName("-");
                referrerInfo.setKeyword("-");
            }

            //1.3、设置eqid，只有百度搜索引擎有。
            if (referrerInfo.getQuery() != "-" &&
                    referrerInfo.getSearchEngineName().equalsIgnoreCase("baidu")) {
                referrerInfo.setEqId(referParams.getOrDefault("eqid", "-"));
            } else {
                referrerInfo.setEqId("-");
            }

            //2. 来源渠道的计算逻辑
            //先赋值为广告系列渠道，如果没有的话则赋值为搜索引擎，如果还没有的话则赋值为来源域名
            String adChannel = adInfo.getUtmChannel();
            if (!isNullOrEmptyOrDash(adChannel)) {
                referrerInfo.setChannel(adChannel);
            } else if (!isNullOrEmptyOrDash(referrerInfo.getSearchEngineName())) {
                referrerInfo.setChannel(referrerInfo.getSearchEngineName());
            } else {
                referrerInfo.setChannel(referrerInfo.getDomain());
            }

            //3. 来源类型计算逻辑
            if (!isNullOrEmptyOrDash(referrerInfo.getSearchEngineName())) {
                if (adInfo.isPaid()) {
                    //从搜索引擎中过来且是付费流量
                    referrerInfo.setReferType("paid search"); //付费搜索
                } else {
                    //从搜索引擎中过来但不是付费流量
                    referrerInfo.setReferType("organic search"); //自然搜索
                }
            } else if (!isNullOrEmptyOrDash(referrerInfo.getDomain())) {
                //从非搜索引擎的网站中过来
                referrerInfo.setReferType("referral"); //引荐，其实就是外部链接
            } else {
                //直接访问
                referrerInfo.setReferType("direct"); //直接访问
            }
        }
        return referrerInfo;
    }


    private BrowserInfo createBrowserInfo(ColumnReader columnReader) {
        BrowserInfo browserInfo = new BrowserInfo();
        browserInfo.setAlexaToolBar(ParseUtils.parseBoolean(columnReader.getStringValue("gsalexaver")));
        browserInfo.setBrowserLanguage(columnReader.getStringValue("gsbrlang"));
        browserInfo.setColorDepth(columnReader.getStringValue("gsclr"));
        browserInfo.setCookieEnable(ParseUtils.parseBoolean(columnReader.getStringValue("gsce")));
        browserInfo.setDeviceName(columnReader.getStringValue("dvn"));
        browserInfo.setDeviceType(columnReader.getStringValue("dvt"));
        browserInfo.setFlashVersion(columnReader.getStringValue("gsflver"));
        browserInfo.setJavaEnable(ParseUtils.parseBoolean(columnReader.getStringValue("gsje")));
        browserInfo.setOsLanguage(columnReader.getStringValue("gsoslang"));
        browserInfo.setResolution(columnReader.getStringValue("gsscr"));
        browserInfo.setSilverlightVersion(columnReader.getStringValue("gssil"));
        return browserInfo;
    }

    private AdInfo createAdInfo(String query) {
        AdInfo adInfo = new AdInfo();
        Map<String, String> landingParams = UrlParseUtils.getQueryParams(query);
        adInfo.setUtmCampaign(landingParams.getOrDefault("utm_campaign", "-"));
        adInfo.setUtmMedium(landingParams.getOrDefault("utm_medium", "-"));
        adInfo.setUtmContent(landingParams.getOrDefault("utm_content", "-"));
        adInfo.setUtmChannel(landingParams.getOrDefault("utm_channel", "-"));
        adInfo.setUtmTerm(landingParams.getOrDefault("utm_term", "-"));
        adInfo.setUtmSource(landingParams.getOrDefault("utm_source", "-"));
        adInfo.setUtmAdGroup(landingParams.getOrDefault("utm_adgroup", "-"));
        return adInfo;
    }

    private SiteResourceInfo createSiteResourceInfo(ColumnReader columnReader) {
        SiteResourceInfo siteResourceInfo = new SiteResourceInfo();
        UrlInfo urlInfo = getInfoFromUrl(columnReader.getStringValue("gsurl"));
        siteResourceInfo.setDomain(urlInfo.getDomain());
        siteResourceInfo.setQuery(urlInfo.getQuery());
        siteResourceInfo.setUrl(urlInfo.getFullUrl());
        siteResourceInfo.setPageTitle(columnReader.getStringValue("gstl"));
        siteResourceInfo.setOriginalUrl(columnReader.getStringValue("gsorurl"));
        return siteResourceInfo;
    }
}
