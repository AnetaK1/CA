package sample;

import javafx.scene.paint.Color;

import java.awt.geom.Point2D;
import java.util.*;

import static sample.Neighbourhood.*;


public class CA {



    private int width;
    private int height;
    private Cell[][] matrix;
    Random r = new Random();
    boolean choiceHeksa;
    int choicePenta ;


    public CA() {
        width = 10;
        height = 10;
        matrix = new Cell[height + 2][width +2 ];
        for(int i = 0; i < (width-1); i++){
            for(int j = 0; j < (height-1); j++){
                matrix[i][j] = new Cell();
                matrix[i][j].setPosition(i,j);
                matrix[i][j].setState(0);
                matrix[i][j].setCellColor(javafx.scene.paint.Color.WHITE);
            }
        }
        r = new Random();
    }

    public CA(int width, int height){
        this.width = (width + 2);
        this.height = (height + 2);
        r = new Random();

        matrix = new Cell[this.height][this.width];
        for(int i = 0; i < (this.height); i++){
            for(int j = 0; j < (this.width); j++){
                matrix[i][j] = new Cell();
                matrix[i][j].setPosition(i,j);
                matrix[i][j].setState(0);
                matrix[i][j].setCellColor(javafx.scene.paint.Color.WHITE);
            }
        }
    }



    void buildHomogenous(int ca, int ra){
        int h = this.height;
        int w = this.width;
        int r =  ((h-1) / (ra ));
        int c =  (((w-1) / (ca )));

        int k = 1;

        for(int i = r-2; i <=(r*ra); i+=(r)){
            for(int j = c-2; j <=(c*ca) ; j+=(c)){

                matrix[i][j].setState(k);
                k++;

                matrix[i][j].setCellColor(generateColor());
            }
        }

    }

    void buildWithRadius(int amount, int radius){



        int l =1;


        loops:
             for(int i = radius; i < this.height-1; i+=2*radius){

                for(int j = radius; j < this.width-1; j+=2*radius){



                            matrix[i][j].setState(l);
                            matrix[i][j].setCellColor(generateColor());
                            if(l==amount) break loops;
                            l++;
                    }
                }
    }






    void buildRandom(int amount){

        for(int i = 1; i < amount + 1; i++){

            int x = (r.nextInt(height-2)+1);
            int y = (r.nextInt(width-2)+1);

            matrix[x][y].setState(i);

            Color c = generateColor();


            matrix[x][y].setCellColor(c);

        }
    }

    //--------------------------------------------------//
    //warunki brzegowe periodyczne
    void setPeriodic(){

        for(int i = 0; i < (height); i++){
            for(int j = 0; j < (width); j++){

               if( i == 0 && j > 0 && j < (width-2)){
                   matrix[i][j] = matrix[height-2][j];
               }

               if(i == (height-1) && j > 0 && j < (width-2)){
                   matrix[i][j] = matrix[1][j];
               }

               if(j == 0 && i>0 && i <(height-2)){
                   matrix[i][j] = matrix[i][width -2];
               }
                if(j == (width-1) && i>0 && i <(height-2)){
                    matrix[i][j] = matrix[i][1];
                }


            }
        }

        matrix[0][0] = matrix[height - 2][width -2];
        matrix[0][width-1] = matrix[height -2][1];
        matrix[height-1][0] = matrix[1][width-2];
        matrix[height-1][width-1] = matrix[1][1];

    }

    //warunki brzgowe absorbujące
    void setAbsorbic(){
        for(int i = 0; i <=(height -1); i++){
            for(int j = 0; j <=(width-1); j++){

                if(i == 0) matrix[i][j].setState(0);
                if(i == (height -1)) matrix[i][j].setState(0);
                if(j == 0) matrix[i][j].setState(0);
                if(j == (width-1)) matrix[i][j].setState(0);



            }
        }
    }

