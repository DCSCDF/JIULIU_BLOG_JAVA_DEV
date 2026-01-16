package com.jiuliu.myblog_dev.dto.user;


import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String new_password; //new password
    private String old_password;
}
