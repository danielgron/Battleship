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
public class NextShooter {

    private final static Random rnd = new Random();
    private boolean evenOdd = rnd.nextBoolean();
    private ArrayList<Position> shotsFired = new ArrayList();
    private OurPlayer op;
    boolean blank;
    ArrayList<Position> gridPositions = new ArrayList();
    ArrayList<Position> huntLocations = new ArrayList();
    ArrayList<Position> hits = new ArrayList();
    //private int [][] shotMap=new int[10][10];
    private int boardX;
    private int boardY;
    private ArrayList<Ship> enemyRemaining = new ArrayList();
    //private int[][] shipMap = new int [10][10];
    private boolean testing = false;
    private int[][][] maps;
    final int SHOT = 0;
    final int SHOTMAP = 1;
    final int SHIPCOMBOS = 2;
    final int ALLOURSHOTS = 3;
    final int ALLTHEIRSHOTS = 4;
    final int THEIRSHOTSUNTILHIT = 5;
    final int THEIRPLACEMENTS = 6;
    final int THEIRSHIPSFULLGAME = 7;
    final int OURBOARDTHISROUND = 8;
    private int turnCount;

    final double COOLDOWN = 0.8;
    final double HEATUP = 5;
    final int TOLERANCE = 7;
    final int MAXHEAT = 25;

    public void setBoardX(int boardX) {
        this.boardX = boardX;

    }

    public void startMaps() {
        System.out.println("Starting maps with x: " + boardX + " and y: " + boardY);
        maps = new int[boardX][boardY][9];
        System.out.println("Map started");
    }

    public void setEnemyRemaining(ArrayList<Ship> enemyRemaining) {
        this.enemyRemaining = enemyRemaining;
    }

    public ArrayList<Ship> getEnemyRemaining() {
        return enemyRemaining;
    }

    public void printOutMap(int map) {
        if (map==5)System.out.println("Their shots until hit:");
        if (map==6)System.out.println("Heatmap of their placements");
        if (map==3)System.out.println("Map of all our shots");


        //if (map==6)System.out.println("");
        System.out.println("Map number " + map);
        for (int i = boardY - 1; i >= 0; i--) {              //Write from top -> 9
            for (int j = 0; j < boardX; j++) {          //Write from x=0;
                if (maps[j][i][map] < 10) {
                    System.out.print("00" + maps[j][i][map] + " ");
                } else if (maps[j][i][map] < 100) {
                    System.out.print("0" + maps[j][i][map] + " ");
                } else {
                    System.out.print(maps[j][i][map] + " ");
                }
            }
            System.out.println("");

        }
    }

    public void newRound() {
        turnCount = 0;
    }

    public void makeOurBoard(ArrayList<Position> p, ArrayList<Boolean> v) {
        for (int y = 0; y < boardY; y++) {
            for (int x = 0; x < boardX; x++) {
                maps[x][y][OURBOARDTHISROUND] = 0;
            }
        }
        int[] size = {5, 4, 3, 3, 2};
        for (int i = 0; i < p.size(); i++) {
            //System.out.println("MakeOurBoard forLoop "+ p.size()+ " " +v.size()+ " "+size.length);
            addToOurBoard(p.get(i), v.get(i), size[i]);
        }
    }

    public void addToOurBoard(Position p, boolean v, int size) {
        if (v) {
            for (int i = 0; i < size; i++) {
                maps[p.x][p.y + i][OURBOARDTHISROUND] = size;
            }
        } else {
            for (int i = 0; i < size; i++) {
                maps[p.x + i][p.y][OURBOARDTHISROUND] = size;
            }
        }
        //printOutMap(8);

    }

    public boolean checkIfHit(Position p) {
        return (maps[p.x][p.y][OURBOARDTHISROUND] > 0);
    }

    public void enemyMissBeforeHit(Position p) {
        maps[p.x][p.y][THEIRSHOTSUNTILHIT]++;
    }

    public void setBoardY(int boardY) {
        this.boardY = boardY;
    }