    //-----------------------------------------------------------------//
    //szukanie najczęściej występującego id
    //jeśli któreś występują tyle samo razy wybierane jest id losowo spośród najczęściej występujących
    // zmienna n oznacza ile dla danego sąsiedztwa może wsytąpić tyle samo razy jedno id
    public Cell findMaxCell(List<Cell> neighbours){
        if(neighbours.isEmpty()) return new Cell();
        else{

            Cell max = new Cell();

            //zliczanie ile jest id takich samych
            Map<Integer, Integer> mapa = new HashMap<>();
            for(Cell cc :neighbours){
                if(mapa.containsKey(cc.getState())){
                    Integer value = mapa.get(cc.getState());
                    mapa.put(cc.getState(), value+ 1);
                }else{
                    mapa.put(cc.getState(), 1);
                }

            }

            //szukanie najczęściej występującego id
            Map.Entry<Integer, Integer> maxEntry = null;

            for(Map.Entry<Integer, Integer> entry:mapa.entrySet()){
                if(maxEntry == null || entry.getValue() >= maxEntry.getValue()){
                    if(maxEntry != null && entry.getValue() == maxEntry.getValue()){
                        Random random = new Random();
                        Boolean choice = random.nextBoolean();
                        if(choice){
                            maxEntry = entry;
                        }
                        else continue;

                    }else {
                        maxEntry = entry;
                    }



                }
            }

            // szukam po ID wybraną komórkę z listy sąsiadów i zwracam ją
            for(Cell g : neighbours ){
                if(g.getState() == maxEntry.getKey()){
                    max = g;
                }
            }

            return max;
        }

    }



    //-----------------------------------------------//
    //sąsiedztwo Moore'a
    public List<Cell> Moore(int x, int y){

        List<Cell> neighbours = new ArrayList<>();

        //tworzę listę komórek będących sąsiadami
        for(int i = x - 1; i <= x + 1; i++){

            if ( i < 0 || i > height - 1) continue;

            for(int j = y - 1; j <= y + 1; j++){

                if ( j < 0 || j > width - 1) continue;

                if(matrix[i][j].getState() != 0){
                    neighbours.add(new Cell(matrix[i][j].getState(), matrix[i][j].getX(),
                                     matrix[i][j].getY(), matrix[i][j].getCellColor()));
                }

            }
        }
        // szukam których komórek o tym samym id jest najwięcej
        // jeśli jest taka sama ilość wybieram w sposób losowy

        return neighbours;

    }


    //-------------------------------------//
    //sasiedztwo vonNeumanna
    public List<Cell> vonNeumann(int x, int y){
        List<Cell> neighbours = new ArrayList<>();
        if((x-1)>=0 && matrix[x-1][y].getState()!=0) neighbours.add(new Cell(matrix[x-1][y].getState(),x-1,y,matrix[x-1][y].getCellColor()));
        if((x+1)<height && matrix[x+1][y].getState()!=0) neighbours.add(new Cell(matrix[x+1][y].getState(),x+1,y,matrix[x+1][y].getCellColor()));
        if((y-1)>=0 && matrix[x][y-1].getState()!=0) neighbours.add(new Cell(matrix[x][y-1].getState(),x,y-1,matrix[x][y-1].getCellColor()));
        if((y+1)<width && matrix[x][y+1].getState()!=0) neighbours.add(new Cell(matrix[x][y+1].getState(),x,y+1,matrix[x][y+1].getCellColor()));


        return neighbours;
    }

    //----------------------------//
    //pentagonalne lewe
    public List<Cell> pentagonalLeft(int x, int y){


        List<Cell> neighbours = new ArrayList<>();

        for(int i = x - 1; i <= x + 1; i++){

            if ( i < 0 || i > height - 1 ) continue;

            for(int j = y; j <= y+1; j++){

                if ( j < 0 || j > width - 1 ) continue;

                if(matrix[i][j].getState() != 0){
                    neighbours.add(new Cell(matrix[i][j].getState(), matrix[i][j].getX(),
                            matrix[i][j].getY(), matrix[i][j].getCellColor()));
                }

            }
        }

        return neighbours;
    }

