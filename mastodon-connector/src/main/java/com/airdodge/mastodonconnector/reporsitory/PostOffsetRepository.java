package com.airdodge.mastodonconnector.reporsitory;

import com.airdodge.mastodonconnector.domain.PostOffset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostOffsetRepository extends JpaRepository<PostOffset, String> {

    Optional<PostOffset> findTopByOrderByCreatedAtDesc();

}