    public Ship smallest() {
        Ship ship = enemyRemaining.get(0);
        for (Ship s : enemyRemaining) {
            if (s.size() < ship.size()) {
                ship = s;
            }
        }
        return ship;
    }

    public Ship largest() {
        Ship ship = enemyRemaining.get(0);
        for (Ship s : enemyRemaining) {
            if (s.size() > ship.size()) {
                ship = s;
            }
        }
        return ship;
    }

    public void resetShotMap() {
        for (int y = boardX - 1; y >= 0; y--) {
            for (int x = 0; x < boardX; x++) {
                maps[x][y][1] = 0;
            }
        }

    }

    public void addShot(Position p, boolean hit) {
        turnCount++;
        //if (turnCount==70) printOutMap(1);
        if (turnCount <= 4) {
            maps[p.x][p.y][0]++;
        }
        if (hit) {
            maps[p.x][p.y][1] = 1;
        } else {
            maps[p.x][p.y][1] = -1;

        }
    }

    public void shipWrecked(Position p, int length) {
        boolean up = true;
        boolean down = true;
        boolean left = true;
        boolean right = true;
        //System.out.println("Looking for ship of length "+length+" to remove");

        for (int i = 0; i < length; i++) {
            if (p.y + i >= boardY || maps[p.x][p.y + i][1] != 1) {
                up = false;
            }
            if (p.y - i < 0 || maps[p.x][p.y - i][1] != 1) {
                down = false;
            }
            if (p.x - i < 0 || maps[p.x - i][p.y][1] != 1) {
                left = false;
            }
            if (p.x + i >= boardX || maps[p.x + i][p.y][1] != 1) {
                right = false;
            }
        }
        if (up) {
            if (testing) {
                System.out.println("Removing ship up");
            }
            for (int i = 0; i < length; i++) {
                maps[p.x][p.y + i][1] = -1;
                maps[p.x][p.y + i][THEIRPLACEMENTS] += HEATUP;
                maps[p.x][p.y + i][THEIRSHIPSFULLGAME]++;
            }
        }
        if (down) {
            if (testing) {
                System.out.println("Removing ship down");
            }
            for (int i = 0; i < length; i++) {
                maps[p.x][p.y - i][1] = -1;
                maps[p.x][p.y - i][THEIRPLACEMENTS] += HEATUP;     //The cooldown value. Higher value - longer memory
                maps[p.x][p.y - i][THEIRSHIPSFULLGAME]++;
            }
        }
        if (left) {
            if (testing) {
                System.out.println("Removing ship left");
            }
            for (int i = 0; i < length; i++) {
                maps[p.x - i][p.y][1] = -1;
                maps[p.x - i][p.y][THEIRPLACEMENTS] += HEATUP;
                maps[p.x - i][p.y][THEIRSHIPSFULLGAME]++;
            }
        }
        if (right) {
            if (testing) {
                System.out.println("Removing ship right");
            }
            for (int i = 0; i < length; i++) {
                maps[p.x + i][p.y][1] = -1;
                maps[p.x + i][p.y][THEIRPLACEMENTS] += HEATUP;
                maps[p.x + i][p.y][THEIRSHIPSFULLGAME]++;
            }
        }

        if (testing && !up && !down && !left && !right) {
            System.out.println("Didn't find ship to remove");
        }

    }

    public void setOP(OurPlayer op) {
        this.op = op;
    }

    public void clearGridList() {
        gridPositions.clear();
    }

    public void clearShotsFired() {
        shotsFired.clear();
    }

    public void addHits(Position hit) {
        hits.add(hit);
    }

    public void clearHits() {
        hits.clear();
    }

    public void fillGridList() {

        evenOdd = rnd.nextBoolean();
        int x = 0;
        int y = 0;
        while (y < boardY) {
            if (evenOdd && y % 2 == 0) {
                x++;
            }
            if (!evenOdd && y % 2 == 1) {
                x++;
            }
            while (x < boardX) {
                gridPositions.add(new Position(x, y));
                x += 2;
            }
            y++;
            x = 0;
        }
        if (gridPositions.size() > 50) {
            System.out.println("Too many grid positions");
        }
    }