    //--------------------------------------------------------//
    //pentagonlne prawe
    public List<Cell> pentagonalRight(int x, int y){
        List<Cell> neighbours = new ArrayList<>();

        for(int i = x - 1; i <= x + 1; i++){

            if ( i < 0 || i > height - 1 ) continue;

            for(int j = y - 1; j <= y; j++){

                if ( j < 0 || j > width - 1 ) continue;

                if(matrix[i][j].getState() != 0){
                    neighbours.add(new Cell(matrix[i][j].getState(), matrix[i][j].getX(),
                            matrix[i][j].getY(), matrix[i][j].getCellColor()));
                }

            }
        }

        return neighbours;

    }


    //--------------------------------------------//
    //pentagonalne góra
    public List<Cell> pentagonalUp(int x, int y){
        List<Cell> neighbours = new ArrayList<>();

        for(int i = x; i <= x + 1; i++){

            if ( i < 0 || i > height - 1 ) continue;

            for(int j = y-1; j <= y+1; j++){

                if ( j < 0 || j > width - 1 ) continue;

                if(matrix[i][j].getState() != 0){
                    neighbours.add(new Cell(matrix[i][j].getState(), matrix[i][j].getX(),
                            matrix[i][j].getY(), matrix[i][j].getCellColor()));
                }

            }
        }

        return neighbours;
    }

    //------------------------------------------------------//
    //pentagonalne góra
    public List<Cell> pentagonalDown(int x, int y){
        List<Cell> neighbours = new ArrayList<>();

        for(int i = x-1; i <= x; i++){

            if ( i < 0 || i > height - 1 ) continue;

            for(int j = y-1; j <= y+1; j++){

                if ( j < 0 || j > width - 1 ) continue;

                if(matrix[i][j].getState() != 0){
                    neighbours.add(new Cell(matrix[i][j].getState(), matrix[i][j].getX(),
                            matrix[i][j].getY(), matrix[i][j].getCellColor()));
                }

            }
        }

        return neighbours;
    }

    //-----------------------------------------------------//
    //penatgonalne losowe
    public List<Cell> pentagonalRandom(int x, int y){
        List<Cell> neighbours = new ArrayList<>();

        //int choicePenta = r.nextInt(4);
      //  System.out.println(choicePenta);

        switch (choicePenta){
            case 0:
                neighbours = pentagonalDown(x,y);
               // System.out.println(PentagonalDown);
                break;
            case 1:
                neighbours = pentagonalLeft(x, y);
               // System.out.println(PentagonalLeft);
                break;
            case 2:
                neighbours = pentagonalRight(x,y);
              //  System.out.println(PentagonalRight);
                break;
            case 3:
                neighbours = pentagonalUp(x,y);
               // System.out.println(PentagonalUp);
                break;
            default:
                System.out.println("Wrong choice!!!");

        }

        return neighbours;
    }
    //--------------------------------------------------//
    //hekasgonalne lewe
    public List<Cell> heksagonalLeft(int x, int y){
        List<Cell> neighbours = new ArrayList<>();

        //tworzę listę komórek będących sąsiadami
        for(int i = x - 1; i <= x + 1; i++){

            if ( i < 0 || i > height - 1) continue;

            for(int j = y - 1; j <= y + 1; j++){

                if ( j < 0 || j > width - 1) continue;

                if((j == y-1 && i == x-1) || (j == y+1 && i == x+1)) continue;
                if(matrix[i][j].getState() != 0){
                    neighbours.add(new Cell(matrix[i][j].getState(), matrix[i][j].getX(),
                            matrix[i][j].getY(), matrix[i][j].getCellColor()));
                }

            }
        }


        return neighbours;
    }

