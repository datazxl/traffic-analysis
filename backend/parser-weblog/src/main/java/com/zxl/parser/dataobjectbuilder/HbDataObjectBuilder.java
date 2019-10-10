package com.zxl.parser.dataobjectbuilder;

import com.zxl.parser.dataobject.BaseDataObject;
import com.zxl.parser.dataobject.HbDataObject;
import com.zxl.parser.utils.ColumnReader;
import com.zxl.preparser.PreParsedLog;

import java.util.ArrayList;
import java.util.List;

import static com.zxl.parser.utils.ParseUtils.isNullOrEmptyOrDash;

public class HbDataObjectBuilder extends AbstractDataObjectBuilder {
    @Override
    public String getCommond() {
        return "hb";
    }

    @Override
    public List<BaseDataObject> doBuildDataObjects(PreParsedLog preParsedLog) {
        List<BaseDataObject> baseDataObjects = new ArrayList<>();
        //1.解析并填充公共字段
        HbDataObject hbDataObject = new HbDataObject();
        ColumnReader columnReader = new ColumnReader(preParsedLog.getQueryString());
        fillCommonBaseDataObjectValue(hbDataObject, preParsedLog, columnReader);
        //2.解析并填充特有字段
        int loadingDuration = 0;
        String plt = columnReader.getStringValue("plt");
        if (!isNullOrEmptyOrDash(plt)) {
            loadingDuration = Math.round(Float.parseFloat(plt));
        }
        hbDataObject.setLoadingDuration(loadingDuration);

        int clientPageDuration = 0;
        String psd = columnReader.getStringValue("psd");
        if (!isNullOrEmptyOrDash(psd)) {
            clientPageDuration = Math.round(Float.parseFloat(psd));
        }
        hbDataObject.setClientPageDuration(clientPageDuration);

        baseDataObjects.add(hbDataObject);
        return baseDataObjects;
    }
}
