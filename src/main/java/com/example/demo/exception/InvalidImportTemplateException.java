package com.example.demo.exception;

import com.example.demo.util.I18nHelper;
import org.springframework.http.HttpStatus;

public class InvalidImportTemplateException extends CustomException {

    public InvalidImportTemplateException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidImportTemplateException() {
        super(I18nHelper.getMessage("report.error.invalid_import_template"), HttpStatus.BAD_REQUEST);
    }

}
