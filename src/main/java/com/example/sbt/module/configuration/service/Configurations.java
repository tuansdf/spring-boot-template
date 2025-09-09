package com.example.sbt.module.configuration.service;

import com.example.sbt.core.constant.ConfigurationCode;
import com.example.sbt.shared.util.CommonUtils;
import com.example.sbt.shared.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class Configurations {
    private final ConfigurationService configurationService;

    public Boolean isRegistrationEnabled() {
        return ConversionUtils.toBoolean(configurationService.findValueByCode(ConfigurationCode.REGISTRATION_ENABLED));
    }

    public String getActivateAccountUrl() {
        return configurationService.findValueByCode(ConfigurationCode.ACTIVATE_ACCOUNT_URL);
    }

    public List<String> getWhitelistedIps() {
        String result = configurationService.findValueByCode(ConfigurationCode.WHITELISTED_IPS);
        if (result == null) return null;
        return Arrays.stream(result.split(";")).toList();
    }

    public Integer getLoginMaxAttempts() {
        Integer result = ConversionUtils.toInteger(configurationService.findValueByCode(ConfigurationCode.LOGIN_MAX_ATTEMPTS));
        if (!CommonUtils.isPositive(result)) return null;
        return Math.abs(result);
    }

    public Integer getLoginTimeWindow() {
        Integer result = ConversionUtils.toInteger(configurationService.findValueByCode(ConfigurationCode.LOGIN_TIME_WINDOW));
        if (!CommonUtils.isPositive(result)) return null;
        return Math.abs(result);
    }

    public Integer getEmailThrottleTimeWindow() {
        Integer result = ConversionUtils.toInteger(configurationService.findValueByCode(ConfigurationCode.EMAIL_THROTTLE_TIME_WINDOW));
        if (!CommonUtils.isPositive(result)) return null;
        return Math.abs(result);
    }

    public List<String> getLoginEmailDomains() {
        String result = ConversionUtils.toString(configurationService.findValueByCode(ConfigurationCode.LOGIN_EMAIL_DOMAINS));
        if (result == null) return null;
        return Arrays.stream(result.split(";")).toList();
    }

    public List<String> getRegisterEmailDomains() {
        String result = ConversionUtils.toString(configurationService.findValueByCode(ConfigurationCode.REGISTER_EMAIL_DOMAINS));
        if (result == null) return null;
        return Arrays.stream(result.split(";")).toList();
    }
}
