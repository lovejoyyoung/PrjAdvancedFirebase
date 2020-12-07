package model;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Person {
    private int id;
    private String name;
    private String photo;
    private Car car;
    private ArrayList<String> hobbies;

    public Person() {
    }

    public Person(int id, String name, String photo, Car car, ArrayList<String> hobbies) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.car = car;
        this.hobbies = hobbies;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public ArrayList<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(ArrayList<String> hobbies) {
        this.hobbies = hobbies;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
