package com.project.uni_meta.services;

import com.project.uni_meta.dtos.ArticleDTO;
import com.project.uni_meta.dtos.ArticleImageDTO;
import com.project.uni_meta.dtos.DataMailDTO;
import com.project.uni_meta.dtos.MailDTO;
import com.project.uni_meta.exceptions.DataNotFoundException;
import com.project.uni_meta.models.*;
import com.project.uni_meta.repositories.*;
import com.project.uni_meta.responses.ArticleResponse;
import com.project.uni_meta.utils.Const;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ArticleService implements IArticleService{
    private final ArticleRepository articleRepository;
    private final AcademicYearRepository academicYearRepository;
    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final IMailService mailService;
    private final ClosureRepository closureRepository;
    @Override
    public Page<ArticleResponse> getAllArticles(String keyword, Long userId, Long facultyId, PageRequest pageRequest) {
        Page<Article> articlePage;
        articlePage = articleRepository.searchArticles(keyword, userId, facultyId, pageRequest);
        return articlePage.map(ArticleResponse::fromArticle);
    }

    public List<ArticleResponse> getArticlesByAllParam(String keyword, Long userId, Long facultyId, Long academicYearId){
        List<Article> articles;
        articles = articleRepository.getAllWithKeywords(keyword, userId, facultyId, academicYearId);
        return articles.stream().map(ArticleResponse::fromArticle).toList();
    }

    public List<ArticleResponse> getArticlesByUserId(Long userId) throws DataNotFoundException {
        List<Article> existingArticles = articleRepository.findByUserId(userId);
        return existingArticles.stream().map(ArticleResponse::fromArticle).toList();
    }

    @Override
    public Article addArticle(ArticleDTO articleDTO) throws Exception {
        AcademicYear currentAcademic = academicYearRepository.findById(articleDTO.getAcademicId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find this year"));
        Faculty existingFaculty = facultyRepository.findById(articleDTO.getFacultyId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find faculty"));
        User submitUser = userRepository.findById(articleDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        Article newArticle = new Article();
        newArticle.setName(articleDTO.getName());
        newArticle.setDescription(articleDTO.getDescription());
        newArticle.setFaculty(existingFaculty);
        newArticle.setAcademicYear(currentAcademic);
        newArticle.setUser(submitUser);
        LocalDateTime submissionDate = articleDTO.getSubmissionDate() == null ? LocalDateTime.now() : articleDTO.getSubmissionDate();
        newArticle.setSubmissionDate(submissionDate);
        newArticle.setStatus(articleDTO.getStatus());
        newArticle.setPublish(false);
        newArticle.setView(articleDTO.getView());

        newArticle = articleRepository.save(newArticle);

        return newArticle;
    }

    @Override
    @Transactional
    public Article updateArticleFile(Long articleId, String fileName) throws DataNotFoundException {
        // Tìm bài báo theo ID
        Article existingArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find the article with id: " + articleId));

        // Thêm tên tệp vào cột filename của bài báo
        existingArticle.setFileName(fileName);
        return articleRepository.save(existingArticle);
    }

    @Override
    public Image createArticleImage(Long articleId, ArticleImageDTO articleImageDTO) throws Exception{
        Article existingArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find this Article!"));
        List<Image> checkMaximum = imageRepository.findByArticleId(articleId);
//        if(checkMaximum.size()>=3){
//            throw new Exception("Cannot import any image, maximum is 3 images per article!");
//        }
        Image newArticleImage = Image.builder()
                .article(existingArticle)
                .imageUrl(articleImageDTO.getImageUrl())
                .build();
        int size = imageRepository.findByArticleId(articleId).size();
        if(size > Image.MAXIMUM_IMAGES_PER_PRODUCT){
            throw new Exception("Number of image must be <= "+Image.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return imageRepository.save(newArticleImage);
    }

    @Override
    public Article getArticleById(long articleId) throws Exception {
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        if(optionalArticle.isPresent()) {
            return optionalArticle.get();
        }
        throw new DataNotFoundException("Cannot find article with id =" + articleId);
    }


    @Override
    @Transactional
    public Article updateArticle(Long id, ArticleDTO articleDTO) throws Exception {
        Article existingArticle = articleRepository.getById(id);

        // Kiểm tra và cập nhật AcademicYear
        if (articleDTO.getAcademicId() != null) {
            AcademicYear exAcademicYear = academicYearRepository.findById(articleDTO.getAcademicId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find this academic year with this id: " + articleDTO.getAcademicId()));
            existingArticle.setAcademicYear(exAcademicYear);
        }

        // Kiểm tra và cập nhật Faculty
        if (articleDTO.getFacultyId() != null) {
            Faculty exFaculty = facultyRepository.findById(articleDTO.getFacultyId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find this faculty with this id: " + articleDTO.getFacultyId()));
            existingArticle.setFaculty(exFaculty);
        }

        if(articleDTO.getPublish() != null){
            existingArticle.setPublish(articleDTO.getPublish());
        }

        // Kiểm tra và cập nhật User
        if (articleDTO.getUserId() != null) {
            User exUser = userRepository.findById(articleDTO.getUserId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find this academic year with this id: " + articleDTO.getUserId()));
            existingArticle.setUser(exUser);
        }

        // Kiểm tra và cập nhật các trường thông tin khác
        if (articleDTO.getName() != null) {
            existingArticle.setName(articleDTO.getName());
        }
        if (articleDTO.getDescription() != null) {
            existingArticle.setDescription(articleDTO.getDescription());
        }
        if(Objects.equals(articleDTO.getStatus(), "accepted")){
            existingArticle.setStatus(articleDTO.getStatus());
        } else if (Objects.equals(articleDTO.getStatus(), "rejected")){
            Path rejectedFile = Paths.get("uploads", articleDTO.getFileName());
            Path moveToRejectedFolder = Paths.get("files_rejected", articleDTO.getFileName());
            Files.move(rejectedFile, moveToRejectedFolder);
            existingArticle.setStatus(articleDTO.getStatus());
        } else{
            existingArticle.setStatus("pending"); // Có thể cập nhật status mặc định ở đây
        }
        existingArticle.setSubmissionDate(articleDTO.getSubmissionDate() != null ? articleDTO.getSubmissionDate() : LocalDateTime.now());

        // Lưu các thay đổi vào cơ sở dữ liệu và trả về bài báo đã được cập nhật
        return articleRepository.save(existingArticle);
    }

    @Override
    public void deleteArticle(Long id) throws Exception {
        Optional<Article> existingArticle = articleRepository.findById(id);

        if(existingArticle.isPresent()){
            Path deleteFile = Paths.get("uploads/", existingArticle.get().getFileName());
            File file = new File(String.valueOf(deleteFile));
            file.delete();
            existingArticle.ifPresent(articleRepository::delete);
        }
    }

    @Override
    public boolean sendMail(MailDTO mailDTO) {
        try {
            DataMailDTO dataMail = new DataMailDTO();

            List<User> findUserWithFaculty = userRepository.findByFacultyId(mailDTO.getFacultyId());

            // Danh sách địa chỉ email của các MARKETING_COORDINATOR
            List<String> coordinatorEmails = new ArrayList<>();

            for (User user : findUserWithFaculty) {
                if (user.getRole().getName().equals(Role.MARKETING_COORDINATOR)) {
                    // Nếu người dùng có vai trò là MARKETING_COORDINATOR, thêm địa chỉ email của họ vào danh sách
                    coordinatorEmails.add(user.getEmail());
                }
            }

            // Gửi email cho tất cả MARKETING_COORDINATOR trong danh sách
            for (String email : coordinatorEmails) {
                dataMail.setTo(email);
                dataMail.setSubject(Const.SEND_EMAIL_SUBJECT.CLIENT_NOTIFICATION);
                Map<String, Object> props = new HashMap<>();
                props.put("url", mailDTO.getUrl());
                dataMail.setProps(props);
                mailService.sendHtmlMail(dataMail, Const.TEMPLATE_FILE_NAME.CLIENT_NOTIFICATION);
            }

            return true;
        } catch (MessagingException exp) {
            exp.printStackTrace();
            return false;
        }
    }


    @Override
    public List<Image> getImagesByArticleId(Long articleId){
        return imageRepository.findByArticleId(articleId);
    }

    @Override
    public void deleteImage(Long id){
        Optional<Image> findImage = imageRepository.findById(id);
        if(findImage.isEmpty())
        {
            throw new DataIntegrityViolationException("Cannot find this image!");
        }
        Path deleteImage = Paths.get("upload_images/", findImage.get().getImageUrl());
        File file = new File(String.valueOf(deleteImage));
        file.delete();
        findImage.ifPresent(imageRepository::delete);
    }
}
