package meurobot;
import robocode.*;
import robocode.util.Utils;
/**
 * @author Lucas Espinola & Mario Kochan
 * Fem us d'AdvancedRobot per tal de fer coses mes precises.
 */
public class Robotito extends AdvancedRobot {
    
    boolean direccio = false; //false = der , true = izq
    boolean hitwall = false;
    
    public void run(){
        /*
        * Fem un scaneig a tot arreu fins trobar a l'enemic.
        * 1)Amb getRadarTurnRemaining() veiem el comportament del radar(volem movement)
        *   la funcio retorna double: (<-)-1; (quiet) 0; (->)1
        */
        setAdjustGunForRobotTurn(false);
        setAdjustRadarForRobotTurn(false);
        setAdjustRadarForGunTurn(false);
       

        while(true){
            if(getRadarTurnRemaining()== 0.0){
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            }
           
            //scan();
            
                  
            if(nearWall()){
                setBack(80);
                waitFor(new MoveCompleteCondition(this));
            }
       
            
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

            
            
            setTurnGunRightRadians(angleGun);
            if(e.getDistance()>40){
                GoToEnemy(e.getDistance(),Utils.normalRelativeAngle(angle - getHeadingRadians()));
            }
            if(e.getDistance()<200 && e.getDistance()>150){
                RotateAround(Utils.normalRelativeAngle(angle - getHeadingRadians()));
            }
            
           // fire(1);
//            setTurnRightRadians(angleGun);
//            //if((e.getEnergy()<getEnergy())){
//                if(e.getDistance()<20){
//                   setTurnRightDegrees(e.getBearing());
//                   setAhead(e.getDistance());
//                }else{
//                    double angleZigZag;
//    
//                    double distZigZag;
//                    if(!(direccio)){
//                        angleZigZag = e.getBearing() + 45;
//                        distZigZag =50;
//                        setTurnRightDegrees(angleZigZag);
//                        if(nearWall()){
//                           setBack(distZigZag); 
//                        }else{
//                            setAhead(distZigZag);
//                        }
//                        direccio = true;
//                    }else{
//                        angleZigZag = e.getBearing() + 45;
//                        distZigZag = 50;
//                        setTurnLeftDegrees(angleZigZag);
//                        if(nearWall()){
//                           setBack(distZigZag); 
//                        }else{
//                            setAhead(distZigZag);
//                        }
//                  
//                        direccio = false;
//                    }
//                    
//                }
                    
        
    }
    public void GoToEnemy(double dist,double bear){
//        if(bear>0 && bear<180){
//            setTurnRight(bear);
//        }else{
//            setTurnLeft(bear);
//        }
        setTurnRightRadians(bear);
         //waitFor(new TurnCompleteCondition(this));
        setAhead(dist-150);
        fire(1);
    }
    public void RotateAround(double bear){
        
        
    }
    public boolean nearWall(){
        
        //Paret Izq
        if(getX()<=50 || getY()<=50 || 
           getX()>=getBattleFieldWidth()-50 || 
           getY()>= getBattleFieldHeight()-50){
            System.out.println("TRUEEEE");
            return true;
        }                            
        System.out.println("FAALLLSEEEE");
        return false;
        
    }
    
}
