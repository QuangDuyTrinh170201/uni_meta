package com.project.uni_meta.controllers;

import com.project.uni_meta.dtos.*;
import com.project.uni_meta.exceptions.DataNotFoundException;
import com.project.uni_meta.models.User;
import com.project.uni_meta.services.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final IUserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult result){
        try{
            if(result.hasErrors())
            {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())){
                return ResponseEntity.badRequest().body("Password does not match");
            }
            User user = userService.CreateUser(userDTO);
            //return ResponseEntity.ok("Create user successfully");
            return ResponseEntity.ok(user);

        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        try {
            Map<String, Object> response = userService.Login(userLoginDTO.getUserName(), userLoginDTO.getPassword());
            return ResponseEntity.ok(response);
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/details")
    public ResponseEntity<?> getUserById(Long id, @RequestHeader("Authorization") String authorizationHeader){
        try {
            String extractedToken = authorizationHeader.substring(7);
            User userDetailsFromToken = userService.getUserDetailsFromToken(extractedToken);
//            Optional<User> user = userService.getUserById(id);
            return ResponseEntity.ok(userDetailsFromToken);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MARKETING_MANAGER')")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUserByAdmin(@PathVariable Long userId, @RequestBody UpdateUserDTO updatedUserDTO) {
        try {
            User updatedUser = userService.updateUserInforByAdmin(userId, updatedUserDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user information: " + e.getMessage());
        }
    }

    @GetMapping("/downloads/all")
    @PreAuthorize("hasRole('ROLE_MARKETING_MANAGER')")
    public ResponseEntity<?> downloadAllFiles() {
        try {
            // Thư mục chứa tất cả các tệp cần tải về
            Path folderPath = Paths.get("uploads");

            // Kiểm tra xem thư mục tồn tại không
            if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
                return ResponseEntity.badRequest().body("Upload folder does not exist");
            }

            // Tạo tệp zip
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                Files.walk(folderPath)
                        .filter(Files::isRegularFile)
                        .forEach(file -> {
                            try {
                                Path relativePath = folderPath.relativize(file);
                                ZipEntry zipEntry = new ZipEntry(relativePath.toString());
                                zos.putNextEntry(zipEntry);
                                Files.copy(file, zos);
                                zos.closeEntry();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }

            // Trả về tệp zip
            byte[] zipBytes = baos.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename("all_files.zip").build());
            return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while creating zip file");
        }
    }

    @PostMapping("/sendMailChangePassword")
    public ResponseEntity<Boolean> create(
            @RequestBody MailDTO mailDTO
    ) throws DataNotFoundException {
        return ResponseEntity.ok(userService.sendMailPassword(mailDTO));
    }

    @PutMapping("/changePassword/{id}")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody UserInforDTO userInforDTO, @RequestHeader("Authorization") String authorizationHeader){
        try{
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            //Ensure that the user making the request matches the user being updated
            if(!Objects.equals(user.getId(), id)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User changUserPassword = userService.changePassword(id, userInforDTO);
            return ResponseEntity.ok(changUserPassword);
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/avatar/{userId}")
    public ResponseEntity<?> updateAvatar(@PathVariable Long userId,
                                                @ModelAttribute MultipartFile file, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            //Ensure that the user making the request matches the user being updated
            if(!Objects.equals(user.getId(), userId)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            String imageUpdate = storeFile(file);
            User updateAvatar = userService.updateUserImage(userId, imageUpdate);
            return ResponseEntity.ok().body("Import image successfully!");
        } catch (DataNotFoundException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFileName = UUID.randomUUID().toString() + "_" + filename;
        Path uploadDir = Paths.get("upload_images");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Đường dẫn đến file đích
        Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName){
        try{
            Path imagePath = Paths.get("upload_images/"+imageName);
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

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) throws Exception {
        try{
            userService.deleteUser(id);
            return ResponseEntity.ok().body("Delete user successfully");
        }catch (Exception ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
