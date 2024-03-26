package com.project.uni_meta.controllers;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.project.uni_meta.dtos.ArticleDTO;
import com.project.uni_meta.dtos.ArticleImageDTO;
import com.project.uni_meta.dtos.MailDTO;
import com.project.uni_meta.exceptions.DataNotFoundException;
import com.project.uni_meta.models.Article;
import com.project.uni_meta.models.Image;
import com.project.uni_meta.services.IArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/articles")
public class ArticleController {
    private final IArticleService articleService;
    @PostMapping
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<Article> addArticle(@RequestBody ArticleDTO articleDTO) {
        try {
            Article newArticle = articleService.addArticle(articleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newArticle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/{articleId}/file")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> updateArticleFile(@PathVariable("articleId") Long articleId,
                                               @RequestParam("file") MultipartFile file) {
        try {
            // Lưu tệp vào thư mục uploads
            String fileUpdate = storeFile(file);
            // Cập nhật thông tin bài báo với tên tệp mới
            Article updatedArticle = articleService.updateArticleFile(articleId, fileUpdate);
            return ResponseEntity.ok().body("Import file successfully!");
        } catch (DataNotFoundException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        // Xử lý tên tệp để đảm bảo tính duy nhất
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFileName = UUID.randomUUID().toString() + "_" + filename;
        // Lưu tệp vào thư mục uploads
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    private boolean isDocumentFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("application/pdf") || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
    }

    @GetMapping("/downloads/{fileName}")
    public ResponseEntity<?> viewFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("uploads", fileName);
            UrlResource resource = new UrlResource(filePath.toUri());


            if (resource.exists()) {
                MediaType mediaType = MediaType.parseMediaType(
                        determineMimeType(fileName));

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.badRequest().body("Ops, somethings wrong T.T");
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String determineMimeType(String fileName) {
        String[] parts = fileName.split("\\.");
        if (parts.length > 1) {
            String extension = parts[parts.length - 1];
            switch (extension.toLowerCase()) {
                case "pdf":
                    return "application/pdf";
                case "docx":
                    return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }
        }
        // Default "application/octet-stream" if do not find the path
        return "application/octet-stream";
    }

    @GetMapping("/views/{fileName}")
    public ResponseEntity<?> viewFileInMarketing(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("uploads", fileName);
            UrlResource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                // Xác định loại mime của file dựa trên phần mở rộng của tên file
                MediaType mediaType = MediaType.parseMediaType(
                        determineMimeType(fileName));
                    // Nếu là file docx, chuyển đổi sang PDF và trả về
                    File convertedFile = convertDocxToPdf(filePath);
                    UrlResource pdfResource = new UrlResource(convertedFile.toURI());

                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_PDF)
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + pdfResource.getFilename() + "\"")
                            .body(pdfResource);
            }

            return ResponseEntity.badRequest().body("Ops, something's wrong T.T");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Chuyển đổi file docx sang pdf
    private File convertDocxToPdf(Path docxFilePath) throws Exception {
        Document doc = new Document(docxFilePath.toString());
        File pdfFile = Files.createTempFile(UUID.randomUUID().toString(), ".pdf").toFile();
        doc.save(pdfFile.getAbsolutePath(), SaveFormat.PDF);
        return pdfFile;
    }

    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> uploadImages(@PathVariable("id") Long articleId, @ModelAttribute List<MultipartFile> files){
        try {
            Article existingArticle = articleService.getArticleById(articleId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if(files.size() > Image.MAXIMUM_IMAGES_PER_PRODUCT){
                return ResponseEntity.badRequest().body("You can only upload maximum 3 images");
            }
            List<Image> articleImages = new ArrayList<>();
            String firstImageName = null; // Lưu tên của ảnh đầu tiên
            for(MultipartFile file : files){
                if(file.getSize() == 0){
                    continue;
                }
                // kiểm tra kích thước file và định dạng file
                if(file.getSize() > 10 * 1024 * 1024){
                    // kích thước > 10 mb
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large, maximum file size is 10MB");
                }
                String contentType = file.getContentType();
                if(contentType == null || !contentType.startsWith("image/")){
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");
                }
                // lưu file và cập nhật thumbnail trong DTO
                String filename = storeImageFile(file);
                // lưu vào đối tượng product trong db
                Image articleImage = articleService.createArticleImage(existingArticle.getId(),
                        ArticleImageDTO
                                .builder()
                                .imageUrl(filename)
                                .build());
                articleImages.add(articleImage);
            }
            return ResponseEntity.ok().body(articleImages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private String storeImageFile(MultipartFile file)throws IOException{
        if(!isImageFile(file) || file.getOriginalFilename() == null){
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        //thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFileName = UUID.randomUUID().toString() + "_" + filename;
        java.nio.file.Path uploadDir = Paths.get("uploads");
        if(!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }
        //Đường dẫn đến file đích
        Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    private boolean isImageFile(MultipartFile file){
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName){
        try{
            Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if(resource.exists()){
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }else{
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
            }
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/sendMail")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<Boolean> create(
            @RequestBody MailDTO mailDTO
    ) {
        return ResponseEntity.ok(articleService.sendMail(mailDTO));
    }

}
