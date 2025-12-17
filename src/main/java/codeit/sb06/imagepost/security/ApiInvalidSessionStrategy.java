package codeit.sb06.imagepost.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.web.session.InvalidSessionStrategy;

import java.io.IOException;
import java.util.Map;

public class ApiInvalidSessionStrategy implements InvalidSessionStrategy {
    private final ObjectMapper objectMapper= new ObjectMapper();

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String requestURI = request.getRequestURI();

        if(requestURI.startsWith("/app")||requestURI.startsWith("/api/debug")) {

            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            response.sendRedirect(requestURI);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), Map.of(
                "code","SESSION_EXPIRED",
                "message","Current session is expired."
        ));
    }


}
