package com.zxl.parser.dataobjectbuilder;

import com.zxl.parser.dataobject.BaseDataObject;
import com.zxl.parser.iplocation.ParserIpLocation;
import com.zxl.parser.utils.ColumnReader;
import com.zxl.preparser.PreParsedLog;
import eu.bitwalker.useragentutils.UserAgent;

import java.util.List;

public abstract class AbstractDataObjectBuilder {

    public abstract String getCommond();

    public abstract List<BaseDataObject> doBuildDataObjects(PreParsedLog preParsedLog);


    /**
     * 解析公共的字段到BaseDataObject
     * @param baseDataObject 存放数据对象
     * @param preParsedLog 存放原始数据对象
     * @param columnReader 存放解析后的queryString map
     */
    public void fillCommonBaseDataObjectValue(BaseDataObject baseDataObject, PreParsedLog preParsedLog, ColumnReader columnReader){
        baseDataObject.setProfileId(preParsedLog.getProfileId());
        baseDataObject.setServerTimeString(preParsedLog.getServerTime());

        baseDataObject.setUserId(columnReader.getStringValue("gsuid"));
        baseDataObject.setTrackerVersion(columnReader.getStringValue("gsver"));
        baseDataObject.setPvId(columnReader.getStringValue("pvid"));
        baseDataObject.setCommand(columnReader.getStringValue("gscmd"));

        //解析ip位置信息
        baseDataObject.setClientIp(preParsedLog.getClientIp());
        baseDataObject.setIpLocation(ParserIpLocation.parse(preParsedLog.getClientIp()));
//        //解析UserAgent信息
        baseDataObject.setUserAgent(preParsedLog.getUserAgent());
        baseDataObject.setUserAgentInfo(UserAgent.parseUserAgentString(preParsedLog.getUserAgent()));
    }
}
