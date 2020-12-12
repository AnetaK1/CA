package sample;

import java.awt.*;

public class Cell {
    int state;
    int x;
    int y;
    javafx.scene.paint.Color cellColor;

    public Cell(){
        x = 1;
        y = 1;
        state = 0;
        cellColor = javafx.scene.paint.Color.WHITE;
    }
    public Cell(int x, int y){
        this.x = x;
        this.y = y;
    }
    public Cell(int state, int x, int y, javafx.scene.paint.Color cellColor) {
        this.state = state;
        this.x = x;
        this.y = y;
        this.cellColor = cellColor;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPosition(int x, int y){
        setX(x);
        setY(y);
    }

    public double getDistance(int x1, int y1) {
        double dist = 0;
        dist = Math.sqrt((this.x - x1)*(this.x - x1) + Math.pow(this.y - y1, 2));

        return dist;
    }
    public javafx.scene.paint.Color getCellColor() {
        return cellColor;
    }

    public void setCellColor(javafx.scene.paint.Color cellColor) {
        this.cellColor = cellColor;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                ", state = "+ state+
                '}';
    }
}
