package com.example.sbt.features.user.batch;

import com.example.sbt.features.user.entity.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User u = new User();
        u.setId(rs.getObject("id", UUID.class));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setName(rs.getString("name"));
        u.setIsEnabled(rs.getBoolean("is_enabled"));
        u.setIsVerified(rs.getBoolean("is_verified"));
        u.setIsOtpEnabled(rs.getBoolean("is_otp_enabled"));
        u.setTenantId(rs.getString("tenant_id"));
        return u;
    }
}