    public Position shootInGrid(int gridCase) {
        //if (checkIdentShips()) return shootSamePosition();
        if (gridCase == 0) {
            return shootInGridNormal();
        }
        
        if (gridCase == 1) {
            return shootInGridHeatBased();
        }
        //if (gridCase==2) return shootSamePosition();
        return shootInGridHeatBased();
    }
    public Position shootInGridNormal(){
        
        doComboMap();
        Position shot=gridPositions.get(0);
        //System.out.println("Got default shot");
        if (testing){
        printOutMap(SHIPCOMBOS);
        }
        //shot = gridPositions.get((int)(Math.random()*gridPositions.size()));
        for (Position p : gridPositions) {
            if (maps[shot.x][shot.y][SHIPCOMBOS]<maps[p.x][p.y][SHIPCOMBOS]) shot=p;
        }
        int highestValue = maps[shot.x][shot.y][SHIPCOMBOS];
        for (Position p : gridPositions) {
            if (maps[p.x][p.y][SHIPCOMBOS]==highestValue 
                    && maps[shot.x][shot.y][THEIRPLACEMENTS]<maps[p.x][p.y][THEIRPLACEMENTS]) shot=p;
        }
        for (Position p : gridPositions) {
            if (maps[p.x][p.y][THEIRPLACEMENTS]>35) {
                shot=p;
                //System.out.println("Shot changed "+shot.x + " "+shot.y);
                    
            }
            //System.out.println("shot determined by previous placement");
            
            
        }
        
        gridPositions.remove(shot);
        
                shotsFired.add(shot);
        return shot;
    }

    public Position shootInGridHeatBased() {

        doComboMap();
        Position shot = gridPositions.get((int)(Math.random()*gridPositions.size()));
        for (Position p : gridPositions) {
            if (maps[p.x][p.y][THEIRPLACEMENTS]>maps[shot.x][shot.y][THEIRPLACEMENTS]) shot=p;
        }
        //System.out.println("Got default shot");
        if (testing) {
            printOutMap(SHIPCOMBOS);
        }
        //shot = gridPositions.get((int)(Math.random()*gridPositions.size()));
        for (Position p : gridPositions) {
            if (maps[shot.x][shot.y][SHIPCOMBOS] < maps[p.x][p.y][SHIPCOMBOS]) {
                //shot = p;
            }
        }
        int highestValue = maps[shot.x][shot.y][SHIPCOMBOS];
        for (Position p : gridPositions) {
            if (maps[p.x][p.y][SHIPCOMBOS] == highestValue 
                    && maps[shot.x][shot.y][THEIRPLACEMENTS] < maps[p.x][p.y][THEIRPLACEMENTS]) {
                shot = p;
            }
        }
        for (Position p : gridPositions) {
            if (maps[p.x][p.y][THEIRPLACEMENTS] > TOLERANCE) {
                shot = p;
                //System.out.println("Shot changed "+shot.x + " "+shot.y);

            }
            //System.out.println("shot determined by previous placement");

        }

        gridPositions.remove(shot);

        shotsFired.add(shot);
        return shot;
    }

    public Position shootInGridLostFour() {
        //System.out.println("Shoot in grid lost four");

        doComboMap();
        Position shot = gridPositions.get(0);
        //System.out.println("Got default shot");
        if (testing) {
            printOutMap(SHIPCOMBOS);
        }
        //shot = gridPositions.get((int)(Math.random()*gridPositions.size()));
        for (Position p : gridPositions) {
            if (maps[shot.x][shot.y][SHIPCOMBOS] + Math.min((maps[shot.x][shot.y][6] / 5), 5)
                    < maps[p.x][p.y][SHIPCOMBOS] + Math.min((maps[p.x][p.y][6] / 5), 5)) {
                shot = p;
            }
        }

        gridPositions.remove(shot);

        shotsFired.add(shot);
        return shot;
    }

    public boolean checkIdentShips() {
        for (int y = boardY - 1; y >= 0; y--) {
            for (int x = 0; x < boardX; x++) {
                if (maps[x][y][7] >= 3) {
                    return true;
                }
            }
        }
        return false;
    }

