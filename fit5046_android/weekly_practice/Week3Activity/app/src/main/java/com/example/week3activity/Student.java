package com.example.week3activity;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {
    private String name;
    private String surname;
    private int age;

    public Student(){
        name = "";
        String surname = "";
        int age = 0;
    }
    public Student(Parcel in){
        this.name = in.readString();
        this.surname = in.readString();
        this.age = in.readInt();
    }
    public Student(String name, String surname, int age){
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeInt(age);
    }
    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };
    public void setName(String name){this.name = name;}
    public void setSurname(String surname){this.surname = surname;}
    public void setAge(int age){this.age = age;}
    public String getName(){return name;}
    public String getSurname(){return  surname;}
    public int getAge(){return age;}
    public String StrStudent(){return "Your name: " + name + '\n' + "Your surname: " + surname + '\n' + "Your age: " + age;}
}
