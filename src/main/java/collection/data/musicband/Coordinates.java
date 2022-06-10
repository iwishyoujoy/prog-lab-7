package collection.data.musicband;

import collection.data.exceptions.InvalidFieldException;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private double x; //Максимальное значение поля: 37
    private Integer y; //Максимальное значение поля: 626, Поле не может быть null

    public double getX() {
        return x;
    }

    public void setX(double x) {
        if(x > 37) throw new InvalidFieldException("x", String.valueOf(x));
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        if(y == null) throw new InvalidFieldException("y", "null");
        if(y > 626) throw new InvalidFieldException("y", y.toString());
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%f; %d)", x,y);
    }
}
