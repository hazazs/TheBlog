package hu.hazazs.blog.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class BlogExceptionController {
    @Value("${spring.servlet.multipart.max-file-size}")
    private String limit;
    @Value("${service.status.maximum-size-exceeded}")
    private String status;
    @ExceptionHandler
    public String exceptionHandler(Exception exception, Model model, HttpServletRequest request) {
        if (exception instanceof MultipartException && exception.getCause() instanceof IllegalStateException && exception.getCause().getCause() instanceof SizeLimitExceededException) {
            model.addAttribute("back", request.getHeader("Referer"));
            return BlogController.status(model, "sorry", status + "<span class=\"red\"> (<b>" + limit + "</b>)</span>", null, null);
        }
        return BlogController.status(model, "sorry", "<span class=\"red\">" + exception.getMessage() + "</span>", null, null);
    }
}