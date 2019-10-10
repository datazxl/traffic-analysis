package com.zxl.parser.dataobject;

import com.zxl.parser.dataobject.dim.TargetPageInfo;
import java.util.List;

/**
 * 目标页面实体
 */
public class TargetPageDataObject extends BaseDataObject {

    private PvDataObject pvDataObject;//该pv信息
    private List<TargetPageInfo> targetPagesInfos; //目标页面信息

    public List<TargetPageInfo> getTargetPagesInfos() {
        return targetPagesInfos;
    }

    public void setTargetPagesInfo(List<TargetPageInfo> targetPagesInfos) {
        this.targetPagesInfos = targetPagesInfos;
    }

    public PvDataObject getPvDataObject() {
        return pvDataObject;
    }

    public void setPvDataObject(PvDataObject pvDataObject) {
        this.pvDataObject = pvDataObject;
    }
}