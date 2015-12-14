/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r58;

import battleship.interfaces.Board;
import battleship.interfaces.Fleet;
import battleship.interfaces.Position;
import battleship.interfaces.Ship;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author dennisschmock
 */
public class FleetMaker {

    private int nextX;
    private int nextY;
    private int sizeX;
    private int sizeY;
    private int[][] shipMap;
    private Random rnd = new Random();
    private boolean shipPlaced;
    private ArrayList<Position> savedPos = new ArrayList<>();
    private ArrayList<Boolean> verticalSave = new ArrayList<>();
    public double[][] enemyShots = new double[10][10];
    public int numbersOfShipsPlaced = 0;
    public int numberOfTimesEveryShipIsPlaced = 0;
   
    //Tweaking "smart" ship placement
    private final double HEATUP = 3;
    private final double INITIALHEATUP = 35;
    private final double COOLDOWN = 0.5;
    private final int TOLERANCE = 20;
    public int numberOfShotsCounted = 25;

    public FleetMaker() {
        //Making a temporary map of where we place our ships
        shipMap = new int[10][10];
        clearShipMap();
    }

    public void clearShipMap() {
        for (int[] shipMap1 : shipMap) {
            Arrays.fill(shipMap1, 0);
        }
    }

    public void placeOurShips(Fleet fleet, Board board) {
        this.numbersOfShipsPlaced = 0;
        sizeX = board.sizeX();
        sizeY = board.sizeY();
        boolean vert = true;
        boolean tryShip;
        boolean placeOneAppart = rnd.nextBoolean();
        for (int i = fleet.getNumberOfShips() - 1; i >= 0; i--) {
            int tryCount = 0;
            Ship s = fleet.getShip(i);
            //System.out.println("Placing ship of size: " + s.size());

            tryShip = false;
            while (!tryShip && tryCount <= 500) {
                nextX = rnd.nextInt(sizeX);
                nextY = rnd.nextInt(sizeY);
                vert = rnd.nextBoolean();
                tryShip = placeShipAdapting(nextX, nextY, s.size(), vert);
                tryCount++;
            }
            while (!tryShip && tryCount > 500) {
                nextX = rnd.nextInt(sizeX);
                nextY = rnd.nextInt(sizeY);
                vert = rnd.nextBoolean();
                if (placeOneAppart) {
                    tryShip = placePosRandom(nextX, nextY, s.size(), vert);
                } else {
                    tryShip = placePosOneAppart(nextX, nextY, s.size(), vert);
                }
                tryCount++;
            }
            Position pos = new Position(nextX, nextY);
            savedPos.add(pos);
            verticalSave.add(vert);
            board.placeShip(pos, s, vert);
            this.numbersOfShipsPlaced++;

        }
        this.numberOfTimesEveryShipIsPlaced++;
//        System.out.println("PlaceOneAppart: " + placeOneAppart);
//        System.out.println("Numbers of ships placed: " + this.numbersOfShipsPlaced);
//        System.out.println(this.numberOfTimesEveryShipIsPlaced);

    }

    public void clearArrays() {
        savedPos.clear();
        verticalSave.clear();
    }

    public boolean placeShipAdapting(int x, int y, int size, boolean vert) {

        if (x >= 10 || y >= 10 || shipMap[x][y] == 1 || enemyShots[x][y] < TOLERANCE) {
            return false;
        }
        if (size <= 1 && shipMap[x][y] == 0 && enemyShots[x][y] < TOLERANCE) {
            shipMap[x][y] = +1;
            return true;
        }
        shipPlaced = false;
        shipMap[x][y] = shipMap[x][y] + 1;

        if (shipMap[x][y] == 1 && vert) {
            this.shipPlaced = placePosRandom(x, y + 1, size - 1, vert);
        }
        if (shipMap[x][y] == 1 && !vert) {
            this.shipPlaced = placePosRandom(x + 1, y, size - 1, vert);
        }
        if (!this.shipPlaced) {
            this.shipMap[x][y] = this.shipMap[x][y] - 1;
        }
        return this.shipPlaced;
    }

    public boolean placePosRandom(int x, int y, int size, boolean vert) {

        if (x >= 10 || y >= 10 || shipMap[x][y] == 1) {
            return false;
        }
        if (size <= 1 && shipMap[x][y] == 0) {
            shipMap[x][y] = +1;
            return true;
        }
        shipPlaced = false;
        shipMap[x][y] = shipMap[x][y] + 1;

        if (shipMap[x][y] == 1 && vert) {
            this.shipPlaced = placePosRandom(x, y + 1, size - 1, vert);
        }
        if (shipMap[x][y] == 1 && !vert) {
            this.shipPlaced = placePosRandom(x + 1, y, size - 1, vert);
        }
        if (!this.shipPlaced) {
            this.shipMap[x][y] = this.shipMap[x][y] - 1;
        }
        return this.shipPlaced;
    }

