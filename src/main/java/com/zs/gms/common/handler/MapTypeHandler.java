package com.zs.gms.common.handler;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@MappedTypes(value = {Map.class})
@MappedJdbcTypes(value = JdbcType.VARCHAR)
@Slf4j
public class MapTypeHandler implements TypeHandler<Map> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Map parameter, JdbcType jdbcType) throws SQLException {
        String param = JSONObject.toJSON(parameter).toString();
        try {
            ps.setString(i,param);
        }catch (SQLException e){
            log.error("sql类型转换失败",e);
        }
    }

    @Override
    public Map getResult(ResultSet rs, String columnName) throws SQLException {
        return JSONObject.parseObject(rs.getString(columnName), HashMap.class);
    }

    @Override
    public Map getResult(ResultSet rs, int columnIndex) throws SQLException {
        return JSONObject.parseObject(rs.getString(columnIndex),HashMap.class);
    }

    @Override
    public Map getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JSONObject.parseObject(cs.getString(columnIndex),HashMap.class);
    }
}
