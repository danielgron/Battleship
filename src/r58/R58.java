/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package r58;

import battleship.interfaces.BattleshipsPlayer;
import tournament.player.PlayerFactory;

/**
 *
 * @author Tobias Grundtvig
 */
public class R58 implements PlayerFactory<BattleshipsPlayer>
{

    public R58(){}
    
    
    @Override
    public BattleshipsPlayer getNewInstance()
    {
        return new OurPlayer();
    }

    @Override
    public String getID()
    {
        return "R58";
    }

    @Override
    public String getName()
    {
        return "Rock and Roll Zygicide";
    }
    
}
