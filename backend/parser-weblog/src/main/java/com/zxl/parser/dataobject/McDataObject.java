package com.zxl.parser.dataobject;

public class McDataObject extends BaseDataObject {
    //点击页面维度
    private String url;
    private String originalUrl;
    private String pageHostName;
    private String pageTitle;
    private String pageVersion;
    //点击位置维度
    private int clickX;
    private int clickY;
    private int pageRegion;
    private int snapshotId;
    private String clickScreenResolution;
    //点击链接位置维度
    private String linkText;
    private String linkUrl;
    private boolean isLinkClicked;
    private String linkHostName;
    private int linkX;
    private int linkY;
    private int linkWidth;
    private int linkHeight;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getPageHostName() {
        return pageHostName;
    }

    public void setPageHostName(String pageHostName) {
        this.pageHostName = pageHostName;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageVersion() {
        return pageVersion;
    }

    public void setPageVersion(String pageVersion) {
        this.pageVersion = pageVersion;
    }

    public int getClickX() {
        return clickX;
    }

    public void setClickX(int clickX) {
        this.clickX = clickX;
    }

    public int getClickY() {
        return clickY;
    }

    public void setClickY(int clickY) {
        this.clickY = clickY;
    }

    public int getPageRegion() {
        return pageRegion;
    }

    public void setPageRegion(int pageRegion) {
        this.pageRegion = pageRegion;
    }

    public int getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(int snapshotId) {
        this.snapshotId = snapshotId;
    }

    public String getClickScreenResolution() {
        return clickScreenResolution;
    }

    public void setClickScreenResolution(String clickScreenResolution) {
        this.clickScreenResolution = clickScreenResolution;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public boolean isLinkClicked() {
        return isLinkClicked;
    }

    public void setLinkClicked(boolean linkClicked) {
        isLinkClicked = linkClicked;
    }

    public String getLinkHostName() {
        return linkHostName;
    }

    public void setLinkHostName(String linkHostName) {
        this.linkHostName = linkHostName;
    }

    public int getLinkX() {
        return linkX;
    }

    public void setLinkX(int linkX) {
        this.linkX = linkX;
    }

    public int getLinkY() {
        return linkY;
    }

    public void setLinkY(int linkY) {
        this.linkY = linkY;
    }

    public int getLinkWidth() {
        return linkWidth;
    }

    public void setLinkWidth(int linkWidth) {
        this.linkWidth = linkWidth;
    }

    public int getLinkHeight() {
        return linkHeight;
    }

    public void setLinkHeight(int linkHeight) {
        this.linkHeight = linkHeight;
    }
}
