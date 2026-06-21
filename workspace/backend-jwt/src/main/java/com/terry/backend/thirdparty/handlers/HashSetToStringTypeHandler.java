package com.terry.backend.thirdparty.handlers;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;

@MappedJdbcTypes(value = JdbcType.VARCHAR, includeNullJdbcType = true)
public class HashSetToStringTypeHandler extends BaseTypeHandler<HashSet<String>> {

  @Override
  public void setParameter(PreparedStatement ps, int i, HashSet<String> parameter,
      JdbcType jdbcType) throws SQLException {
    System.out.println("SetStringTypeHandler!!");
    if (parameter == null || parameter.isEmpty()) {
      ps.setString(i, null);
    } else {
      ps.setString(i, StringUtils.collectionToCommaDelimitedString(parameter));
    }
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, HashSet<String> parameter,
      JdbcType jdbcType) throws SQLException {
    System.out.println("SetStringTypeHandler!!");
    ps.setString(i, StringUtils.collectionToCommaDelimitedString(parameter));
  }

  @Override
  public HashSet<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return stringToSet(rs.getString(columnName));
  }

  @Override
  public HashSet<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return stringToSet(rs.getString(columnIndex));
  }

  @Override
  public HashSet<String> getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    return stringToSet(cs.getString(columnIndex));
  }

  private HashSet<String> stringToSet(String str) {
    if (StringUtils.hasText(str)) {
      HashSet<String> result = new HashSet<String>();
      String[] strs = str.split(",");
      result.addAll(Arrays.asList(strs));
      return result;
    } else {
      return null;
    }
  }

}
