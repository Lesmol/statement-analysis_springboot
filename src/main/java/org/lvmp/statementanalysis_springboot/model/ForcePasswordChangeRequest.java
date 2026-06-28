package org.lvmp.statementanalysis_springboot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForcePasswordChangeRequest {
    private String username;
    private String newPassword;
    private String session;
}
