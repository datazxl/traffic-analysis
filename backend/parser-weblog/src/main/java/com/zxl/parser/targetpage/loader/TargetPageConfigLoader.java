package com.zxl.parser.targetpage.loader;

import com.zxl.parser.targetpage.TargetPage;

import java.util.List;
import java.util.Map;

/**
 * 加载TargetPageConfig的接口
 */
public interface TargetPageConfigLoader {
    /**
     * 从数据源（MongoDB或其他）加载所有的目标页面配置。
     * @return Map<Integer, List<TargetPage>> key是ProfileId，value是该ProfileId对应的所有目标页面配置数据
     */
    public Map<Integer, List<TargetPage>> getTargetPagesConfigs();
}
