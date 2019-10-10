package com.zxl.parser.dataobjectbuilder;

import com.zxl.parser.dataobject.BaseDataObject;
import com.zxl.parser.dataobject.EvDataObject;
import com.zxl.parser.utils.ColumnReader;
import com.zxl.preparser.PreParsedLog;

import java.util.ArrayList;
import java.util.List;

import static com.zxl.parser.utils.ParseUtils.isNullOrEmptyOrDash;
import static com.zxl.parser.utils.UrlParseUtils.getInfoFromUrl;

public class EvDataObjectBuilder extends AbstractDataObjectBuilder {

    @Override
    public String getCommond() {
        return "ev";
    }

    public List<BaseDataObject> doBuildDataObjects(PreParsedLog preParsedLog) {
        List<BaseDataObject> baseDataObjects = new ArrayList<>();
        // 1.解析并填充公共字段
        EvDataObject evDataObject = new EvDataObject();
        ColumnReader columnReader = new ColumnReader(preParsedLog.getQueryString());
        fillCommonBaseDataObjectValue(evDataObject, preParsedLog, columnReader);

        // 2.解析并填充特有字段
        evDataObject.setEventCategory(columnReader.getStringValue("eca"));
        evDataObject.setEventAction(columnReader.getStringValue("eac"));
        evDataObject.setEventLabel(columnReader.getStringValue("ela"));
        String eva = columnReader.getStringValue("eva");
        if (!isNullOrEmptyOrDash(eva)) {
            evDataObject.setEventValue(Float.parseFloat(eva));
        }
        evDataObject.setUrl(columnReader.getStringValue("gsurl"));
        evDataObject.setOriginalUrl(columnReader.getStringValue("gsourl"));
        evDataObject.setTitle(columnReader.getStringValue("gstl"));
        evDataObject.setHostDomain(getInfoFromUrl(evDataObject.getUrl()).getDomain());
        baseDataObjects.add(evDataObject);
        return baseDataObjects;
    }
}
