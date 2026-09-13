package org.lvmp.statementanalysis_springboot.authentication;

public record UserAuthenticated(String sub, String email, String phoneNumber) {
}
