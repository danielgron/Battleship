/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r58;

import battleship.interfaces.Position;
import battleship.interfaces.Ship;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Daniel
 */
public class Shooter {
    private final static Random rnd = new Random();
    private boolean evenOdd= rnd.nextBoolean();
    private ArrayList<Position> shotsFired= new ArrayList();
    private OurPlayer op;
    boolean blank;
    ArrayList<Position> gridPositions = new ArrayList();
    ArrayList<Position> huntLocations = new ArrayList();
    ArrayList<Position> hits = new ArrayList();
    private boolean cluster;
    private int [][] shotMap=new int[10][10];
    private int boardX;
    private int boardY;
    private ArrayList<Ship> enemyRemaining= new ArrayList();
    private int[][] shipMap = new int [10][10];
    private boolean testing= false;
    
    
    public void setBoardX(int boardX) {
        this.boardX = boardX;
        
    }
    
    public void setEnemyRemaining(ArrayList<Ship> enemyRemaining){
        this.enemyRemaining=enemyRemaining;
    }
    public ArrayList<Ship> getEnemyRemaining(){
        return enemyRemaining;
    }

    public void setBoardY(int boardY) {
        this.boardY = boardY;
    }
    public Ship smallest(){
        Ship ship=enemyRemaining.get(0);
        for (Ship s : enemyRemaining) {
            if (s.size()<ship.size()) ship = s;
        }
        return ship;
    }
    public Ship largest(){
        Ship ship=enemyRemaining.get(0);
        for (Ship s : enemyRemaining) {
            if (s.size()>ship.size()) ship = s;
        }
        return ship;
    }
    
    
    
    
    public int[][] getSunkMap(){
        return shotMap;
    }
    public void resetShotMap(){
        shotMap=new int[10][10];
        
        
    }
    public void addShot(Position p,boolean hit){
        if (hit) shotMap[p.x][p.y]=1;
        else shotMap[p.x][p.y]=-1;
    }
    public void shipWrecked(Position p, int length){
        boolean up=true;
        boolean down=true;
        boolean left=true;
        boolean right=true;
        if(testing)System.out.println("Looking for ship of length "+length+" to remove");
        
        for (int i = 0; i < length; i++) {
            if (p.y+i>=10 || shotMap[p.x][p.y+i]!=1) up=false;
            if (p.y-i<0 || shotMap[p.x][p.y-i]!=1) down=false;
            if (p.x-i<0 || shotMap[p.x-i][p.y]!=1) left=false;
            if (p.x+i>=10 || shotMap[p.x+i][p.y]!=1) right=false;
        }
        if (up){
            if(testing)System.out.println("Removing ship up");
            for (int i = 0; i < length; i++) {
                shotMap[p.x][p.y+i]=-1;
                
            }
        }
        if (down){
            if(testing)System.out.println("Removing ship down");
            for (int i = 0; i < length; i++) {
                shotMap[p.x][p.y-i]=-1;
                
            }
        }
        if (left){
            if(testing)System.out.println("Removing ship left");
            for (int i = 0; i < length; i++) {
                shotMap[p.x-i][p.y]=-1;
                
            }
        }
        if (right){
            if(testing)System.out.println("Removing ship right");
            for (int i = 0; i < length; i++) {
                shotMap[p.x+i][p.y]=-1;
            }
        }
        
        if (testing && !up && !down && !left && !right) System.out.println("Didn't find ship to remove");
        
    }
        
    
    public void setOP(OurPlayer op){
        this.op=op;
    }
    public void clearGridList(){
        gridPositions.clear();
    }
    public void clearShotsFired(){
        shotsFired.clear();
    }
    public void addHits(Position hit){
        hits.add(hit);
    }
    
    public void clearHits(){
        hits.clear();
    }
    public void fillGridList(){
        evenOdd= rnd.nextBoolean();    
        int x=0;
        int y=0;
        while (y<10){
            if (evenOdd && y%2==0) x++;
            if (!evenOdd && y%2==1) x++;
            while (x<10){
                gridPositions.add(new Position (x,y));
                x+=2;
            }
            y++;
            x=0;            
        }   
}
    
    
    public Position shootInGrid(){
        doComboMap();
        
        Position shot=gridPositions.get(0);
        
        if (testing){
        System.out.println("Ze combo map");
            for (int i = 9; i >= 0; i--) {              //Write from top -> 9
                for (int j = 0; j < 10; j++) {          //Write from x=0;
                    System.out.print(shipMap[j][i]+" ");
                }
                System.out.println("");
                
            }
        }
        //shot = gridPositions.get((int)(Math.random()*gridPositions.size()));
        for (Position p : gridPositions) {
            if (shipMap[shot.x][shot.y]<shipMap[p.x][p.y]) shot=p;
        }
        gridPositions.remove(shot);
        
                shotsFired.add(shot);
        return shot;
    }
    public void clearHunt(){
        huntLocations.clear();
    }