    public void clearHunt() {
        huntLocations.clear();
    }

    public Position huntOtherWay() {
        Position prospect = null;

        for (int y = boardY - 1; y >= 0; y--) {
            for (int x = 0; x < boardX; x++) {
                if (maps[x][y][1] == 1 && x + 1 < boardX && x - 1 >= 0 && (maps[x + 1][y][1] == 1) && (maps[x - 1][y][1] == 0)) {
                    prospect = (new Position(x - 1, y));
                    break;  // Probably should be subbed with method to evaluate candidates
                }
                if (maps[x][y][1] == 1 && x + 1 < boardX && x - 1 >= 0 && (maps[x - 1][y][1] == 1) && (maps[x + 1][y][1] == 0)) {
                    prospect = (new Position(x + 1, y));
                    break;  // Probably should be subbed with method to evaluate candidates
                }
                if (maps[x][y][1] == 1 && y + 1 < boardY && y - 1 >= 0 && (maps[x][y + 1][1] == 1) && (maps[x][y - 1][1] == 0)) {
                    prospect = (new Position(x, y - 1));
                    break;  // Probably should be subbed with method to evaluate candidates
                }
                if (maps[x][y][1] == 1 && y + 1 < boardY && y - 1 >= 0 && (maps[x][y - 1][1] == 1) && (maps[x][y + 1][1] == 0)) {
                    prospect = (new Position(x, y + 1));
                    break;  // Probably should be subbed with method to evaluate candidates
                }
            }
        }
        if (testing && prospect == null) {
            System.out.println("Didn't find two connected hits, calling method consulting map");
        }
        if (gridPositions.contains(prospect)) {
            gridPositions.remove(prospect);
        }
        return prospect;
    }

    public Position hunt(Position hit) {
        huntLocations.clear();
        for (int y = boardY - 1; y >= 0; y--) {
            for (int x = 0; x < boardX; x++) {
                if (maps[x][y][1] == 1) {       //If there is a ship that hasn't been sunk yet...
                    if (x - 1 >= 0 && (maps[x - 1][y][1] == 0)) {
                        huntLocations.add(new Position(x - 1, y));
                        //break;  // Probably should be subbed with method to evaluate candidates
                    }
                    if (x + 1 < boardX && (maps[x + 1][y][1] == 0)) {
                        huntLocations.add(new Position(x + 1, y));
                        //break;  // Probably should be subbed with method to evaluate candidates
                    }
                    if (y - 1 >= 0 && (maps[x][y - 1][1] == 0)) {
                        huntLocations.add(new Position(x, y - 1));
                        //break;  // Probably should be subbed with method to evaluate candidates
                    }
                    if (y + 1 < boardY && (maps[x][y + 1][1] == 0)) {
                        huntLocations.add(new Position(x, y + 1));
                        //break;  // Probably should be subbed with method to evaluate candidates
                    }
                }
            }
        }

        if (testing) {
            System.out.println("Huntlocations " + huntLocations.size());
        }

        Position shot = null;
        boolean bingo = false;
        for (Position p : huntLocations) {
            if (maps[p.x][p.y][6] > 90) {
                shot = p;
                bingo = true;
            }
        }
        if (!bingo) {
            shot = huntLocations.remove((int) (Math.random() * huntLocations.size()));
        }
        shotsFired.add(shot);
        return shot;
    }

    public boolean checkForMoreTargets() {
        for (int y = boardY - 1; y >= 0; y--) {
            for (int x = 0; x < boardX; x++) {
                if (maps[x][y][1] == 1) {
                    return true;       // Returns true if the map has a
                }
            }                                           // ship that's discovered but not sunk
        }
        return false;
    }
    //Make a map of posible combinations of ships in each field

