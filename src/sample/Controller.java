package sample;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;


import java.net.URL;

import java.util.Random;
import java.util.ResourceBundle;

import static java.lang.Math.round;
import static java.lang.System.exit;
import static sample.BoundaryCondition.absorbic;
import static sample.BoundaryCondition.periodic;
import static sample.Neighbourhood.*;
import static sample.Nucleation.*;

public class Controller implements Initializable {

    public Canvas matrix;
    public ChoiceBox BC;
    public ChoiceBox type;
    public TextField h;
    public TextField w;
    public TextField rowAmount;
    public TextField columnAmount;
    public TextField radius;
    public Button start;
    public TextField amount;

    public CA automat;
    public GridPane gridPane;
    public javafx.scene.layout.Pane Pane;
    public ScrollPane scroll;
    public ChoiceBox squareSize;
    public ChoiceBox neighbourhood;
    public Button Allgrowth;
    public Button step;
    AnimationTimer animationTimer;

    // int size;//wielkość jednej komórki
    GraphicsContext gc;
    Random r;
    double k;
    int height, width;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        BC.setItems(FXCollections.observableArrayList(
                periodic, absorbic));
        BC.setValue(absorbic);
        type.setItems(FXCollections.observableArrayList(
                homogeneous, withRadius,random, clicking));
        type.setValue(random);
        squareSize.setItems(FXCollections.observableArrayList(1,2,4,10));
        squareSize.setValue(2);
        neighbourhood.setItems(FXCollections.observableArrayList(
                vonNeumann, Moore, PentagonalDown, PentagonalLeft, PentagonalRandom, PentagonalRight,
                PentagonalUp, HeksagonalLeft, HeksagonalRight, HeksagonalRandom));
        neighbourhood.setValue(Moore);
        w.setText("250");
        h.setText("200");
        amount.setText("12");

        height = Integer.parseInt(h.getText())*(int) squareSize.getValue();
        width = Integer.parseInt(w.getText())*(int) squareSize.getValue();


        gc = matrix.getGraphicsContext2D();
        r= new Random();
        Pane.setPrefWidth(gridPane.getPrefWidth());

        Pane.setPrefHeight(gridPane.getPrefHeight());

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {

                simulation((Neighbourhood) neighbourhood.getValue());

                try {
                    //slowing down for better animation
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(automat.isOver()) stop();
            }
        };



    }


    void build(Object t, Object b){



        if (homogeneous.equals(t) && Integer.parseInt(rowAmount.getText())>1 &&
                Integer.parseInt(rowAmount.getText()) < (height / (2*(int) squareSize.getValue())) &&  Integer.parseInt(columnAmount.getText()) >1 &&
                Integer.parseInt(columnAmount.getText()) < (width/ (2*(int) squareSize.getValue())) ) {
//            && height%Integer.parseInt(rowAmount.getText()) == 0 &&
//                    width%Integer.parseInt(columnAmount.getText()) == 0
            //sprawdzam by w w r i c nie było ciągiem komórek mają być przerwy
            //sprawdzam by dlugosc byla podzielna przez ilosc

            setBC(b);
            automat.buildHomogenous( Integer.parseInt(columnAmount.getText()),Integer.parseInt(rowAmount.getText()));
            draw(gc);
        } else if (withRadius.equals(t)) {

            automat.buildWithRadius(Integer.parseInt(amount.getText()),Integer.parseInt(radius.getText()));
            setBC(b);
            draw(gc);

        } else if (random.equals(t) && Integer.parseInt(amount.getText())>0 &&
                Integer.parseInt(amount.getText()) < ((width *height)/((int) squareSize.getValue() *100))) {

            //sprawdzam by zajęte było max 1/100 przestrzeni

            automat.buildRandom(Integer.parseInt(amount.getText()));
            setBC(b);
            draw(gc);
        } else if (clicking.equals(t)) {
            setBC(b);
            matrix.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                    int x = (int) event.getX()+1;
                    int y = (int) event.getY()+1;

                    automat.setCAState(x/(int) squareSize.getValue(),y/(int) squareSize.getValue());

                    draw(gc);
                }
            });

        }

    }

    public void setBC(Object b){
        if(b == periodic){
            automat.setPeriodic();
        }else if( b == absorbic){
            automat.setAbsorbic();
        }
    }

    public void simulation(Neighbourhood n){


                automat.simulation(n, (BoundaryCondition) BC.getValue());
                draw(gc);


    }
    public void begin(ActionEvent actionEvent) {

        if(limitSpace()) {

            height = Integer.parseInt(h.getText())*(int) squareSize.getValue();
            width = Integer.parseInt(w.getText())*(int) squareSize.getValue();
            matrix.setHeight(height);
            matrix.setWidth((width));
            automat = new CA((int)matrix.getWidth()/(int) squareSize.getValue(),(int) matrix.getHeight()/(int) squareSize.getValue());




            Pane.setPrefWidth(gridPane.getPrefWidth());
            Pane.setPrefHeight(gridPane.getPrefHeight());


            build(type.getValue(), BC.getValue());

        }
    }


    public void draw(GraphicsContext g){

        g.setFill(Color.WHITE);
        g.fillRect( (int)squareSize.getValue(), (int) squareSize.getValue(), matrix.getWidth(), matrix.getHeight());



        for (int y =1; y < ((matrix.getHeight()/(int) squareSize.getValue())); y++) {
            for (int x = 1; x <(( matrix.getWidth()/(int) squareSize.getValue())); x++) {
                if (this.automat.getState(y, x) != 0) {

                    g.setFill(automat.getColor(y, x));
                    g.fillRect(x*(int) squareSize.getValue(),y*(int) squareSize.getValue(), (int) squareSize.getValue(),(int) squareSize.getValue());


                }
            }
        }

//        //creating grid on board
//        // vertical lines
//        g.setStroke(Color.GRAY);
//        for(int i = 1 ; i <= matrix.getWidth() ; i+=(int) squareSize.getValue()){
//            g.strokeLine(i, 1, i, matrix.getHeight() );
//        }
//
//        // horizontal lines
//        g.setStroke(Color.GRAY);
//        for(int i = 1 ; i <= matrix.getHeight() ; i+=(int) squareSize.getValue()){
//            g.strokeLine(1, i, matrix.getWidth(), i);
//        }



    }


    public boolean limitSpace(){
        if(Integer.parseInt(w.getText()) >= 10 && Integer.parseInt(h.getText()) >= 10
        && Integer.parseInt(w.getText()) <=1000 && Integer.parseInt(h.getText()) <= 1000 ) return true;
        else return false;
    }


    public void StartGrowth(ActionEvent actionEvent) {

        animationTimer.stop();
        simulation((Neighbourhood) neighbourhood.getValue());

    }

    public void Growth(ActionEvent actionEvent) {

        animationTimer.start();
    }
}
