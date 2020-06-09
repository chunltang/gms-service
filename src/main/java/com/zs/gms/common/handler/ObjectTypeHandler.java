package com.zs.gms.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.zs.gms.entity.mapmanager.MapInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.ParameterizedType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(value = {MapInfo.MapVersion.class})
@MappedJdbcTypes(value = JdbcType.VARCHAR)
@Slf4j
public class ObjectTypeHandler implements TypeHandler<MapInfo.MapVersion> {

    private Class<T> getClazz(){
        return (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, MapInfo.MapVersion parameter, JdbcType jdbcType) throws SQLException {
        String param = JSONObject.toJSON(parameter).toString();
        try {
            ps.setString(i,param);
        }catch (SQLException e){
            log.error("sql类型转换失败",e);
        }
    }

    @Override
    public MapInfo.MapVersion getResult(ResultSet rs, String columnName) throws SQLException {
        return JSONObject.parseObject(rs.getString(columnName),MapInfo.MapVersion.class);
    }

    @Override
    public MapInfo.MapVersion getResult(ResultSet rs, int columnIndex) throws SQLException {
        return JSONObject.parseObject(rs.getString(columnIndex),MapInfo.MapVersion.class);
    }

    @Override
    public MapInfo.MapVersion getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JSONObject.parseObject(cs.getString(columnIndex),MapInfo.MapVersion.class);
    }
}