    public void doComboMap() {
        if (testing) {
            System.out.println("Make 4 temp maps");
        }
        int[][] map1 = new int[boardX][boardY];
        int[][] map2 = new int[boardX][boardY];
        int[][] map3 = new int[boardX][boardY];
        int[][] map4 = new int[boardX][boardY];
        if (testing) {
            System.out.println("fill temp maps with combos");
        }
        //Write from x=0;

        map1 = possibleShipCombosMap(largest(), true);
        map2 = possibleShipCombosMap(largest(), false);
        map3 = possibleShipCombosMap(smallest(), true);
        map4 = possibleShipCombosMap(smallest(), false);

        if (testing) {
            System.out.println("Sum the temp maps");
        }

        for (int y = boardY - 1; y >= 0; y--) {              //Write from top -> 9
            for (int x = 0; x < boardX; x++) {          //Write from x=0;
                maps[x][y][SHIPCOMBOS] = map1[x][y];
                maps[x][y][SHIPCOMBOS] += map2[x][y];
                maps[x][y][SHIPCOMBOS] += map3[x][y];
                maps[x][y][SHIPCOMBOS] += map4[x][y];
                //printOutMap(SHIPCOMBOS);
                //System.out.println("Hello");
                //if(testing)System.out.println(map1[x][y]+" "+map2[x][y]+" "+map3[x][y]+" "+map4[x][y]);
            }
        }

    }

    public void updateComboMap(Position p) {
        for (int x = 0; x < boardX; x++) {

        }
        for (int i = 1; i < 5; i++) {

        }

    }

    // Check the amount of posible combinations in a specific spot
    public int possibleShipCombos(Ship ship, boolean vertical, Position p) {
        int[][] map = new int[boardX][boardY];
        //Write from x=0;
        boolean fits = true;
        for (int j = 0; j < ship.size(); j++) {
            if (!vertical && (p.x + j > boardX - 1 || p.x + j < 0 || maps[p.x + j][p.y][1] == -1)) {
                fits = false;
            }
            if (vertical && (p.y + j > 9 || p.y + j < 0 || maps[p.x][p.y + j][1] == -1)) {
                fits = false;
            }
        }
        if (fits) {
            for (int j = 0; j < ship.size(); j++) {
                if (!vertical && maps[p.x + j][p.y][1] != 1) {
                    map[p.x + j][p.y]++;
                }
                if (vertical && maps[p.x][p.y + j][1] != 1) {
                    map[p.x][p.y + j]++;
                }
            }
        }

        return map[p.x][p.y];
    }

    public int[][] possibleShipCombosMap(Ship ship, boolean vertical) {
        int[][] map = new int[boardX][boardY];
        for (int y = boardY - 1; y >= 0; y--) {              //Write from top -> 9
            for (int x = 0; x < boardX; x++) {          //Write from x=0;

                boolean fits = true;
                for (int j = 0; j < ship.size(); j++) {
                    if (!vertical && (x + j > boardX - 1 || maps[x + j][y][1] == -1)) {
                        fits = false;
                    }
                    if (vertical && (y + j > boardY - 1 || maps[x][y + j][1] == -1)) {
                        fits = false;
                    }
                }
                if (fits) {
                    for (int j = 0; j < ship.size(); j++) {
                        if (!vertical && maps[x + j][y][1] != 1) {
                            map[x + j][y]++;
                        }
                        if (vertical && maps[x][y + j][1] != 1) {
                            map[x][y + j]++;
                        }
                    }
                }
            }
        }
        return map;
    }

    public void coolDownPlacement() {
        for (int y = boardY - 1; y >= 0; y--) {
            for (int x = 0; x < boardX; x++) {
                if (maps[x][y][THEIRPLACEMENTS] >= 0) {
                    maps[x][y][THEIRPLACEMENTS] =(int)(maps[x][y][THEIRPLACEMENTS] *COOLDOWN);
                }
                if (maps[x][y][THEIRPLACEMENTS] >= MAXHEAT) {
                    maps[x][y][THEIRPLACEMENTS] = MAXHEAT;
                }
                if (maps[x][y][THEIRPLACEMENTS] < 0) {
                    maps[x][y][THEIRPLACEMENTS] = 0;
                }
            }
        }
    }

    void addEnemyMiss(Position pos) {
        maps[pos.x][pos.y][THEIRSHOTSUNTILHIT]++;
    }

}
