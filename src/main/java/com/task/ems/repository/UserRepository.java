package com.task.ems.repository;

import com.task.ems.model.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    @Query(value = "SELECT * FROM user u WHERE u.username = ?1", nativeQuery = true)
    UserEntity findByUsername(String username);

    @Query(value = "SELECT * FROM user u WHERE (?1 IS NULL OR u.department_id = ?1) AND (?2 IS NULL OR u.username LIKE ?2)", nativeQuery = true)
    Iterable<UserEntity> findByDepartmentIdOrUserName(Long departmentId, String username);
}