    public Position huntOtherWay(){
        Position prospect=null;
        
        for (int y = 9; y >=0; y--) {
            for (int x = 0; x < 10; x++) {
                if (shotMap[x][y]==1 && x+1<10 && x-1>=0 &&(shotMap[x+1][y]==1)&& (shotMap[x-1][y]==0)){
                    prospect=(new Position(x-1,y));
                    break;  // Probably should be subbed with method to evaluate candidates
                }
                if (shotMap[x][y]==1 && x+1<10 && x-1>=0 && (shotMap[x-1][y]==1)&& (shotMap[x+1][y]==0)){
                    prospect=(new Position(x+1,y));
                    break;  // Probably should be subbed with method to evaluate candidates
                }
                if (shotMap[x][y]==1 && y+1<10 && y-1>=0 &&(shotMap[x][y+1]==1)&& (shotMap[x][y-1]==0)){
                    prospect=(new Position(x,y-1));
                    break;  // Probably should be subbed with method to evaluate candidates
                }
                if (shotMap[x][y]==1 && y+1<10 && y-1>=0 &&(shotMap[x][y-1]==1)&& (shotMap[x][y+1]==0)){
                    prospect=(new Position(x,y+1));
                    break;  // Probably should be subbed with method to evaluate candidates
                }
            }
        }
        if (testing && prospect==null)System.out.println("Didn't find two connected hits, calling method consulting map");
        return prospect;
    }
    
    
    public Position hunt(Position hit){
        huntLocations.clear();
        for (int y = 9; y >=0; y--) {
            for (int x = 0; x < 10; x++) {
                if (shotMap[x][y]==1){       //If there is a ship that hasn't been sunk yet...
                if ( x-1>=0 && (shotMap[x-1][y]==0)){
                    huntLocations.add(new Position(x-1,y));
                    //break;  // Probably should be subbed with method to evaluate candidates
                }
                if (x+1<10 &&  (shotMap[x+1][y]==0)){
                    huntLocations.add(new Position(x+1,y));
                    //break;  // Probably should be subbed with method to evaluate candidates
                }
                if (y-1>=0 && (shotMap[x][y-1]==0)){
                    huntLocations.add(new Position(x,y-1));
                    //break;  // Probably should be subbed with method to evaluate candidates
                }
                if (y+1<10 && (shotMap[x][y+1]==0)){
                    huntLocations.add(new Position(x,y+1));
                    //break;  // Probably should be subbed with method to evaluate candidates
                }
                }
            }
        }
        
        if(testing)System.out.println("Huntlocations "+huntLocations.size());
        
        Position shot = huntLocations.remove((int)(Math.random()*huntLocations.size()));
        shotsFired.add(shot);
        return shot;
    }
    
