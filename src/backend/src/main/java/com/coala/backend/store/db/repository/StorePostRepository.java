package com.coala.backend.store.db.repository;

import com.coala.backend.member.db.entity.Member;
import com.coala.backend.store.db.entity.StorePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StorePostRepository extends JpaRepository<StorePost, Long> {

    List<StorePost> findByMember(Member member);
}
