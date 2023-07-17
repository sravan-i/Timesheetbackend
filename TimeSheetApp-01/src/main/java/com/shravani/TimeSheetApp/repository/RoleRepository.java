package com.shravani.TimeSheetApp.repository;

import com.shravani.TimeSheetApp.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findByName(String name);
}