    public boolean checkForMoreTargets(){
        for (int y = 9; y >= 0; y--) {             
                for (int x = 0; x < 10; x++) {          
                    if(shotMap[x][y]==1) return true;       // Returns true if the map has a
                }                                           // ship that's discovered but not sunk
        }
        return false;
    }
    //Make a map of posible combinations of ships in each field
public void doComboMap(){
    int[][] map1 = new int[10][10];
    int[][] map2 = new int[10][10];
    int[][] map3 = new int[10][10];
    int[][] map4 = new int[10][10];
                 //Write from x=0;
                    
                    map1=possibleShipCombosMap(largest(),true);
                    map2=possibleShipCombosMap(largest(),false);
                    map3=possibleShipCombosMap(smallest(),true);
                    map4=possibleShipCombosMap(smallest(),false);
                
        shipMap= new int[10][10];
        for (int y = 9; y >= 0; y--) {              //Write from top -> 9
                for (int x = 0; x < 10; x++) {          //Write from x=0;
                shipMap[x][y]+=map1[x][y];
                shipMap[x][y]+=map2[x][y];
                shipMap[x][y]+=map3[x][y];
                shipMap[x][y]+=map4[x][y];
                    //System.out.println("Hello");
                    if(testing)System.out.print(map1[x][y]+" "+map2[x][y]+" "+map3[x][y]+" "+map4[x][y]);
                }
        }
        
}

public void updateComboMap(Position p){
    for (int i = 1; i < 5; i++) {
        shipMap[p.x][p.y]+=possibleShipCombos(largest(),true,new Position(p.x,p.y));
        shipMap[p.x][p.y]+=possibleShipCombos(largest(),false,new Position(p.x,p.y));
        shipMap[p.x][p.y]+=possibleShipCombos(smallest(),true,new Position(p.x,p.y));
        shipMap[p.x][p.y]+=possibleShipCombos(smallest(),false,new Position(p.x,p.y));
        
        shipMap[p.x+i][p.y]+=possibleShipCombos(largest(),true,new Position(p.x+i,p.y));
        shipMap[p.x+i][p.y]+=possibleShipCombos(largest(),false,new Position(p.x+i,p.y));
        shipMap[p.x+i][p.y]+=possibleShipCombos(smallest(),true,new Position(p.x+i,p.y));
        shipMap[p.x+i][p.y]+=possibleShipCombos(smallest(),false,new Position(p.x+i,p.y));
        
        shipMap[p.x-i][p.y]+=possibleShipCombos(largest(),true,new Position(p.x-i,p.y));
        shipMap[p.x-i][p.y]+=possibleShipCombos(largest(),false,new Position(p.x-i,p.y));
        shipMap[p.x-i][p.y]+=possibleShipCombos(smallest(),true,new Position(p.x-i,p.y));
        shipMap[p.x-i][p.y]+=possibleShipCombos(smallest(),false,new Position(p.x-i,p.y));
        
        shipMap[p.x][p.y+i]+=possibleShipCombos(largest(),true,new Position(p.x,p.y+i));
        shipMap[p.x][p.y+i]+=possibleShipCombos(largest(),false,new Position(p.x,p.y+i));
        shipMap[p.x][p.y+i]+=possibleShipCombos(smallest(),true,new Position(p.x,p.y+i));
        shipMap[p.x][p.y+i]+=possibleShipCombos(smallest(),false,new Position(p.x,p.y+i));
        
        shipMap[p.x][p.y-i]+=possibleShipCombos(largest(),true,new Position(p.x,p.y-i));
        shipMap[p.x][p.y-i]+=possibleShipCombos(largest(),false,new Position(p.x,p.y-i));
        shipMap[p.x][p.y-i]+=possibleShipCombos(smallest(),true,new Position(p.x,p.y-i));
        shipMap[p.x][p.y-i]+=possibleShipCombos(smallest(),false,new Position(p.x,p.y-i));
                
    }
    
}
    // Check the amount of posible combinations in a specific spot
    public int  possibleShipCombos(Ship ship, boolean vertical, Position p){
        int[][] map = new int[10][10];
                //Write from x=0;
                        boolean fits=true;
                        for (int j = 0; j < ship.size(); j++) {
                            if (!vertical && (p.x+j>9 || p.x+j<0 || shotMap[p.x+j][p.y]==-1)) fits=false;
                            if (vertical && (p.y+j>9  || p.y+j<0|| shotMap[p.x][p.y+j]==-1)) fits=false;
                        }
                        if (fits){
                            for (int j = 0; j < ship.size(); j++) { 
                            if (!vertical && shotMap[p.x+j][p.y]!=1) map[p.x+j][p.y]++;
                            if (vertical && shotMap[p.x][p.y+j]!=1) map[p.x][p.y+j]++;
                        }
                    }
                
        return map[p.x][p.y];
    }
    public int [][] possibleShipCombosMap(Ship ship, boolean vertical){
        int[][] map = new int[10][10];
        for (int y = 9; y >= 0; y--) {              //Write from top -> 9
                for (int x = 0; x < 10; x++) {          //Write from x=0;
                    
                        boolean fits=true;
                        for (int j = 0; j < ship.size(); j++) {
                            if (!vertical && (x+j>9 || shotMap[x+j][y]==-1)) fits=false;
                            if (vertical && (y+j>9 || shotMap[x][y+j]==-1)) fits=false;
                        }
                        if (fits){
                            for (int j = 0; j < ship.size(); j++) { 
                            if (!vertical && shotMap[x+j][y]!=1) map[x+j][y]++;
                            if (vertical && shotMap[x][y+j]!=1) map[x][y+j]++;
                        }
                    }
                }
            }
        return map;
    }
    

//class Target {
//    
//    private Position p;
//    private int horizontalHuntValue;
//    private int verticalHuntValue;
//    private int combinedHuntValue;
//    
//    Target(Position p,Shooter s){
//        this.p=p;
//        ArrayList<Ship> enemyRemaining = getEnemyRemaining();
//        for (Ship ship : enemyRemaining) {
//            horizontalHuntValue+=possibleShipCombos(ship, true, p);
//            
//        }
//        horizontalHuntValue=possibleShipCombos(enemyRemaining, true, p);
//        
//    }
//    
//}
}
