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

    public FleetMaker() {
        //Making a temporary map of where we place our ships
        shipMap = new int[12][12];
        clearShipMap();
    }

    public void clearShipMap() {
        for (int[] shipMap1 : shipMap) {
            Arrays.fill(shipMap1, 0);
        }
    }
    
    public ArrayList<Position> getSavedPos(){
        return savedPos;
    }

    public void placeOurShips(Fleet fleet, Board board) {

        sizeX = board.sizeX();
        sizeY = board.sizeY();
        boolean vert = true;
        boolean tryShip;
        boolean placeOneAppart = rnd.nextBoolean();
        for (int i = fleet.getNumberOfShips()-1; i >=0 ; i--) {
            Ship s = fleet.getShip(i);
            //System.out.println("Placing ship of size: " + s.size());
        
            tryShip = false;

            while (!tryShip) {
                nextX = rnd.nextInt(sizeX);
                nextY = rnd.nextInt(sizeY);
                vert = rnd.nextBoolean();
                if(placeOneAppart) tryShip = placePosRandom(nextX, nextY, s.size(), vert);
                else tryShip = placePosOneAppart(nextX, nextY, s.size(), vert);

            }
            Position pos = new Position(nextX, nextY);
            savedPos.add(pos);
            verticalSave.add(vert);
            board.placeShip(pos, s, vert);

        }

    }
    public void clearArrays(){
        savedPos.clear();
        verticalSave.clear();
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
    
    public void useSamePositionAgain(Fleet fleet, Board board){
        //System.out.println("UsedSamePosition");
        
            board.placeShip(savedPos.get(4), fleet.getShip(0), shipPlaced);
            board.placeShip(savedPos.get(3), fleet.getShip(1), shipPlaced);
            board.placeShip(savedPos.get(2), fleet.getShip(2), shipPlaced);
            board.placeShip(savedPos.get(1), fleet.getShip(3), shipPlaced);
            board.placeShip(savedPos.get(0), fleet.getShip(4), shipPlaced);
        
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
}
