package ru.practicum.scooter.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor

public class Login {

private String login;
private String password;


public static Login from(Courier courier) {
    return new Login(courier.getLogin(), courier.getPassword());
}

}