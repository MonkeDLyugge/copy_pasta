package com.lyugge.dao;

import com.lyugge.api.enums.Access;
import com.lyugge.entity.AppPaste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PasteDao extends JpaRepository<AppPaste, Long> {
    List<AppPaste> findAllByAccess(Access access);
}
