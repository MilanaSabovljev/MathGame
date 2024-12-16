package com.example.igraracunanja;

public class Users {

    private String firstName;
    private String lastName;
    private String username; // Promenjeno iz 'userName' u 'username'
    private String email;
    private String password;
    private int age;

    // Potreban prazan konstruktor za Firebase
    public Users() {
    }

    public Users(String firstName, String lastName, String username, String email, String password, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username; // Promenjeno
        this.email = email;
        this.password = password;
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() { // Promenjeno iz 'getUserName' u 'getUsername'
        return username;
    }

    public void setUsername(String username) { // Promenjeno iz 'setUserName' u 'setUsername'
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
