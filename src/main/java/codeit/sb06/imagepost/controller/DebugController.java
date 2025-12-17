package codeit.sb06.imagepost.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @GetMapping("/api/debug/context")
    public String getContextInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated()) {
            return "Current Context: Anonymous (No Authentication)";
        }

        return "Current Context: " + auth.getName() + " (" + auth.getAuthorities() + ")";
    }
}
