package com.example.demo.common.exception;

import com.example.demo.common.util.I18nHelper;
import org.springframework.http.HttpStatus;

public class InvalidImportTemplateException extends CustomException {

    public InvalidImportTemplateException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidImportTemplateException() {
        super(I18nHelper.getMessage("report.error.invalid_import_template"), HttpStatus.BAD_REQUEST);
    }

}
