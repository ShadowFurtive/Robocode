package meurobot;
import robocode.*;
/**
 *
 * @author lucas
 */
public class Robotito extends AdvancedRobot {
    public void run(){
        /*
        * Fem un scaneig a tot arreu fins trobar a l'enemic.
        * 1)Amb getRadarTurnRemaining() veiem el comportament del radar(volem movement)
        *   la funcio retorna double: (<-)-1; (quiet) 0; (->)1
        */
        while(true){
            if(getRadarTurnRemaining()== 0.0){
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            }
            execute();
        }
    }
    public void onScannedRobot(ScannedRobotEvent e){
        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());
    }

}
