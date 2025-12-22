package com.consorcio.api.repository;

import com.consorcio.api.domain.enums.GroupRole;
import com.consorcio.api.domain.enums.GroupStatus;
import com.consorcio.api.model.GroupModel;
import com.consorcio.api.model.UserGroupModel;
import com.consorcio.api.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroupModel, Long> {

    // =========================
    // PARTICIPAÇÃO
    // =========================
    Optional<UserGroupModel> findByUserAndGroup(UserModel user, GroupModel group);

    boolean existsByUserAndGroup(UserModel user, GroupModel group);

    // =========================
    // PAPEL / PERMISSÃO
    // =========================
    boolean existsByUserAndGroupAndRole(
            UserModel user,
            GroupModel group,
            GroupRole role
    );

    // =========================
    // LISTAGEM
    // =========================
    List<UserGroupModel> findAllByGroup(GroupModel group);

    List<UserGroupModel> findAllByUser(UserModel user);

    // =========================
    // CONTAGEM
    // =========================
    long countByGroup(GroupModel group);

    long countByGroupAndRole(GroupModel group, GroupRole role);

    // =========================
    // SEGURANÇA DE DOMÍNIO
    // =========================
    @Query("""
        SELECT COUNT(ug) > 0
        FROM UserGroupModel ug
        WHERE ug.group = :group
          AND ug.role = 'ADMIN'
    """)
    boolean groupHasAdmin(GroupModel group);

    // =========================
    // APOIO AO DELETE /me
    // =========================
    @Query("""
        SELECT COUNT(ug) > 0
        FROM UserGroupModel ug
        WHERE ug.user = :user
          AND ug.role = 'ADMIN'
          AND ug.group.status = :status
    """)
    boolean userIsAdminOfGroupWithStatus(UserModel user, GroupStatus status);
}
