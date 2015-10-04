package org.games.geofox.entities;


import java.io.Serializable;
import java.util.List;

//@XmlRootElement(name = "gamestatus")
public class GameStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * gamestatus.
     *
     * 0 - game in process. 1 - game end fox win you are fox<br>
     * 2 - game end fox win you are hunter<br>
     * 3 - game end hunters win you are fox<br>
     * 4 - game end hunters win you are hunter<br>
     * 5 - game end timeout
     */
    private int gamestatus;



    /**

     * gamestatusmessage
     */
    private String gamestatusmessage;

    /**
     * myposition
     */
    private PositionData myposition;

    /**
     * foxposition
     */
    private PositionData foxposition;

    List<PositionData> huntersposition;

    /**
     * @return the gamestatus
     */
    public int getGamestatus() {
        return gamestatus;
    }

    /**
     * @param gamestatus
     *          the gamestatus to set
     */
    public void setGamestatus(final int gamestatus) {
        this.gamestatus = gamestatus;
    }

    /**
     * @return the gamestatusmessage
     */
    public String getGamestatusmessage() {
        return gamestatusmessage;
    }

    /**
     * @param gamestatusmessage
     *          the gamestatusmessage to set
     */
    public void setGamestatusmessage(final String gamestatusmessage) {
        this.gamestatusmessage = gamestatusmessage;
    }

    /**
     * @return the myposition
     */
    public PositionData getMyposition() {
        return myposition;
    }

    /**
     * @param myposition
     *          the myposition to set
     */
    public void setMyposition(final PositionData myposition) {
        this.myposition = myposition;
    }

    /**
     * @return the foxposition
     */
    public PositionData getFoxposition() {
        return foxposition;
    }

    /**
     * @param foxposition
     *          the foxposition to set
     */
    public void setFoxposition(final PositionData foxposition) {
        this.foxposition = foxposition;
    }

    public List<PositionData> getHuntersposition() {
        return huntersposition;
    }

    public void setHuntersposition(List<PositionData> huntersposition) {

        this.huntersposition = huntersposition;
    }
}
