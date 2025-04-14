package com.example.project.Repository;

import com.example.project.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserRepository {

    public int create(User user);
    public User findByEmail(@Param("email") String email);
    public User findByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);
}
