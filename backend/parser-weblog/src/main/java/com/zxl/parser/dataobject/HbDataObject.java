package com.zxl.parser.dataobject;

public class HbDataObject extends BaseDataObject {
    private int clientPageDuration;
    private int loadingDuration;

    public int getClientPageDuration() {
        return clientPageDuration;
    }

    public void setClientPageDuration(int clientPageDuration) {
        this.clientPageDuration = clientPageDuration;
    }

    public int getLoadingDuration() {
        return loadingDuration;
    }

    public void setLoadingDuration(int loadingDuration) {
        this.loadingDuration = loadingDuration;
    }
}
