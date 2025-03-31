package com.example.sbt.common.exception;

import com.example.sbt.common.util.LocaleHelper;
import org.springframework.http.HttpStatus;

public class InvalidImportTemplateException extends CustomException {

    public InvalidImportTemplateException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidImportTemplateException() {
        super(LocaleHelper.getMessage("report.error.invalid_import_template"), HttpStatus.BAD_REQUEST);
    }

}
