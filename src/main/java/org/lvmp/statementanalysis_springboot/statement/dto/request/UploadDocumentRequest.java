package org.lvmp.statementanalysis_springboot.statement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.lvmp.statementanalysis_springboot.validation.PdfFile;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadDocumentRequest {
    @NotNull(message = "File cannot be empty")
    @PdfFile(message = "File must be a valid PDF")
    private MultipartFile file;
}
