package com.illiterate.illiterate.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDto {
    private int id;
    private String userid;
    private String username;
    private String password;
    private String phonenum;
}
