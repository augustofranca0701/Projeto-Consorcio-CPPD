package com.consorcio.api.Repository;

import com.consorcio.api.Model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long>
{
    UserModel findByEmail(String email);
}
