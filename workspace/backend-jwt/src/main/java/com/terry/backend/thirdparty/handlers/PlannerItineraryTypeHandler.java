package com.terry.backend.thirdparty.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.terry.backend.api.planner.dto.PlannerItineraryDayDTO;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PlannerItineraryTypeHandler extends BaseTypeHandler<List<PlannerItineraryDayDTO>> {

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<PlannerItineraryDayDTO> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, MAPPER.writeValueAsString(parameter));
        } catch (Exception e) {
            throw new SQLException("List<PlannerItineraryDayDTO> 직렬화 실패", e);
        }
    }

    @Override
    public List<PlannerItineraryDayDTO> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public List<PlannerItineraryDayDTO> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public List<PlannerItineraryDayDTO> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private List<PlannerItineraryDayDTO> parse(String json) throws SQLException {
        if (json == null || json.isEmpty()) {
            return List.of();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<List<PlannerItineraryDayDTO>>() {});
        } catch (Exception e) {
            throw new SQLException("List<PlannerItineraryDayDTO> 역직렬화 실패", e);
        }
    }
}
