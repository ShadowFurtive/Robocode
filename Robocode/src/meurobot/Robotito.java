package meurobot;
import robocode.*;
import robocode.util.Utils;
/**
 * @author Lucas Espinola & Mario Kochan
 * Fem us d'AdvancedRobot per tal de fer coses mes precises.
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
           
            //scan();
            execute();
        }
    }
    public void onScannedRobot(ScannedRobotEvent e){
       /*
        *Versió 1
        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());
        */
        //Tots els angles les normalitzem
        
        //Ara angle sera el angle absolut fins a l'enemic
        double angle = getHeadingRadians() + e.getBearingRadians();
         
        //angleRadar sera el requerido para girar hacia el enemigo
        double angleRadar = Utils.normalRelativeAngle(angle - getRadarHeadingRadians());
        //d'aquesta manera el robot estaria girant el radar amb el centre del robot enemic com a 
        //referencia, fem que s'adelanti una mica més cap a on s'esta anant l'enemic
        
        double extragir = Math.atan(40/e.getDistance());
       
        if(angleRadar < 0 ){
            angleRadar = angleRadar - extragir;
        }else{
            angleRadar = angleRadar + extragir;
        }
        setTurnRadarRightRadians(angleRadar);
        /*
        *Fem el mateix amb el gun
        */
        double angleGun = Utils.normalRelativeAngle(angle - getGunHeadingRadians());
        //d'aquesta manera el robot estaria girant el radar amb el centre del robot enemic com a 
        //referencia, fem que s'adelanti una mica més cap a on s'esta anant l'enemic
        
      
       
        if(angleGun < 0 ){
            angleGun = angleGun - extragir;
        }else{
            angleGun = angleGun + extragir;
        }
        setTurnGunRightRadians(angleGun);
        fire(1);

        
    }

}
