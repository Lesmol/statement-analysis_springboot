package org.lvmp.statementanalysis_springboot.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class PdfFileValidator implements ConstraintValidator<PdfFile, MultipartFile> {

    /**
     * Validates that the provided multipart upload represents a PDF file.
     *
     * @param file    the uploaded multipart file to validate
     * @param context the constraint validation context
     * @return        `true` if `file` is not null, not empty, has an original filename ending with `.pdf` (case-insensitive),
     *                and its content type equals `"application/pdf"`, `false` otherwise
     */
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/pdf");
    }
}
