package com.project.uni_meta.services;

import com.project.uni_meta.dtos.ArticleDTO;
import com.project.uni_meta.exceptions.DataNotFoundException;
import com.project.uni_meta.models.AcademicYear;
import com.project.uni_meta.models.Article;
import com.project.uni_meta.models.Faculty;
import com.project.uni_meta.models.User;
import com.project.uni_meta.repositories.AcademicYearRepository;
import com.project.uni_meta.repositories.ArticleRepository;
import com.project.uni_meta.repositories.FacultyRepository;
import com.project.uni_meta.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService implements IArticleService{
    private final ArticleRepository articleRepository;
    private final AcademicYearRepository academicYearRepository;
    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
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
    public Article updateArticle(Long id, ArticleDTO articleDTO) throws Exception {
        return null;
    }

    @Override
    public void deleteArticle(Long id) throws Exception {

    }
}
