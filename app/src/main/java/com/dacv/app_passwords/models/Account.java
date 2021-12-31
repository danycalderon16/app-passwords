package com.dacv.app_passwords.models;

public class Account {

    private String name;
    private String email;
    private String pass;
    private String key;

    public Account(String name, String email, String pass, String key) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", pass='" + pass + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
