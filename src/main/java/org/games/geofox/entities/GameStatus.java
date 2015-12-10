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
     * gametype
     */
    private int gametype;

    /**
     * gamestatusmessage
     */
    private String gamestatusmessage;

    /**
     * myposition
     */
    private MemberData myposition;

    /**
     * foxposition
     */
    private MemberData foxposition;

    List<MemberData> huntersposition;

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

    public int getGametype() {
        return gametype;
    }

    public void setGametype(int gametype) {
        this.gametype = gametype;
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
    public MemberData getMyposition() {
        return myposition;
    }

    /**
     * @param myposition
     *          the myposition to set
     */
    public void setMyposition(final MemberData myposition) {
        this.myposition = myposition;
    }

    /**
     * @return the foxposition
     */
    public MemberData getFoxposition() {
        return foxposition;
    }

    /**
     * @param foxposition
     *          the foxposition to set
     */
    public void setFoxposition(final MemberData foxposition) {
        this.foxposition = foxposition;
    }

    public List<MemberData> getHuntersposition() {
        return huntersposition;
    }

    public void setHuntersposition(List<MemberData> huntersposition) {

        this.huntersposition = huntersposition;
    }
}
