/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package r58;

import battleship.interfaces.BattleshipsPlayer;
import battleship.interfaces.Fleet;
import battleship.interfaces.Position;
import battleship.interfaces.Board;
import battleship.interfaces.Ship;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Tobias
 */
public class OurPlayer implements BattleshipsPlayer {

    private final static Random rnd = new Random();
    private int sizeX;
    private int sizeY;
    private Board myBoard;
    private boolean hunting = false;
    private Fleet previousFleet;
    private Shooter s = new Shooter();
    private Position hit = new Position(0, 0);
    private Position lastShot = new Position(0, 0);
    private final int[][] enemyShots = new int[10][10];
    private final int[][] ourShots = new int[10][10];

    private ArrayList<Ship> shipsLeftBeforeShot = new ArrayList();
    private ArrayList<Ship> shipsLeftAfterShot = new ArrayList();
    private Ship lastSunk;
    private boolean samePlacementAgain = false;

    private int round = 0;

    private int numOfEnemyShips;

    private boolean testing = false;

    private FleetMaker fleetMaker;

    public OurPlayer() {
        fleetMaker = new FleetMaker();

    }

    /**
     * The method called when its time for the AI to place ships on the board
     * (at the beginning of each round).
     *
     * The Ship object to be placed MUST be taken from the Fleet given (do not
     * create your own Ship objects!).
     *
     * A ship is placed by calling the board.placeShip(..., Ship ship, ...) for
     * each ship in the fleet (see board interface for details on placeShip()).
     *
     * A player is not required to place all the ships. Ships placed outside the
     * board or on top of each other are wrecked.
     *
     * @param fleet Fleet all the ships that a player should place.
     * @param board Board the board were the ships must be placed.
     */
    @Override
    public void placeShips(Fleet fleet, Board board) {
          if (samePlacementAgain) {
            fleetMaker.useSamePositionAgain(fleet, board);
        } else {
            fleetMaker.clearArrays();
            fleetMaker.placeOurShips(fleet, board);
        }

    }

    /**
     * Called every time the enemy has fired a shot.
     *
     * The purpose of this method is to allow the AI to react to the enemy's
     * incoming fire and place his/her ships differently next round.
     *
     * @param pos Position of the enemy's shot
     */
    @Override
    public void incoming(Position pos) {
        enemyShots[pos.x][pos.y]++;
        //Do nothing
    }

    /**
     * Called by the Game application to get the Position of your shot.
     * hitFeedBack(...) is called right after this method.
     *
     * @param enemyShips Fleet the enemy's ships. Compare this to the Fleet
     * supplied in the hitFeedBack(...) method to see if you have sunk any
     * ships.
     *
     * @return Position of you next shot.
     */
    @Override
    public Position getFireCoordinates(Fleet enemyShips) {

        numOfEnemyShips = enemyShips.getNumberOfShips();

        shipsLeftBeforeShot.clear();
        for (Ship enemyShip : enemyShips) {
            shipsLeftBeforeShot.add(enemyShip);
        }
        s.setEnemyRemaining(shipsLeftBeforeShot);
        s.setOP(this);

        Position shot = null;
        if (!hunting) {
            shot = s.shootInGrid();
        } else if (hunting) {
            shot = s.huntOtherWay();
            if (shot == null) {
                shot = s.hunt(hit);
            }
        }

        lastShot = shot;

        if (testing) {
            System.out.println("Shooting at " + shot.x + " " + shot.y);
        }
        ourShots[shot.x][shot.y]++;
        return shot;
    }

