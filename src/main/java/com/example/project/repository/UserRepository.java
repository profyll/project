package com.example.project.repository;

import com.example.project.entity.SearchHistory;
import com.example.project.entity.Song;
import com.example.project.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserRepository {

    public int create(User user);
    User findById(int id);
    public User findByEmail(@Param("email") String email);
    public User findByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);
    List<Song> findLikedSongsByUserId(@Param("userId") int userId);

}
