package model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Kyza on 26.06.2016.
 */
public class User {
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final int id;

    private String name;

    private String surname;

    private long phonenum;



    public User(String name, String surname,  long phonenum) {
        this.id = COUNTER.getAndIncrement();
        this.name = name;
        this.surname = surname;

        this.phonenum = phonenum;
    }

    public User() {
        this.id = COUNTER.getAndIncrement();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(int phonenum) {
        this.phonenum = phonenum;
    }


}
