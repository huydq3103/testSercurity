package com.example.demo.hybrid.service;

import com.example.demo.hybrid.entity.UsersEntity;

public interface IUserService {
     UsersEntity saveUser(UsersEntity user) throws Exception;

     UsersEntity getUser(Long id,String publicKeyContent);
}
