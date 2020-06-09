package com.zs.gms.common.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BoolConverterHandler implements TypeHandler<Boolean> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter ? 1 : 0);
    }

    @Override
    public Boolean getResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getInt(columnName) > 0;
    }

    @Override
    public Boolean getResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getInt(columnIndex) > 0;
    }

    @Override
    public Boolean getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getInt(columnIndex) > 0;
    }
}
