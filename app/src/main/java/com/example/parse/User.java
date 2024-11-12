package com.example.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

public class User extends ParseObject {

    String name;
    String lastName;
    int age;
    //double latitude;
    //double longitude;

    public User() {
    }

    public User(String theClassName, String name, String lastName, int age) {
        super(theClassName);
        this.name = name;
        this.lastName = lastName;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                '}';
    }
}
