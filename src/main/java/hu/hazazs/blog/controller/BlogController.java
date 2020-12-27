package hu.hazazs.blog.controller;

import hu.hazazs.blog.BlogApplication;
import hu.hazazs.blog.entity.Blog;
import hu.hazazs.blog.service.StatusService;
import hu.hazazs.blog.entity.Blogger;
import hu.hazazs.blog.service.VerificationService;
import hu.hazazs.blog.service.AuthenticationService;
import hu.hazazs.blog.service.BlogService;
import hu.hazazs.blog.service.BloggerService;
import hu.hazazs.blog.service.EmailService;
import hu.hazazs.blog.service.IOService;
import hu.hazazs.blog.service.PasswordService;
import hu.hazazs.blog.service.RoleService;
import hu.hazazs.blog.service.SignupService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

@Controller
public class BlogController {
    private final AuthenticationService authenticationService;
    private final BlogService blogService;
    private final BloggerService bloggerService;
    private final EmailService emailService;
    private final IOService IOService;
    private final PasswordService passwordService;
    private final RoleService roleService;
    private final SignupService signupService;
    private final StatusService statusService;
    private final VerificationService verificationService;
    @Autowired
    public BlogController(
        AuthenticationService authenticationService,
        BlogService blogService,
        BloggerService bloggerService,
        EmailService emailService,
        IOService IOService,
        PasswordService passwordService,
        RoleService roleService,
        SignupService signupService,
        StatusService statusService,
        VerificationService verificationService) {
            this.authenticationService = authenticationService;
            this.blogService = blogService;
            this.bloggerService = bloggerService;
            this.emailService = emailService;
            this.IOService = IOService;
            this.passwordService = passwordService;
            this.roleService = roleService;
            this.signupService = signupService;
            this.statusService = statusService;
            this.verificationService = verificationService;
    }
    public static String status(Model model, String type, String message1, String message2, String message3) {
        model.addAttribute("type", type);
        model.addAttribute("message1", message1);
        if (message2 != null) {
            model.addAttribute("message2", message2);
            if (message3 != null)
                model.addAttribute("message3", message3);
        }
        return "status";
    }
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        Authentication authentication = authenticationService.getAuthentication();
        model.addAttribute("avatar", authentication instanceof AnonymousAuthenticationToken ? authentication.getName() + ".jpg" : bloggerService.findByUsername(authentication.getName()).getAvatar());
        return "home";
    }
    @RequestMapping(value = "/blogs", method = RequestMethod.GET)
    public String blogs(Model model) {
        model.addAttribute("title", "Blogs");
        model.addAttribute("blogs", blogService.findAllByOrderByPostedDesc());
        return "blog-s";
    }
    @RequestMapping("/blogs/new")
    public String newBlog(HttpServletRequest request, Model model, @ModelAttribute Blog blog) {
        if (request.getMethod().equals("GET")) {
            model.addAttribute("title", "New blog");
            model.addAttribute("action", "/blogs/new");
            model.addAttribute("blog", new Blog());
            model.addAttribute("button", "Post");
            return "blog-new-and-edit";
        }
        blogService.save(blogService.fill(blog, bloggerService.findByUsername(authenticationService.getAuthentication().getName())));
        return "redirect:/blogs";
    }
    @RequestMapping(value = "/blog/{id}", method = RequestMethod.GET)
    public String blog(@PathVariable String id, Model model) {
        try {
            List<Blog> blogs = blogService.findById(Long.parseLong(id));
            if (!blogs.isEmpty()) {
                model.addAttribute("title", "Blog #" + id);
                model.addAttribute("blogs", blogs);
                return "blog-s";
            }
            return status(model, "sorry", statusService.getStatus().get("blog-not-found") + " <b class=\"red\">" + id + "</b>", null, null);
        } catch(NumberFormatException exception) {
            BlogApplication.getLogger().error(exception.getMessage());
            return status(model, "info", statusService.getStatus().get("blog-id-invalid"), null, null);
          }
    }
    @RequestMapping("/blog/{id}/edit")
    public String editBlog(@PathVariable String id, HttpServletRequest request, Model model) {
        try {
            List<Blog> blogs = blogService.findById(Long.parseLong(id));
            Blog blog = !blogs.isEmpty() ? blogs.get(0) : new Blog();
            if (request.getMethod().equals("GET")) {
                if (!blogs.isEmpty()) {
                    if (authenticationService.getAuthentication().getName().equals(blog.getBlogger().getUsername()) || request.isUserInRole("ROLE_ADMIN")) {
                        model.addAttribute("title", "Edit blog #" + id);
                        model.addAttribute("action", "/blog/" + id + "/edit");
                        model.addAttribute("blog", blog);
                        model.addAttribute("button", "Save");
                        return "blog-new-and-edit";
                    }
                    return status(model, "sorry", statusService.getStatus().get("no-permission-to-edit"), null, null);
                }
                return status(model, "sorry", statusService.getStatus().get("blog-not-found") + " <b class=\"red\">" + id + "</b>", null, null);
            }
            blogService.save(blogService.edit(blog, request.getParameter("title"), request.getParameter("content")));
            return "redirect:/blogs";
        } catch(NumberFormatException exception) {
            BlogApplication.getLogger().error(exception.getMessage());
            return status(model, "info", statusService.getStatus().get("blog-id-invalid"), null, null);
          }
    }
    @RequestMapping(value = "/blog/{id}/delete", method = RequestMethod.POST)
    public String deleteBlog(@PathVariable Long id) {
        blogService.deleteById(id);
        return "redirect:/blogs";
    }
    @RequestMapping("/bloggers")
    public String bloggers(HttpServletRequest request, Model model) {
        List<Blogger> bloggers;
        if (request.getMethod().equals("GET"))
            bloggers = bloggerService.findAllOrderByFullName();
        else {
            String search = request.getParameter("search");
            model.addAttribute("bloggers_search", search);
            bloggers = bloggerService.findBySearchOrderByFullName(search.toLowerCase());
        }
        model.addAttribute("bloggers", bloggers);
        return "bloggers";
    }
    @RequestMapping(value = "/bloggers/export-pdf", method = RequestMethod.POST)
    public ResponseEntity<InputStreamResource> exportPDF(HttpServletRequest request) {
        String search = request.getParameter("search");
        if (search == null)
            return IOService.exportPDF(bloggerService.findAllOrderByFullName());
        return IOService.exportPDF(bloggerService.findBySearchOrderByFullName(search.toLowerCase()));
    }
    @RequestMapping(value = "/blogger/{username}", method = RequestMethod.GET)
    public String blogger(@PathVariable String username, Model model) {
        Blogger blogger = bloggerService.findByUsername(username);
        if (blogger == null)
            return status(model, "sorry", statusService.getStatus().get("blogger-not-found") + " <b class=\"red\">" + username + "</b>", null, null);
        model.addAttribute("blogger", blogger);
        model.addAttribute("blogs", blogService.findByBloggerOrderByPostedDesc(blogger.getId()));
        return "blogger";
    }
    @RequestMapping(value = "/blogger/{username}/change-avatar-failed", method = RequestMethod.POST)
    public String changeAvatar(@RequestParam("file") MultipartFile file, Model model, HttpServletRequest request, @PathVariable String username) throws InterruptedException {
        String type = file.getContentType();
        if (type == null || !IOService.typeValidator(type)) {
            model.addAttribute("back", request.getHeader("Referer"));
            return status(model, "sorry", statusService.getStatus().get("unsupported-image-format"), null, null);
        }
        Blogger blogger = bloggerService.findByUsername(username);
        bloggerService.save(IOService.changeAvatar(file, blogger));
        return "redirect:/blogger/" + blogger.getUsername();
    }
    @RequestMapping(value = "/blogger/{username}/default-avatar", method = RequestMethod.POST)
    public String defaultAvatar(@PathVariable String username) {
        Blogger blogger = bloggerService.findByUsername(username);
        bloggerService.save(IOService.defaultAvatar(blogger));
        return "redirect:/blogger/" + blogger.getUsername();
    }
    @RequestMapping(value = "/blogger/{username}/delete-account", method = RequestMethod.POST)
    public String deleteAccount(@PathVariable String username, HttpServletRequest request) {
        Long id =  bloggerService.findByUsername(username).getId();
        Blogger blogger = bloggerService.findById(id);
        blogService.deleteBlogs(id);
        bloggerService.deleteRoles(id);
        bloggerService.deleteById(id);
        if (blogger.getAvatar().endsWith("jpg"))
            IOService.deleteAvatar(blogger);
        emailService.sendEmailToBlogger(blogger, "delete-account");
        if (!authenticationService.getAuthentication().getName().equals(blogger.getUsername()))
            return "redirect:/bloggers";
        authenticationService.logout();
        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return "redirect:/goodbye";
    }
    @RequestMapping("/help")
    public String help(HttpServletRequest request, Model model) {
        if (request.getMethod().equals("GET"))
            return "help";
        String email = authenticationService.getAuthentication() instanceof AnonymousAuthenticationToken ?
            request.getParameter("email") :
            bloggerService.findByUsername(authenticationService.getAuthentication().getName()).getEmail();
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        if (!emailService.emailValidator(email)) {
            model.addAttribute("help_email", email);
            model.addAttribute("help_subject", subject);
            model.addAttribute("help_message", message);
            model.addAttribute("alert_email", statusService.getStatus().get("email-invalid"));
            return "help";
        }
        if (emailService.sendEmailToAdmin(subject, email, message))
            return status(model, "info", statusService.getStatus().get("help-message-sent"), statusService.getStatus().get("wait-for-response"), null);
        return status(model, "sorry", statusService.getStatus().get("help-message-failed"), statusService.getStatus().get("try-again-later"), null);
    }
    @RequestMapping("/signup")
    public String signup(HttpServletRequest request, Model model, @ModelAttribute Blogger blogger) {
        if (!(authenticationService.getAuthentication() instanceof AnonymousAuthenticationToken))
            return "redirect:/";
        if (request.getMethod().equals("GET")) {
            model.addAttribute("blogger", new Blogger());
            return "signup";
        }
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        List<String> status = signupService.signupStatus(
            blogger, bloggerService.findByUsername(blogger.getUsername()), bloggerService.findByEmail(blogger.getEmail()), passwordService.passwordValidator(password1), password1, password2);
        if (!status.isEmpty()) {
            model.addAttribute("signup_username", blogger.getUsername());
            model.addAttribute("signup_email", blogger.getEmail());
            model.addAttribute("signup_password1", password1);
            model.addAttribute("signup_password2", password2);
            model.addAttribute("signup_fullname", blogger.getFullName());
            model.addAttribute("signup_gender", blogger.getGender());
            if (status.contains("username-invalid") || status.contains("username-reserved"))
                model.addAttribute("alert_username", status.contains("username-invalid") ? statusService.getStatus().get("username-invalid") : statusService.getStatus().get("username-reserved"));
            if (status.contains("email-invalid") || status.contains("email-reserved"))
                model.addAttribute("alert_email", status.contains("email-invalid") ? statusService.getStatus().get("email-invalid") : statusService.getStatus().get("email-reserved"));
            if (status.contains("password-invalid"))
                model.addAttribute("alert_password1", statusService.getStatus().get("password-invalid"));
            if (status.contains("password-different"))
                model.addAttribute("alert_password2", statusService.getStatus().get("password-different"));
            if (status.contains("fullname_invalid"))
                model.addAttribute("alert_fullname", statusService.getStatus().get("fullname-invalid"));
            return "signup";
        }
        bloggerService.save(signupService.fill(blogger, bloggerService.getNextId(), password1, roleService.getDefaultRole()));
        if (emailService.sendEmailToBlogger(blogger, "verification"))
            return status(model, "success",
                statusService.getStatus().get("account-created"),
                statusService.getStatus().get("verification-instructions-sent") + " <b>" + blogger.getEmail() + "</b>",
                statusService.getStatus().get("check-spam"));
        return status(model, "info", statusService.getStatus().get("account-created"),statusService.getStatus().get("verification-instructions-failed"), statusService.getStatus().get("try-log-in"));
    }
    @RequestMapping(value = "/verification/{code}", method = RequestMethod.GET)
    public String verification(@PathVariable String code, Model model) {
        Blogger blogger = bloggerService.findByVerificationCode(code);
        String status = verificationService.verification(blogger);
        if (status.equals("SUCCESS")) {
            bloggerService.save(blogger);
            return status(model, "success", statusService.getStatus().get("verification-success"), null, null);
        }
        if (status.equals("already-verified"))
            return status(model, "info", statusService.getStatus().get("already-verified"), null, null);
        return status(model, "sorry", statusService.getStatus().get("verification-code-incorrect"), null, null);
    }
    @RequestMapping(value = "/login-failed", method = RequestMethod.POST)
    public String login(HttpServletRequest request, Model model) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String status = authenticationService.login(bloggerService.findByUsername(username) == null ? bloggerService.findByEmail(username) : bloggerService.findByUsername(username), password);
        if (!status.equals("SUCCESS")) {
            model.addAttribute("username", username);
            model.addAttribute("password", password);
            if (status.equals("bad-credentials"))
                return status(model, "sorry", statusService.getStatus().get("bad-credentials"), statusService.getStatus().get("forgot-your-password"), null);
            model.addAttribute("request_type", "resend-verification");
            model.addAttribute("id", bloggerService.findByUsername(username) == null ? bloggerService.findByEmail(username).getId() : bloggerService.findByUsername(username).getId());
            model.addAttribute("request_class", "envelope");
            model.addAttribute("request_button", "Resend verification e-mail");
            return status(model, "info", statusService.getStatus().get("account-unverified"), null, null);
        }
        return "redirect:/";
    }
    @RequestMapping(value = "/resend-verification", method = RequestMethod.POST)
    public String requestStatus(HttpServletRequest request, Model model) {
        Blogger blogger = bloggerService.findById(Long.parseLong(request.getParameter("id")));
        if (emailService.sendEmailToBlogger(blogger, "verification"))
            return status(model, "info", statusService.getStatus().get("verification-instructions-sent") + " <b>" + blogger.getEmail() + "</b>", statusService.getStatus().get("check-spam"), null);
        return status(model, "sorry", statusService.getStatus().get("verification-instructions-failed"), statusService.getStatus().get("try-again-later"), null);
    }
    @RequestMapping("/password/forgot")
    public String forgotPassword(HttpServletRequest request, Model model) {
        if (request.getMethod().equals("GET"))
            return "password-forgot";
        String email = request.getParameter("email");
        Blogger blogger = bloggerService.findByEmail(email);
        String status = passwordService.forgotPassword(email, blogger);
        if (!status.equals("SUCCESS")) {
            model.addAttribute("reset_password_email", email);
            model.addAttribute("alert_email", status.equals("email-invalid") ? statusService.getStatus().get("email-invalid") : statusService.getStatus().get("email-unregistered"));
            return "password-forgot";
        }
        if (emailService.sendEmailToBlogger(blogger, "reset-password"))
            return status(model, "info", statusService.getStatus().get("reset-password-instructions-sent") + " <b>" + blogger.getEmail() + "</b>", statusService.getStatus().get("check-spam"), null);
        return status(model, "sorry", statusService.getStatus().get("reset-password-instructions-failed"), statusService.getStatus().get("try-again-later"), null);
    }
    @RequestMapping("/password/reset/{code}")
    public String resetPassword(HttpServletRequest request, @PathVariable String code, Model model) {
        if (request.getMethod().equals("GET")) {
            if (bloggerService.findByVerificationCode(code) == null)
                return status(model, "sorry", statusService.getStatus().get("verification-code-incorrect"), null, null);
            model.addAttribute("code", code);
            return "password-reset";
        }
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        Blogger blogger = bloggerService.findByVerificationCode(code);
        List<String> status = passwordService.resetPassword(password1, blogger, password2);
        if (!status.isEmpty()) {
            model.addAttribute("new_password1", password1);
            model.addAttribute("new_password2", password2);
            if (status.contains("password-invalid") || status.contains("password-same"))
                model.addAttribute("alert_password1", status.contains("password-invalid") ? statusService.getStatus().get("password-invalid") : statusService.getStatus().get("password-same"));
            if (status.contains("password-different"))
                model.addAttribute("alert_password2", statusService.getStatus().get("password-different"));
            return "password-reset";
        }
        bloggerService.save(passwordService.changePassword(blogger, password1));
        return status(model, "success", statusService.getStatus().get("password-changed"), null, null);
    }
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public String logout() {
        authenticationService.logout();
        return "redirect:/";
    }
    @RequestMapping(value = "/goodbye", method = RequestMethod.POST)
    public String goodbye(Model model) {
        return status(model, "goodbye", statusService.getStatus().get("account-successfully-deleted"), statusService.getStatus().get("goodbye"), null);
    }
}