    /**
     * Called right after getFireCoordinates(...) to let your AI know if you hit
     * something or not.
     *
     * Compare the number of ships in the enemyShips with that given in
     * getFireCoordinates in order to see if you sunk a ship.
     *
     * @param hit boolean is true if your last shot hit a ship. False otherwise.
     * @param enemyShips Fleet the enemy's ships.
     */
    @Override
    public void hitFeedBack(boolean hit, Fleet enemyShips) {
        shipsLeftAfterShot.clear();
        s.addShot(lastShot, hit);

        for (Ship enemyShip : enemyShips) {
            shipsLeftAfterShot.add(enemyShip);
        }

//        if (hunting && s.huntLocations.size() == 0) {
//            System.out.println("No hunt locations, break out of hunting mode");
//            hunting = false;
//        }
        if (hunting && hit) {
            s.possibleShipCombos(enemyShips.getShip(enemyShips.getNumberOfShips() - 1), false, lastShot);
            //System.out.println("Working hunt suggestion: "+s.huntOtherWay().x+" "+s.huntOtherWay().y);
            this.hit = lastShot;
            s.addHits(this.hit);
            // Check for "cluster" of ships
            //Check for direction
        }
        if (testing) {
            System.out.println("count 1 " + numOfEnemyShips + " count2 " + enemyShips.getNumberOfShips());
        }
        if (!hunting && hit && numOfEnemyShips == enemyShips.getNumberOfShips()) {
            hunting = true;
            this.hit = lastShot;
            s.addHits(this.hit);
            //System.out.println("Hunting enabled!");
        }
        if (testing) {
            System.out.println("count 1 " + numOfEnemyShips + " count2 " + enemyShips.getNumberOfShips());
        }
        if (testing) {
            System.out.println(numOfEnemyShips != enemyShips.getNumberOfShips());
        }
        if (numOfEnemyShips != enemyShips.getNumberOfShips()) {
            if (testing) {
                System.out.println("We get inside the if");
            }
            // if we don't have any hits now hunting should be turned off:

            ArrayList<Ship> temp = shipsLeftBeforeShot;
            for (Ship ship : shipsLeftAfterShot) {
                shipsLeftBeforeShot.remove(ship);
            }
            if (testing) {
                System.out.println("We get past the for loop");
            }
            if (!shipsLeftBeforeShot.isEmpty()) {
                lastSunk = shipsLeftBeforeShot.get(0);
                if (testing) {
                    System.out.println("Last sunk has a size of " + lastSunk.size());
                }
            } else if (testing) {
                System.out.println("Failed to find last sunken ship");
            }

            s.shipWrecked(lastShot, lastSunk.size());
            if (!s.checkForMoreTargets()) {
                hunting = false;
            }

            //System.out.println("Done hunting");
        }

        //numOfEnemyShips = enemyShips.getNumberOfShips();
        int[][] sunk = s.getSunkMap();
        if (testing) {
            System.out.println("Knowledge Map");
            for (int i = 9; i >= 0; i--) {              //Write from top -> 9
                for (int j = 0; j < 10; j++) {          //Write from x=0;
                    System.out.print(sunk[j][i] + " ");
                }
                System.out.println("");

            }
        }

        //Do nothing
    }

    /**
     * Called in the beginning of each match to inform about the number of
     * rounds being played.
     *
     * @param rounds int the number of rounds i a match
     */
    @Override
    public void startMatch(int rounds) {
        //Do nothing
    }

    /**
     * Called at the beginning of each round.
     *
     * @param round int the current round number.
     */
    @Override
    public void startRound(int round) {

        s.clearHunt();
        s.clearGridList();
        s.clearShotsFired();
        s.fillGridList();
        s.clearHits();
        s.resetShotMap();
        s.doComboMap();
        fleetMaker.clearShipMap();

        hunting = false;

        //Do nothing
    }

    /**
     * Called at the end of each round to let you know if you won or lost.
     * Compare your points with the enemy's to see who won.
     *
     * @param round int current round number.
     * @param points your points this round: 100 - number of shot used to sink
     * all of the enemy's ships.
     *
     * @param enemyPoints int enemy's points this round.
     */
    @Override
    public void endRound(int round, int points, int enemyPoints) {   // Key to succes hidden here
        if (enemyPoints < 50) {
            System.out.println("Enemy go " + enemyPoints + " trying to use same pos again");
            samePlacementAgain = true;

        } else {
            samePlacementAgain = false;
        }

        this.round = round;
        //if(points<=20 )System.out.println("80+ shots");
        if (testing) {
            if (round == 500) {
                for (int i = 9; i >= 0; i--) {              //Write from top -> 9
                    for (int j = 0; j < 10; j++) {          //Write from x=0;
                        System.out.print(enemyShots[j][i] + " ");
                    }
                    System.out.println("");

                }
                System.out.println("");
                System.out.println("Our Shots");
                for (int i = 9; i >= 0; i--) {              //Write from top -> 9
                    for (int j = 0; j < 10; j++) {          //Write from x=0;
                        System.out.print(ourShots[j][i] + " ");
                    }
                    System.out.println("");

                }
                System.out.println("");
            }
        }
        //Do nothing
    }

    /**
     * Called at the end of a match (that usually last 1000 rounds) to let you
     * know how many losses, victories and draws you scored.
     *
     * @param won int the number of victories in this match.
     * @param lost int the number of losses in this match.
     * @param draw int the number of draws in this match.
     */
    @Override
    public void endMatch(int won, int lost, int draw) {
        //Do nothing
    }
}
