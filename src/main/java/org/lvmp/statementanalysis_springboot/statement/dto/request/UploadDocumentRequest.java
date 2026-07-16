package org.lvmp.statementanalysis_springboot.statement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.lvmp.statementanalysis_springboot.validation.PdfFile;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadDocumentRequest {
    @NotNull(message = "File cannot be empty")
    @PdfFile(message = "File must be a valid PDF")
    private MultipartFile file;
}
