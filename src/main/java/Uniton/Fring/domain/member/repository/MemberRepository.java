package Uniton.Fring.domain.member.repository;

import Uniton.Fring.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByUsername(String username);

    List<Member> findTop8ByIsRecipeMemberTrueOrderByLikeCountDesc();
    List<Member> findTop8ByIsFarmerTrueOrderByLikeCountDesc();

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);

    // --- 레시피 멤버 전용 검색 ---
    @Query(value = """
        SELECT *
        FROM member m
        WHERE m.is_recipe_member = TRUE
          AND m.nickname LIKE CONCAT('%', :keyword, '%')
        ORDER BY
            CASE
                WHEN m.nickname = :keyword                     THEN 0
                WHEN m.nickname LIKE CONCAT(:keyword, '%')     THEN 1
                WHEN m.nickname LIKE CONCAT('%', :keyword)     THEN 2
                WHEN m.nickname LIKE CONCAT('%', :keyword, '%')THEN 3
                ELSE 4
            END
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM member m
        WHERE m.is_recipe_member = TRUE
          AND m.nickname LIKE CONCAT('%', :keyword, '%')
        """,
            nativeQuery = true)
    Page<Member> searchRecipeMembers(@Param("keyword") String keyword, Pageable pageable);

    // --- 농부(판매자) 멤버 전용 검색 ---
    @Query(value = """
        SELECT *
        FROM member m
        WHERE m.is_farmer = TRUE
          AND m.nickname LIKE CONCAT('%', :keyword, '%')
        ORDER BY
            CASE
                WHEN m.nickname = :keyword                     THEN 0
                WHEN m.nickname LIKE CONCAT(:keyword, '%')     THEN 1
                WHEN m.nickname LIKE CONCAT('%', :keyword)     THEN 2
                WHEN m.nickname LIKE CONCAT('%', :keyword, '%')THEN 3
                ELSE 4
            END
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM member m
        WHERE m.is_farmer = TRUE
          AND m.nickname LIKE CONCAT('%', :keyword, '%')
        """,
            nativeQuery = true)
    Page<Member> searchFarmerMembers(@Param("keyword") String keyword, Pageable pageable);
}
