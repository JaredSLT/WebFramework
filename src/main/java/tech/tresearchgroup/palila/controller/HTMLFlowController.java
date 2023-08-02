package tech.tresearchgroup.palila.controller;

import org.xmlet.htmlapifaster.Datalist;
import org.xmlet.htmlapifaster.Div;

import java.util.List;

public class HTMLFlowController {
    public static String dataListConverter(String id, List<String> values, String selected, Div<?> data) {
        StringBuilder stringBuilder = new StringBuilder();
        Datalist<? extends Div<?>> dataList = data.datalist();
        dataList = generateOptions(values, selected, dataList);
        dataList.attrId(id).__();
        return stringBuilder.toString();
    }

    private static Datalist<? extends Div<?>> generateOptions(List<String> values, String selected, Datalist<? extends Div<?>> dataList) {
        for(String value : values) {
            if(value.equals(selected)) {
                dataList = dataList.option().attrValue(value).attrSelected(true).__();
            } else {
                dataList = dataList.option().attrValue(value).__();
            }
        }
        return dataList;
    }
}
