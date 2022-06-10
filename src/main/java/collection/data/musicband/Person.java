package collection.data.musicband;

import collection.data.exceptions.InvalidFieldException;

import java.io.Serializable;
import java.util.Date;
import java.util.Stack;

public class Person implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private java.util.Date birthday; //Поле не может быть null
    private String passportID; //Длина строки должна быть не меньше 5, Длина строки не должна быть больше 49, Строка не может быть пустой, Поле может быть null
    private Color eyeColor; //Поле может быть null
    private Location location; //Поле не может быть null

    {
        location = new Location();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name==null) throw new InvalidFieldException("name", "null");
        if(name.equals("")) throw new InvalidFieldException("name", name);
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        if(birthday==null) throw new InvalidFieldException("birthday", "null");
        this.birthday = birthday;
    }

    public String getPassportID() {
        return passportID;
    }

    public void setPassportID(String passportID) {
        if(passportID.length() < 5 || passportID.length() > 49) throw new InvalidFieldException("passportID", passportID);
        this.passportID = passportID;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if(location==null) throw new InvalidFieldException("location", "null");
        this.location = location;
    }
}