    //-----------------------------------------------------//
    //heksagonalne prawe
    public List<Cell> hekasgonalRight(int x, int y){
        List<Cell> neighbours = new ArrayList<>();

        //tworzę listę komórek będących sąsiadami
        for(int i = x - 1; i <= x + 1; i++){

            if ( i < 0 || i > height - 1) continue;

            for(int j = y - 1; j <= y + 1; j++){

                if ( j < 0 || j > width - 1) continue;

                if((j == y+1 && i == x-1) || (j == y-1 && i == x+1)) continue;
                if(matrix[i][j].getState() != 0){
                    neighbours.add(new Cell(matrix[i][j].getState(), matrix[i][j].getX(),
                            matrix[i][j].getY(), matrix[i][j].getCellColor()));
                }

            }
        }


        return neighbours;
    }

    //------------------------------------------------------//
    //heksagonalne losowe
    public List<Cell> heksagonalRandom(int x, int y){
        List<Cell> neighbours = new ArrayList<>();

       // boolean choiceHeksa = r.nextBoolean();
        if(choiceHeksa){
            //System.out.println(HeksagonalRight);
            neighbours = hekasgonalRight(x,y);}
        else {
           // System.out.println(HeksagonalLeft);
            neighbours = heksagonalLeft(x, y);
        }

        return neighbours;
    }
    //------------------------------------------------------//
    //symulacja
    public void simulation(Neighbourhood neighbourhood, BoundaryCondition bc){

        choiceHeksa = r.nextBoolean();
        choicePenta = r.nextInt(4);
        Cell[][] tempBoard = new Cell[height][width];
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){


                List<Cell> n = new ArrayList<>();
                if(getState(i, j) == 0){

                    switch (neighbourhood){
                        case Moore:
                            n = Moore(i,j);
                            break;
                        case vonNeumann:
                            n = vonNeumann(i,j);
                            break;
                        case PentagonalLeft:
                            n = pentagonalLeft(i,j);
                            break;
                        case PentagonalRight:
                            n = pentagonalRight(i,j);
                            break;
                        case PentagonalDown:
                            n = pentagonalDown(i,j);
                            break;
                        case PentagonalUp:
                            n = pentagonalUp(i,j);
                            break;
                        case PentagonalRandom:
                            n = pentagonalRandom(i,j);
                            break;
                        case HeksagonalLeft:
                            n = heksagonalLeft(i,j);
                            break;
                        case HeksagonalRight:
                            n = hekasgonalRight(i,j);
                            break;
                        case HeksagonalRandom:
                            n = heksagonalRandom(i,j);
                            break;
                        default:
                            System.out.println("Nie ma takiego sąsiedztwa");
                    }

                    Cell f = findMaxCell(n);
                    tempBoard[i][j] = new Cell(f.getState(),i,j,f.getCellColor());


                }else{
                    tempBoard[i][j] = new Cell(matrix[i][j].getState(),i,j,matrix[i][j].getCellColor());

                }


            }
        }

        this.matrix = tempBoard;
        if(bc == BoundaryCondition.absorbic) setAbsorbic();
        if(bc == BoundaryCondition.periodic) setPeriodic();

    }

    public boolean isOver(){
        int c = 0;
        for(int i = 1; i <(height -2); i++){
            for(int j = 1; j <(width-2); j++){

                if(matrix[i][j].getState() == 0) c++;


            }
        }
        if(c == 0) return true;
        else return false;
    }

    public javafx.scene.paint.Color getColor(int x, int y){
       return this.matrix[x][y].getCellColor();
    }

    public javafx.scene.paint.Color generateColor(){

        int R = (int)(Math.random()*256);
        int G = (int)(Math.random()*256);
        int B= (int)(Math.random()*256);
        Color color = Color.rgb(R, G, B);
        return color;
    }
    public void setCAState(double x, double y){
        this.matrix[(int) y][(int) x].setState(1);
        matrix[(int)y][(int) x].setCellColor(generateColor());
    }

    public void setColor(int x, int y ,Color c){
        matrix[x][y].setCellColor(c);
    }
    public void setMatrix(Cell[][] m){
        this.matrix = m;
    }
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    int getState(int y, int x){
        return matrix[y][x].getState();
    }


}
