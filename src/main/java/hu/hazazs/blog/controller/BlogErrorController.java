package hu.hazazs.blog.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

@Controller
@ConfigurationProperties(prefix = "controller.error")
public class BlogErrorController implements ErrorController {
    @Value("${service.status.forbidden}")
    private String status;
    private ErrorAttributes errorAttributes;
    @Autowired
    public void setErrorAttributes(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }
    @RequestMapping("/error")
    public String error(HttpServletRequest request, Model model) {
        Map<String, Object> error = errorAttributes.getErrorAttributes(new ServletWebRequest(request), ErrorAttributeOptions.defaults());
        String code = error.get("status").toString();
        if (code.equals("999")) {
            error.put("error", "Not Found");
            error.put("status", "404");
        }
        else if (code.equals("403"))
            return BlogController.status(model, "info", status, null, null);
        return BlogController.status(model, "sorry", "<b>" + error.get("status") + "</b>", "<span class=\"red\">" + error.get("error") + "</span>", null);
    }
    @Override
    public String getErrorPath() {
        return null;
    }
}