    public boolean placePosOneAppart(int x, int y, int size, boolean vert) {

        if (x >= 10 || y >= 10 || shipMap[x][y] >= 1) {
            return false;
        }

        if (size <= 1 && shipMap[x][y] == 0) {
            shipMap[x][y] = shipMap[x][y] + 2;
            if (vert) {
                shipMap[x + 1][y]++;
                if (x > 0) {
                    shipMap[x - 1][y]++;
                }
            }
            if (!vert) {
                shipMap[x][y + 1]++;
                if (y > 0) {
                    shipMap[x][y - 1]++;
                }
            }
            return true;
        }

        shipPlaced = false;
        shipMap[x][y] = shipMap[x][y] + 2;
        if (vert) {
            shipMap[x + 1][y]++;
            if (x > 0) {
                shipMap[x - 1][y]++;
            }
        }
        if (!vert) {
            shipMap[x][y + 1]++;
            if (y > 0) {
                shipMap[x][y - 1]++;
            }
        }

        if (shipMap[x][y] == 2 && vert) {
            this.shipPlaced = placePosOneAppart(x, y + 1, size - 1, vert);
        }

        if (shipMap[x][y] == 2 && !vert) {
            this.shipPlaced = placePosOneAppart(x + 1, y, size - 1, vert);
        }

        if (!this.shipPlaced) {
            this.shipMap[x][y] = this.shipMap[x][y] - 2;
            if (vert) {
                shipMap[x + 1][y]--;
                if (x > 0) {
                    shipMap[x - 1][y]--;
                }
            }
            if (!vert) {
                shipMap[x][y + 1]--;
                if (y > 0) {
                    shipMap[x][y - 1]--;
                }
            }
        }
        return this.shipPlaced;
    }

    public void useSamePositionAgain(Fleet fleet, Board board) {
        System.out.println("UsedSamePosition");

        board.placeShip(savedPos.get(4), fleet.getShip(0), verticalSave.get(4));
        board.placeShip(savedPos.get(3), fleet.getShip(1), verticalSave.get(3));
        board.placeShip(savedPos.get(2), fleet.getShip(2), verticalSave.get(2));
        board.placeShip(savedPos.get(1), fleet.getShip(3), verticalSave.get(1));
        board.placeShip(savedPos.get(0), fleet.getShip(4), verticalSave.get(0));

    }

    @Override
    public String toString() {
        String str = "";

        for (int i = shipMap.length - 1; i >= 0; i--) {
            for (int j = 0; j < shipMap.length; j++) {

                str = str + shipMap[j][i] + " ";
                ;

            }
            str = str + "\n";
        }

        return str;
    }

    public void heatUpHeatMap(Position pos) {
        if (enemyShots[pos.x][pos.y] < 100) {
            enemyShots[pos.x][pos.y] = enemyShots[pos.x][pos.y] + this.HEATUP;
        }
    }

    public void cooldownHeatMap() {

        for (int i = enemyShots.length - 1; i >= 0; i--) {
            for (int j = 0; j < enemyShots.length; j++) {
                if (enemyShots[j][i] >= COOLDOWN) {
                    enemyShots[j][i] = enemyShots[j][i] - COOLDOWN;
                }

            }

        }

    }
    
    public void initialHeat(){
        enemyShots[4][4] = this.INITIALHEATUP;
        enemyShots[4][5] = this.INITIALHEATUP;
        enemyShots[5][4] = this.INITIALHEATUP;
        enemyShots[5][5] = this.INITIALHEATUP;
        
    }

    public ArrayList<Position> getSavedPos() {
        return savedPos;
    }
    
    public void printOutHeatMap(int round) {
        //Do nothing
        System.out.println("Print out heatmap for round: " + round);
        for (int i = 9; i >= 0; i--) {              //Write from top -> 9
            for (int j = 0; j < 10; j++) {          //Write from x=0;
                if (enemyShots[j][i] < 10) {
                    System.out.print("00" + (int) enemyShots[j][i] + " ");

                } else if (enemyShots[j][i] < 100) {
                    System.out.print("0" + (int) enemyShots[j][i] + " ");
                } else {
                    System.out.print((int) enemyShots[j][i] + " ");
                }

            }
            System.out.println("");

        }
    }
}
