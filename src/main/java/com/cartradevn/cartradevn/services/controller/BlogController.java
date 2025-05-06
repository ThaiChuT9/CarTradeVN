package com.cartradevn.cartradevn.services.controller;

import com.cartradevn.cartradevn.services.entity.Blog;
import com.cartradevn.cartradevn.services.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/blog")
public class BlogController {

    private final BlogService blogService;

    // Constructor để inject BlogService
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    // Hiển thị danh sách bài viết
    @GetMapping
    public String showBlogList(Model model) {
        List<Blog> blogs = blogService.getAllBlogs();
        model.addAttribute("blogs", blogs);
        return "blog-list-02"; // Trả về template Thymeleaf hiển thị danh sách
    }

    // Hiển thị chi tiết bài viết theo ID
    @GetMapping("/{id}")
    public String showBlogDetail(@PathVariable Long id, Model model) {
        Blog blog = blogService.getBlogById(id);
        if (blog == null) {
            return "error"; // Nếu không tìm thấy, chuyển hướng đến trang lỗi
        }
        model.addAttribute("blog", blog);
        return "blog-single"; // Trả về template Thymeleaf hiển thị bài viết
    }

    // Tạo bài viết mới (Form POST)
    @PostMapping("/create")
    public String createBlog(@ModelAttribute Blog blog) {
        blogService.createBlog(blog);
        return "redirect:/blog"; // Sau khi tạo, quay về danh sách blog
    }

    // Sửa bài viết
    @PostMapping("/update/{id}")
    public String updateBlog(@PathVariable Long id, @ModelAttribute Blog updatedBlog) {
        blogService.updateBlog(id, updatedBlog);
        return "redirect:/blog/" + id; // Sau khi sửa, quay về bài viết đó
    }

    // Xóa bài viết
    @GetMapping("/delete/{id}")
    public String deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return "redirect:/blog"; // Sau khi xóa, quay về danh sách blog
    }
}

