package com.cartradevn.cartradevn.administration.respository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cartradevn.cartradevn.administration.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    // Tìm kiếm người dùng theo tên đăng nhập
    Optional<User> findByUsername(String username);
    // Tìm kiếm người dùng theo email
    User findByEmail(String email);
    // Kiểm tra xem người dùng có tồn tại hay không
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    //Tìm kiếm người dùng theo username và email
    Page<User> findByUsernameOrEmail(String username, String email, org.springframework.data.domain.Pageable pageable);
}
