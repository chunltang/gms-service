package com.zs.gms.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.zs.gms.entity.mapmanager.Point;
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

@MappedTypes(value = {Point.class})
@MappedJdbcTypes(value = JdbcType.VARCHAR)
@Slf4j
public class PointTypeHandler implements TypeHandler<Point> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Point parameter, JdbcType jdbcType) throws SQLException {
        String param = JSONObject.toJSON(parameter).toString();
        try {
            ps.setString(i,param);
        }catch (SQLException e){
            log.error("sql类型转换失败",e);
        }
    }

    @Override
    public Point getResult(ResultSet rs, String columnName) throws SQLException {
        return JSONObject.parseObject(rs.getString(columnName),Point.class);
    }

    @Override
    public Point getResult(ResultSet rs, int columnIndex) throws SQLException {
        return JSONObject.parseObject(rs.getString(columnIndex),Point.class);
    }

    @Override
    public Point getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JSONObject.parseObject(cs.getString(columnIndex),Point.class);
    }
}
