package com.terry.backend.thirdparty.handlers;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@MappedJdbcTypes(value = {JdbcType.VARCHAR, JdbcType.TIMESTAMP, JdbcType.DATE}, includeNullJdbcType = true)
public class DateToDateStringTypeHandler implements TypeHandler<Date> {
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  @Override
  public void setParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType)
          throws SQLException {
    if (parameter != null) {
      ps.setString(i, DATE_FORMAT.format(parameter));
    } else {
      ps.setString(i, null);
    }
  }

  @Override
  public Date getResult(ResultSet rs, String columnName) throws SQLException {
    return parseFromString(rs.getString(columnName));
  }

  @Override
  public Date getResult(ResultSet rs, int columnIndex) throws SQLException {
    return parseFromString(rs.getString(columnIndex));
  }

  @Override
  public Date getResult(CallableStatement cs, int columnIndex) throws SQLException {
    return parseFromString(cs.getString(columnIndex));
  }

  private Date parseFromString(String data) {
    if (!StringUtils.hasText(data)) return null;
    try {
      if (data.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
        return new SimpleDateFormat("yyyy-MM-dd").parse(data);
      } else if (data.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(data);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }
}
