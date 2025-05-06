package com.cartradevn.cartradevn.services.service;

import com.cartradevn.cartradevn.services.entity.Blog;
import com.cartradevn.cartradevn.services.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    // Constructor để inject BlogRepository
    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    // Lấy danh sách tất cả bài viết
    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    // Lấy thông tin bài viết theo ID
    public Blog getBlogById(Long id) {
        Optional<Blog> blog = blogRepository.findById(id);
        return blog.orElse(null); // Trả về null nếu không tìm thấy bài viết
    }

    // Thêm bài viết mới
    public Blog createBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    // Cập nhật bài viết
    public Blog updateBlog(Long id, Blog updatedBlog) {
        Optional<Blog> existingBlog = blogRepository.findById(id);
        if (existingBlog.isPresent()) {
            Blog blog = existingBlog.get();
            blog.setTitle(updatedBlog.getTitle());
            blog.setContent(updatedBlog.getContent());
            blog.setAuthor(updatedBlog.getAuthor());
            blog.setCreatedDate(updatedBlog.getCreatedDate());
            blog.setImage(updatedBlog.getImage());
            return blogRepository.save(blog);
        }
        return null; // Trả về null nếu không tìm thấy bài viết để cập nhật
    }

    // Xóa bài viết
    public boolean deleteBlog(Long id) {
        if (blogRepository.existsById(id)) {
            blogRepository.deleteById(id);
            return true;
        }
        return false; // Trả về false nếu không tìm thấy bài viết để xóa
    }
}
