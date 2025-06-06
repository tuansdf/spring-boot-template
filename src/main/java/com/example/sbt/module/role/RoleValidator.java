package com.example.sbt.module.role;

import com.example.sbt.common.constant.Constants;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.util.LocaleHelper;
import com.example.sbt.common.util.LocaleHelper.LocaleKey;
import com.example.sbt.common.util.ValidationUtils;
import com.example.sbt.module.role.dto.RoleDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoleValidator {

    public void validateUpdate(RoleDTO requestDTO) {
        if (StringUtils.isNotEmpty(requestDTO.getName()) && requestDTO.getName().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.name"), 255));
        }
        if (StringUtils.isNotEmpty(requestDTO.getDescription()) && requestDTO.getDescription().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.description"), 255));
        }
    }

    public void validateCreate(RoleDTO requestDTO) {
        if (StringUtils.isEmpty(requestDTO.getCode())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.code")));
        }
        if (!requestDTO.getCode().startsWith(Constants.ROLE_STARTS_WITH)) {
            throw new CustomException(LocaleHelper.getMessage("form.error.not_start_with", new LocaleKey("field.code"), Constants.ROLE_STARTS_WITH));
        }
        String codeError = ValidationUtils.validateCode(requestDTO.getCode());
        if (codeError != null) {
            throw new CustomException(codeError);
        }
        validateUpdate(requestDTO);
    }

}
