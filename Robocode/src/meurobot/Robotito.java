package meurobot;
import java.awt.Color;
import robocode.*;
import robocode.util.Utils;
/**
 * @author Lucas Espinola & Mario Kochan
 * Fem us d'AdvancedRobot per tal de fer coses mes precises.
 */
public class Robotito extends AdvancedRobot {
    
    boolean direccio = false; //false = der , true = izq
    boolean forwardmove = true;
    boolean nearfocus = false;
    boolean zonaperill;
    public void run(){
        /*
        * Fem un scaneig a tot arreu fins trobar a l'enemic.
        * 1)Amb getRadarTurnRemaining() veiem el comportament del radar(volem movement)
        *   la funcio retorna double: (<-)-1; (quiet) 0; (->)1
        */
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setBodyColor(new Color(255, 0, 0));
        if (getX() <= 50 || getY() <= 50 || getBattleFieldWidth() - getX() <= 50 || getBattleFieldHeight() - getY() <= 50) {
				zonaperill = true;
			} else {
			zonaperill = false;
		}
           setAhead(40000);
           forwardmove = true;
           
        while(true){
            if(getRadarTurnRemaining()== 0.0){
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            }
           
            //scan();
            
            System.out.println("111111");
                if (getX() > 50 && getY() > 50 && getBattleFieldWidth() - getX() > 50 && getBattleFieldHeight() - getY() > 50 && zonaperill == true) {
                        zonaperill = false;
                }
                if (getX() <= 50 || getY() <= 50 || getBattleFieldWidth() - getX() <= 50 || getBattleFieldHeight() - getY() <= 50 ) {
                        if ( zonaperill == false){
                                ChangeDirection();
                                zonaperill = true;
                        }
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
        System.out.println("aa");
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
            if(e.getDistance()<60){
                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAA");
                turnRight(e.getBearing()+180);
                ahead(100);
                //waitFor(new MoveCompleteCondition(this));
            }else{
                if(e.getDistance()>160){
                    System.out.println("NEEEEAR");
                    nearfocus = false;
                }
                if(e.getDistance()>40 && !nearfocus){
                    GoToEnemy(e.getDistance(),Utils.normalRelativeAngle(angle - getHeadingRadians()));
                }
                if(e.getDistance()<200 && e.getDistance()>100 && nearfocus){
                    RotateAround((e.getBearing()));
                }
            }
       if(getEnergy()>50 && e.getDistance()<160){
           fire(1);
       }else{
           
       }

        
    }
    public void GoToEnemy(double dist,double bear){
//        if(bear>0 && bear<180){
//            setTurnRight(bear);
//        }else{
//            setTurnLeft(bear);
//        }
        System.out.println("GOING TO ENEMMMMIII");
        setTurnRightRadians(bear);
         //waitFor(new TurnCompleteCondition(this));
       
            setAhead(dist - 150);
    
        
        nearfocus = true;     

    }
    public void RotateAround(double bear){
        //rotate derecha i izquierda
//        setTurnLeftRadians(bear+ Utils.normalRelativeAngle(Math.toRadians(90)));
//        setAhead(10);
//        waitFor(new MoveCompleteCondition(this));
       
        System.out.println("FENT ROTATE");
        aux();
        setTurnRight(Utils.normalRelativeAngleDegrees(bear+90));
        if(forwardmove){
            setAhead(100);
        }else{
             setBack(100);
           
        }
    }
    public void aux(){
        if (getX() > 50 && getY() > 50 && getBattleFieldWidth() - getX() > 50 && getBattleFieldHeight() - getY() > 50 && zonaperill == true) {
                        zonaperill = false;
                }
                if (getX() <= 50 || getY() <= 50 || getBattleFieldWidth() - getX() <= 50 || getBattleFieldHeight() - getY() <= 50 ) {
                        if ( zonaperill == false){
                                ChangeDirection();
                                zonaperill = true;
                        }
                }
            
    }
    public void ChangeDirection(){
        System.out.println("CHANGEDIR");
        if(forwardmove){
            setBack(100);
            forwardmove = false;
        }else{
            setAhead(100);
            forwardmove = true;
        }
      // waitFor(new MoveCompleteCondition(this));
       
       
//        forwardmove = !forwardmove;
    }
    public void onHitWall(HitWallEvent e) {
		// Bounce off!
                System.out.println("hitWALLLLL");
		ChangeDirection();
	}
//   	public void onHitRobot(HitRobotEvent e) {
//		// If we're moving the other robot, reverse!
//		if (e.isMyFault()) {
//			ChangeDirection();
//		}
//	}
}
