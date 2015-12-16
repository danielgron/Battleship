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
    private NextShooter s = new NextShooter();
    private Position hit = new Position(0, 0);
    private Position lastShot = new Position(0, 0);
    private final int[][] enemyShots = new int[10][10];
    private final int[][] ourShots = new int[10][10];
    private ArrayList<Position> ourShipPos;
    private final static MySecurityManager mySecurityManager
            = new MySecurityManager();

    private ArrayList<Ship> shipsLeftBeforeShot = new ArrayList();
    private ArrayList<Ship> shipsLeftAfterShot = new ArrayList();
    private Ship lastSunk;
    private boolean samePlacementAgain = false;
    private int sumScore = 0;
    private int enPoints = 0;

    private int round = 0;
    private int numOfEnemyShips;
    private boolean testing = false;
    private boolean testingTournament = false;
    private int numberOfShots = 0;

    private FleetMaker fleetMaker;
    private boolean round1 = true;
    private boolean EnemyHasHitUs = false;
    private int lostInRow;
    private boolean firstShot;
    private int failCount;
    private boolean printRound;
    private int lost;
    private int won;

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
        prepareShooter(board);
        if (samePlacementAgain) {
           if (round > 50 && ((won * 100) / (lost * 100)) < 2){
               fleetMaker.turnMap45Degrees(board);
           }
            fleetMaker.useSamePositionAgain(fleet, board);
        } else {
            fleetMaker.clearArrays();
            fleetMaker.placeOurShips(fleet, board);
        }
        ourShipPos = fleetMaker.getSavedPos();

    }

    public void prepareShooter(Board board) {
        if (round1) {
            s.setBoardX(board.sizeX());
            s.setBoardY(board.sizeY());

            s.startMaps();

            round1 = false;
        }
        s.fillGridList();
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
        if (!EnemyHasHitUs && !s.checkIfHit(pos)) {
            s.enemyMissBeforeHit(pos);
            fleetMaker.heatUpHeatMap(pos);
            //System.out.println("We get here!");
        } else if (s.checkIfHit(pos)) {
            EnemyHasHitUs = true;
            //if (round%50==0)System.out.println("Round before the hit first: "+ numberOfShots);
        }
        //Do nothing

        numberOfShots++;
        //if (numberOfShots< fleetMaker.numberOfShotsCounted) {
        //    fleetMaker.heatUpHeatMap(pos);
        //}
        // System.out.println(mySecurityManager.getCallerClassName(2));
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
        if (firstShot) {
            s.makeOurBoard(ourShipPos, fleetMaker.getVerticalSave());
            firstShot = false;
        }
        numOfEnemyShips = enemyShips.getNumberOfShips();
        shipsLeftBeforeShot.clear();
        for (Ship enemyShip : enemyShips) {
            shipsLeftBeforeShot.add(enemyShip);
        }
        s.setEnemyRemaining(shipsLeftBeforeShot);
        s.setOP(this);
        Position shot = null;
        if (!hunting) {
            shot = s.shootInGrid(chooseGrid());
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
        //System.out.println("Ships left "+ enemyShips.getNumberOfShips());
        shipsLeftAfterShot.clear();
        s.addShot(lastShot, hit);
        if (enemyShips.getNumberOfShips() > 0) {

            for (Ship enemyShip : enemyShips) {
                shipsLeftAfterShot.add(enemyShip);

            }
        }

        if (hunting && hit && enemyShips.getNumberOfShips() != 0) {
            s.possibleShipCombos(enemyShips.getShip(enemyShips.getNumberOfShips() - 1), false, lastShot);
            //System.out.println("Working hunt suggestion: "+s.huntOtherWay().x+" "+s.huntOtherWay().y);
            this.hit = lastShot;
            s.addHits(this.hit);
            // Check for "cluster" of ships
            //Check for direction
        }
        if (printRound && enemyShips.getNumberOfShips() == 0) {
            s.printOutMap(1);
            printRound = false;
            //System.out.println("count 1 " + numOfEnemyShips + " count2 " + enemyShips.getNumberOfShips());
        }
        if (!hunting && hit && numOfEnemyShips == enemyShips.getNumberOfShips()) {
            hunting = true;
            this.hit = lastShot;
            s.addHits(this.hit);
            //System.out.println("Hunting enabled!");
        }
        if (false) {
            System.out.println("count 1 " + numOfEnemyShips + " count2 " + enemyShips.getNumberOfShips());
        }
        if (testing) {
            System.out.println(numOfEnemyShips != enemyShips.getNumberOfShips());
        }
        if (numOfEnemyShips != enemyShips.getNumberOfShips()) {
            if (false) {
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
        fleetMaker.initialHeat();
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
        s.clearHits();
        s.resetShotMap();
        s.coolDownPlacement();
        s.newRound();
        //s.doComboMap();
        fleetMaker.clearShipMap();
        EnemyHasHitUs = false;
        firstShot = true;

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
        fleetMaker.cooldownHeatMap();
        this.round = round;
        if (points < enemyPoints) {
            lost++;
        }
        if (enemyPoints < points) {
            won++;
        }

        if (round % 100 == 0) {
            fleetMaker.printOutHeatMap(round);
            s.printOutMap(5);
            s.printOutMap(6);
        }
        //System.out.println("Turns to wreck: "+(100-points));
        sumScore += (100 - points);
        enPoints += (100 - enemyPoints);
        if (points < enemyPoints && points < 40) {
            lostInRow++;
        } else {
            lostInRow = 0;
        }

        if (enemyPoints < 50) {
            //System.out.println("Enemy go " + enemyPoints + " trying to use same pos again");
            samePlacementAgain = true;

        } else {
            samePlacementAgain = false;
        }
        if (testingTournament && points == 0) {
            System.out.println("We used 100 shots");
        }

        this.round = round;
        //if(points<=20 )System.out.println("80+ shots");
        if (testingTournament) {
            if (round % 100 == 0) {
                fleetMaker.printOutHeatMap(round);
                System.out.println("FailCount " + failCount);
                System.out.println("Their shots");
                for (int i = 9; i >= 0; i--) {              //Write from top -> 9
                    for (int j = 0; j < 10; j++) {          //Write from x=0;
                        System.out.print(enemyShots[j][i] + " ");
                    }
                    System.out.println("");

                }
                System.out.println("");
                System.out.println("Our Shots at round " + round);
                for (int i = 9; i >= 0; i--) {              //Write from top -> 9
                    for (int j = 0; j < 10; j++) {          //Write from x=0;
                        System.out.print(ourShots[j][i] + " ");
                    }
                    System.out.println("");

                }
                System.out.println("");
                System.out.println("Cooldown at round " + round);
                s.printOutMap(6);
                System.out.println("");
                System.out.println("Our avg " + sumScore / round);
                System.out.println("Their avg " + enPoints / round);
                s.printOutMap(0);
            }
        }
        numberOfShots = 0;
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

    private int chooseGrid() {
        if (round <= 50) {
            return 0;
        } else if (round > 50 && ((won * 100) / (lost * 100)) < 2) {
            return 1;
        } else {
            return 0;
        }
    }

}
