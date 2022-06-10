package collection.data.musicband;

import collection.data.exceptions.InvalidFieldException;

import java.io.Serializable;

public class Location implements Serializable {
    private Integer x; //Поле не может быть null
    private double y;
    private float z;

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        if(x==null) throw new InvalidFieldException("x", "null");
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("(%d; %f; %f)", x, y, z);
    }
}
