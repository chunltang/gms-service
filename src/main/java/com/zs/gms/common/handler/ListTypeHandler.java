package com.zs.gms.common.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zs.gms.entity.messagebox.ApproveProcess;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@MappedTypes(value = {List.class})
@MappedJdbcTypes(value = JdbcType.VARCHAR)
@Slf4j
public class ListTypeHandler implements TypeHandler<List> {
    @Override
    public void setParameter(PreparedStatement ps, int i, List parameter, JdbcType jdbcType) throws SQLException {
        String param = JSONObject.toJSON(parameter).toString();
        try {
            ps.setString(i,param);
        }catch (SQLException e){
            log.error("sql类型转换失败",e);
        }
    }

    @Override
    public List<ApproveProcess> getResult(ResultSet rs, String columnName) throws SQLException {
        return JSON.parseObject(rs.getString(columnName),new TypeReference<List<ApproveProcess>>(){});
    }

    @Override
    public List<ApproveProcess> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return JSON.parseObject(rs.getString(columnIndex),new TypeReference<List<ApproveProcess>>(){});
    }

    @Override
    public List<ApproveProcess> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JSON.parseObject(cs.getString(columnIndex),new TypeReference<List<ApproveProcess>>(){});
    }
}
