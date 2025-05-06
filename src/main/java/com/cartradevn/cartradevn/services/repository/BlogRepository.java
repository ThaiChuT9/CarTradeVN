package com.cartradevn.cartradevn.services.repository;

import com.cartradevn.cartradevn.services.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    // Nếu cần thêm các truy vấn tùy chỉnh, có thể khai báo tại đây
}

