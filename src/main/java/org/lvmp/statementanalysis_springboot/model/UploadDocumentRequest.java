package org.lvmp.statementanalysis_springboot.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lvmp.statementanalysis_springboot.validation.PdfFile;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UploadDocumentRequest {
    @NotNull(message = "File cannot be empty")
    @PdfFile(message = "File must be a valid PDF")
    private MultipartFile file;
}
