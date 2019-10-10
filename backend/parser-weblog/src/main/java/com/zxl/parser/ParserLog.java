package com.zxl.parser;

import com.zxl.parser.dataobject.InvalidLogObject;
import com.zxl.parser.dataobject.ParsedDataObject;
import com.zxl.parser.dataobjectbuilder.AbstractDataObjectBuilder;
import com.zxl.preparser.PreParsedLog;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ParserLog {
    private Set<String> cmds; //支持解析的日志类型
    private Set<AbstractDataObjectBuilder> builders; //所有解析日志所需的builder

    public ParserLog(Set<String> cmds, Set<AbstractDataObjectBuilder> builders) {
        this.cmds = cmds;
        this.builders = builders;
    }
    /**
     * 日志解析的接口
     *  返回的对象中，含有正常的DataObject，也可能含有无效的DataObject，所以我们返回标识接口ParsedDataObject
     *  不管是正常的DataObject还是无效的DataObject都会实现这个标识接口ParsedDataObject
     * @param preParsedLog
     * @return 返回已经解析好的DataObject
     */
    public List<? extends ParsedDataObject> parse(PreParsedLog preParsedLog){
        String cmd = preParsedLog.getCommand();
        if (cmds.contains(cmd)){//是否支持该日志类型
            for (AbstractDataObjectBuilder builder : builders){
                if (builder.getCommond().equals(cmd)){//是否有对应的builder来解析
                    return builder.doBuildDataObjects(preParsedLog);
                }
            }
            return Arrays.asList(new InvalidLogObject("dont find support builder"));
        }
        return Arrays.asList(new InvalidLogObject("dont support command"));
    }
}
