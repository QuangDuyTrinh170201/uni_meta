package com.project.uni_meta.services;

import com.project.uni_meta.dtos.ArticleDTO;
import com.project.uni_meta.dtos.ArticleImageDTO;
import com.project.uni_meta.exceptions.DataNotFoundException;
import com.project.uni_meta.models.*;
import com.project.uni_meta.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService implements IArticleService{
    private final ArticleRepository articleRepository;
    private final AcademicYearRepository academicYearRepository;
    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
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
        newArticle.setView(articleDTO.getView());

        newArticle = articleRepository.save(newArticle);

        return newArticle;
    }

    @Override
    @Transactional
    public Article updateArticleFile(Long articleId, String fileName) throws DataNotFoundException {
        // Tìm bài báo theo ID
        Article existingArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new DataNotFoundException("Không thể tìm thấy bài báo với id: " + articleId));

        // Thêm tên tệp vào cột filename của bài báo
        existingArticle.setFileName(fileName);
        return articleRepository.save(existingArticle);
    }

    @Override
    public Image createArticleImage(Long articleId, ArticleImageDTO articleImageDTO) throws Exception{
        Article existingArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find this Article!"));
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
    public Article updateArticle(Long id, ArticleDTO articleDTO) throws Exception {
        return null;
    }

    @Override
    public void deleteArticle(Long id) throws Exception {

    }
}
