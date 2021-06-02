package com.razzzil.sphynx.coordinator.repository;

import com.razzzil.sphynx.commons.model.user.UserModel;
import com.razzzil.sphynx.coordinator.jooq.tables.UserTable;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.ONE;

@Repository
public class UserRepository {

    private static final UserTable USER_TABLE = UserTable.USER_TABLE;

    @Autowired
    private DSLContext dsl;

    public List<UserModel> getAll() {
        return this.dsl
                .select()
                .from(USER_TABLE)
                .fetchInto(UserModel.class);
    }

    public Optional<UserModel> getById(Integer id) {
        return this.dsl
                .select()
                .from(USER_TABLE)
                .where(USER_TABLE.ID.eq(id))
                .fetchOptionalInto(UserModel.class);
    }

    public Optional<UserModel> getByLoginOrEmail(String loginOrEmail) {
        return this.dsl
                .select()
                .from(USER_TABLE)
                .where(USER_TABLE.LOGIN.eq(loginOrEmail).or(USER_TABLE.EMAIL.eq(loginOrEmail)))
                .fetchOptionalInto(UserModel.class);
    }

    public Optional<UserModel> getByLogin(String login) {
        return this.dsl
                .select()
                .from(USER_TABLE)
                .where(USER_TABLE.LOGIN.eq(login))
                .fetchOptionalInto(UserModel.class);
    }

    public Optional<UserModel> getByEmail(String email) {
        return this.dsl
                .select()
                .from(USER_TABLE)
                .where(USER_TABLE.EMAIL.eq(email))
                .fetchOptionalInto(UserModel.class);
    }

    public boolean update(UserModel userModel) {
        return this.dsl
                .update(USER_TABLE)
                .set(USER_TABLE.LOGIN, userModel.getLogin())
                .set(USER_TABLE.EMAIL, userModel.getEmail())
                .set(USER_TABLE.HASHED_PASSWORD, userModel.getHashedPassword())
                .set(USER_TABLE.ROLE, userModel.getRole().name())
                .where(USER_TABLE.ID.eq(userModel.getId()))
                .execute() == ONE;
    }

    public boolean delete(Integer id) {
        return dsl.delete(USER_TABLE)
                .where(USER_TABLE.ID.eq(id))
                .execute() == ONE;
    }

    public UserModel save(UserModel userModel) {
        return dsl.insertInto(USER_TABLE)
                .set(USER_TABLE.LOGIN, userModel.getLogin())
                .set(USER_TABLE.EMAIL, userModel.getEmail())
                .set(USER_TABLE.HASHED_PASSWORD, userModel.getHashedPassword())
                .set(USER_TABLE.ROLE, userModel.getRole().name())
                .returning(USER_TABLE.ID, USER_TABLE.LOGIN, USER_TABLE.EMAIL, USER_TABLE.HASHED_PASSWORD, USER_TABLE.ROLE)
                .fetchOne()
                .map(r -> r.into(USER_TABLE).into(UserModel.class));
    }


}
