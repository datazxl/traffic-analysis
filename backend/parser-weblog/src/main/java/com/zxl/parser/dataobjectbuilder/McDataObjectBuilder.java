package com.zxl.parser.dataobjectbuilder;

import com.zxl.parser.dataobject.BaseDataObject;
import com.zxl.parser.dataobject.McDataObject;
import com.zxl.parser.utils.ColumnReader;
import com.zxl.preparser.PreParsedLog;

import java.util.ArrayList;
import java.util.List;

import static com.zxl.parser.utils.ParseUtils.isNullOrEmptyOrDash;
import static com.zxl.parser.utils.UrlParseUtils.getInfoFromUrl;

public class McDataObjectBuilder extends AbstractDataObjectBuilder {
    private static int clickXBoundary = 10000;
    private static int clickYBoundary = 100000;

    @Override
    public String getCommond() {
        return "mc";
    }

    @Override
    public List<BaseDataObject> doBuildDataObjects(PreParsedLog preParsedLog) {
        List<BaseDataObject> baseDataObjects = new ArrayList<>();

        // 1.解析并填充公共字段
        McDataObject mcDataObject = new McDataObject();
        ColumnReader reader = new ColumnReader(preParsedLog.getQueryString());
        fillCommonBaseDataObjectValue(mcDataObject, preParsedLog, reader);

        // 2.解析并填充特有字段
        // 2.1 点击页面维度
        mcDataObject.setUrl(reader.getStringValue("gsmcurl"));
        mcDataObject.setOriginalUrl(reader.getStringValue("gsorurl"));
        mcDataObject.setPageTitle(reader.getStringValue("gstl"));
        mcDataObject.setPageHostName(getInfoFromUrl(reader.getStringValue("gsmcurl")).getDomain());
        mcDataObject.setPageVersion(reader.getStringValue("pageVersion"));
        // 2.2 点击位置维度
        mcDataObject.setClickX(Integer.parseInt(reader.getStringValue("gsmcoffsetx")));
        mcDataObject.setClickY(Integer.parseInt(reader.getStringValue("gsmcoffsety")));
        mcDataObject.setPageRegion(getIntValue("re", reader));
        mcDataObject.setSnapshotId(getIntValue("gssn", reader));
        mcDataObject.setClickScreenResolution(reader.getStringValue("gsscr"));
        // 2.3 点击链接位置维度
        mcDataObject.setLinkText(reader.getStringValue("lt"));
        mcDataObject.setLinkUrl(reader.getStringValue("lk"));
        if (!isNullOrEmptyOrDash(mcDataObject.getLinkText()) ||
                !isNullOrEmptyOrDash(mcDataObject.getLinkUrl())) {
            mcDataObject.setLinkClicked(true);
        } else {
            mcDataObject.setLinkClicked(false);
        }
        mcDataObject.setLinkHostName(getInfoFromUrl(mcDataObject.getLinkUrl()).getDomain());
        mcDataObject.setLinkX(getValidClickXYPoint(getIntValue("lx", reader), clickXBoundary, -1 * clickXBoundary));
        mcDataObject.setLinkY(getValidClickXYPoint(getIntValue("ly", reader), clickYBoundary, 0));
        mcDataObject.setLinkHeight(getIntValue("lh", reader));
        mcDataObject.setLinkWidth(getIntValue("lw", reader));

        baseDataObjects.add(mcDataObject);
        return baseDataObjects;
    }

    private Integer getIntValue(String key, ColumnReader columnReader) {
        String value = columnReader.getStringValue(key);
        if (!isNullOrEmptyOrDash(value)) {
            return Integer.parseInt(value);
        } else {
            return 0;
        }
    }

    private int getValidClickXYPoint(int point, int maxBoundaryValue,
                                     int minBoundaryValue) {
        if (point < minBoundaryValue) {
            return minBoundaryValue;
        } else if (point > maxBoundaryValue) {
            return maxBoundaryValue;
        } else {
            return point;
        }
    }
}
