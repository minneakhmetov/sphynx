package com.razzzil.sphynx.worker.utils;

import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Objects;

public class WorkerUtils {

    private static final Integer CUT_STRING_MAX_VALUE = 100;

    @SneakyThrows
    public static JSONArray resultSetToJson(ResultSet resultSet){
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        final int columnCount = resultSetMetaData.getColumnCount();
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            JSONObject jsonObject = new JSONObject();
            //  Object[] values = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                //   System.out.println(resultSetMetaData.getColumnName(i) + " " + resultSet.getObject(i));
                Object value = resultSet.getObject(i);
                jsonObject.put(resultSetMetaData.getColumnName(i), Objects.nonNull(value) ? value : JSONObject.NULL);
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public static String cutString(String s){
        return s.length() > 100 ? s.substring(0, CUT_STRING_MAX_VALUE).concat("...") : s;
    }
}
