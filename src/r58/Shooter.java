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
        System.out.println("Looking for ship of length "+length+" to remove");
        
        for (int i = 0; i < length; i++) {
            if (p.y+i>=10 || shotMap[p.x][p.y+i]!=1) up=false;
            if (p.y-i<0 || shotMap[p.x][p.y-i]!=1) down=false;
            if (p.x-i<0 || shotMap[p.x-i][p.y]!=1) left=false;
            if (p.x+i>=10 || shotMap[p.x+i][p.y]!=1) right=false;
        }
        if (up){
            System.out.println("Removing ship up");
            for (int i = 0; i < length; i++) {
                shotMap[p.x][p.y+i]=-1;
                
            }
        }
        if (down){
            System.out.println("Removing ship down");
            for (int i = 0; i < length; i++) {
                shotMap[p.x][p.y-i]=-1;
                
            }
        }
        if (left){
            System.out.println("Removing ship left");
            for (int i = 0; i < length; i++) {
                shotMap[p.x-i][p.y]=-1;
                
            }
        }
        if (right){
            System.out.println("Removing ship right");
            for (int i = 0; i < length; i++) {
                shotMap[p.x+i][p.y]=-1;
            }
        }
        
        if (!up && !down && !left && !right) System.out.println("Didn't find ship to remove");
        
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
        
        Position shot;
        
        shot = gridPositions.get((int)(Math.random()*gridPositions.size()));
        gridPositions.remove(shot);
        
                shotsFired.add(shot);
        return shot;
    }
    public void clearHunt(){
        huntLocations.clear();
    }
//    public Position newHunt(Position p){
//        Position prospect = null;
//        //If we've hit somthing on one side shoot in that direction.
//        // Check bounds, if prospect has not been shot and that 
//        if (p.x-1>0 && shotMap[p.x-1][p.y]==0 && shotMap[p.x+1][p.y]==1) prospect = new Position(p.x-1,p.y);
//        if (p.x+1<10 && shotMap[p.x+1][p.y]==0 && shotMap[p.x-1][p.y]==1) prospect = new Position(p.x+1,p.y);
//        if (p.y+1<10 && shotMap[p.x][p.y+1]==0 && shotMap[p.x][p.y-1]==1) prospect = new Position(p.x,p.y+1);
//        if (p.y-1>0 && shotMap[p.x][p.y-1]==0 && shotMap[p.x][p.y+1]==1) prospect = new Position(p.x,p.y-1);
//        
//        return prospect;
//    }
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
        if (prospect==null)System.out.println("No more hunt succes, call method consulting map");
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
        
        System.out.println("Huntlocations "+huntLocations.size());
        
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
    
//    public boolean checkForCluster(){
//        for (Position p : hits) {
//            if ((hits.contains(new Position(p.x-1,p.y)) || hits.contains(new Position(p.x+1,p.y)))
//                    &&(hits.contains(new Position(p.x,p.y-1)) || hits.contains(new Position(p.x,p.y+1)))) cluster=true;
//        }
//        return cluster;
//    }
    
    public int  possibleShipCombos(Ship ship, boolean vertical, Position p){
        int[][] map = new int[10][10];
        for (int y = 9; y >= 0; y--) {              //Write from top -> 9
                for (int x = 0; x < 10; x++) {          //Write from x=0;
                    
                    
                        boolean fits=true;
                        for (int j = 0; j < ship.size(); j++) {
                            if (!vertical && (x+j>9 || shotMap[x+j][y]==-1)) fits=false;
                            if (vertical && (y+j>9 || y>0 || shotMap[x][y+j]==-1)) fits=false;
                        }
                        if (fits){
                            for (int j = 0; j < ship.size(); j++) { 
                            if (!vertical && shotMap[x+j][y]!=1) map[x+j][y]++;
                            if (vertical && shotMap[x][y+j]!=1) map[x][y+j]++;
                        }
                        
                        
                    }
                }
                
                
            }
        System.out.println("Ze map");
            for (int i = 9; i >= 0; i--) {              //Write from top -> 9
                for (int j = 0; j < 10; j++) {          //Write from x=0;
                    System.out.print(map[j][i]+" ");
                }
                System.out.println("");
                
            }
        return map[p.x][p.y];
    }
    public int [][] possibleShipCombosMap(Ship ship, boolean vertical, Position p){
        int[][] map = new int[10][10];
        for (int y = 9; y >= 0; y--) {              //Write from top -> 9
                for (int x = 0; x < 10; x++) {          //Write from x=0;
                    
                    
                        boolean fits=true;
                        for (int j = 0; j < ship.size(); j++) {
                            if (!vertical && (x+j>9 || shotMap[x+j][y]==-1)) fits=false;
                            if (vertical && (y+j>9 || y>0 || shotMap[x][y+j]==-1)) fits=false;
                        }
                        if (fits){
                            for (int j = 0; j < ship.size(); j++) { 
                            if (!vertical && shotMap[x+j][y]!=1) map[x+j][y]++;
                            if (vertical && shotMap[x][y+j]!=1) map[x][y+j]++;
                        }
                        
                        
                    }
                }
                
                
            }
        System.out.println("Ze map");
            for (int i = 9; i >= 0; i--) {              //Write from top -> 9
                for (int j = 0; j < 10; j++) {          //Write from x=0;
                    System.out.print(map[j][i]+" ");
                }
                System.out.println("");
                
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
