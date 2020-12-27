package hu.hazazs.blog.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import hu.hazazs.blog.BlogApplication;
import hu.hazazs.blog.entity.Blogger;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class IOService {
    @Value("${service.IO.avatars.path}")
    private String path;
    @Value("${service.IO.avatars.size}")
    private int size;
    @Value("${service.IO.avatars.supported-image-formats}")
    private String types;
    public Blogger changeAvatar(MultipartFile file, Blogger blogger) throws InterruptedException {
        try {
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            BufferedImage resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D avatar = resized.createGraphics();
            avatar.fillRect(0, 0, size, size);
            double width = original.getWidth();
            double height = original.getHeight();
            if (height > width)
                avatar.drawImage(original, (size - (int) (width / height * size)) / 2, 0, (int) (width / height * size), size, null);
            else avatar.drawImage(original, 0, (size - (int) (height / width * size)) / 2, size, (int) (height / width * size), null);
            avatar.dispose();
            ImageIO.write(resized, "jpg", new File(path + blogger.getUsername() + ".jpg"));
            Thread.sleep(1500);
            blogger.setAvatar(blogger.getUsername() + ".jpg");
        } catch (IOException exception) {
            BlogApplication.getLogger().error(exception.getMessage());
          }
        return blogger;
    }
    public Blogger defaultAvatar(Blogger blogger) {
        blogger.setAvatar(blogger.getGender() + ".png");
        deleteAvatar(blogger);
        return blogger;
    }
    public void deleteAvatar(Blogger blogger) {
        FileUtils.deleteQuietly(new File(path + blogger.getUsername() + ".jpg"));
    }
    public ResponseEntity<InputStreamResource> exportPDF(List<Blogger> bloggers) {
        Document document = new Document();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, stream);
            document.open();
            PdfPTable table = new PdfPTable(new float[]{1, 5, 4, 7});
            table.setWidthPercentage(100);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setPaddingBottom(7.65f);
            table.getDefaultCell().setBackgroundColor(new GrayColor(0.75f));
            table.addCell("ID");
            table.addCell("Name");
            table.addCell("Username");
            table.addCell("E-mail");
            table.getDefaultCell().setBackgroundColor(GrayColor.GRAYWHITE);
            bloggers.forEach(blogger -> {
                table.addCell(String.valueOf(blogger.getId()));
                table.addCell(blogger.getFullName());
                table.addCell(blogger.getUsername());
                table.addCell(blogger.getEmail());
            });
            document.add(table);
            document.close();
        } catch (DocumentException exception) {
            BlogApplication.getLogger().error(exception.getMessage());
          }
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = bloggers.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(stream.toByteArray().length)
            .body(new InputStreamResource(new ByteArrayInputStream(stream.toByteArray())));
    }
    public boolean typeValidator(String type) {
        return Arrays.asList(types.split(", ")).contains(type.substring(6));
    }
}