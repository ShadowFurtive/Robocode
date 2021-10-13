package meurobot;
import java.awt.Color;
import java.awt.geom.Point2D;
import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import robocode.*;
/**
 * ElRobot - a robot by (your name here)
 */
public class ElRobot extends AdvancedRobot
{
	 boolean direccio = false; //false = der , true = izq
    boolean forwardmove = true;
    boolean nearfocus = false;
    boolean zonaperill;
	double lastEnemyHeading;
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
        if (getX() <= 50 || getY() <= 50 || 
                getBattleFieldWidth() - getX() <= 50 || 
                getBattleFieldHeight() - getY() <= 50) {
				zonaperill = true;
			} else {
			zonaperill = false;
		}
           forwardmove = true;
           
        while(true){
        if(getRadarTurnRemaining()== 0.0){
            setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
        }
           
            //scan();
            
            System.out.println("111111");
            nearWall();
            
            execute();  
        }
        
        
        
    }
    public void onScannedRobot(ScannedRobotEvent e){

        
        //Ara angle sera el angle absolut fins a l'enemic
        System.out.println("aa");
        double angle = getHeadingRadians() + e.getBearingRadians();
        //angleRadar sera el requerido para girar hacia el enemigo
        double angleRadar = Utils.normalRelativeAngle(angle - getRadarHeadingRadians());
        //d'aquesta manera el robot estaria girant el radar amb el centre del robot enemic com a 
        //referencia, fem que s'adelanti una mica més cap a on s'esta anant l'enemic

        double extragir = Math.atan(40/e.getDistance());//Per ampliar el tamany del radar (un extra)

        if(angleRadar < 0 ){
            angleRadar = angleRadar - extragir;
        }else{
            angleRadar = angleRadar + extragir;
        }
        setTurnRadarRightRadians(angleRadar);
        /*
        *Fem el mateix amb el gun
        */

        Dispara(e);
        Moviment(e,angle);	
		
    }
public void Moviment(ScannedRobotEvent e,double angle){
    if(e.getDistance()<224){
        
        if(forwardmove){
            setBack(100);
        }
        else {
            setAhead(100);
        }
        fire(3);
    }
    else{	
        if(e.getDistance()>425){
            GoToEnemy(e.getDistance(),Utils.normalRelativeAngle(angle - getHeadingRadians()));
        }
        if(e.getDistance()<424){
            RotateAround((e.getBearing()));
            fire(2);
        }else{
            if(getEnergy()>50){
                fire(1);
            }
        }
    }
    
}
    public void GoToEnemy(double dist,double bear){

        System.out.println("GOING TO ENEMMMMIII");
        setTurnRightRadians(bear);
         //waitFor(new TurnCompleteCondition(this));
       
        setAhead(dist - 275);
    
        
        nearfocus = true;     

    }
public void Dispara(ScannedRobotEvent e){
    if(e.getVelocity()==0){
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians()));
    }
    else{

        
        double enemyHeading = e.getHeadingRadians();
        
        double turnRatio = enemyHeading - lastEnemyHeading;
        double bearing = getHeadingRadians() + e.getBearingRadians();
        //Posicio actual relativa del enemigo.
        double relativeX = getX()+e.getDistance()*Math.sin(bearing);
        double relativeY = getY()+e.getDistance()*Math.cos(bearing);
                //error de que accediamos 10000 veces y se dissable el robot
        double myX = getX();
        
        double myY = getY();
        /*
        *Predicción de la posicio futura con 1 segundo de temps.
        *Si quisiesemos que fuera de 2 segundos habria que multiplicar por n turnRatio.
        *Donde n es el numero de segundos.
        */ 
        //double bulletTime = e.getDistance()/(20-(3*2));
        
        double futureX = relativeX;// + e.getVelocity()*Math.sin(e.getHeadingRadians());
        double futureY = relativeY;// + e.getVelocity()*Math.cos( e.getHeadingRadians());
        double distance = Math.sqrt((myX-relativeX)*(myX-relativeX) + (myY-relativeY)*(myY-relativeY));
        lastEnemyHeading = enemyHeading;
        
        double bulletTime = (20-(3*3));
        double time = 0;
        while((time*bulletTime)<distance){
           
            
            futureX +=  e.getVelocity()*Math.sin(enemyHeading);
            futureY +=  e.getVelocity()*Math.cos( enemyHeading);
           enemyHeading = enemyHeading + turnRatio;
            
            distance = Math.sqrt((myX-futureX)*(myX-futureX) + (myY-futureY)*(myY-futureY));  

//            if(futureX<18 ||
//                futureY<18 ){
//             
//                break;
//            }else if(futureX > getBattleFieldWidth()-18 ||
//                      futureY > getBattleFieldHeight()-18){
//                futureX = getBattleFieldWidth()-18;
//                futureY = getBattleFieldHeight()-18;
//                break;
//            }
                if(	futureX < 18.0 
                       || futureY < 18.0
                       || futureX > getBattleFieldWidth() - 18.0
                       || futureY > getBattleFieldHeight() - 18.0){                      
                            futureX = Math.min(Math.max(18.0, futureX), 
                           getBattleFieldWidth() - 18.0);
                        futureY = Math.min(Math.max(18.0, futureY), 
                            getBattleFieldHeight() - 18.0);
                        break;
               }
            time++;
            
        }
        
        

        //Angulo predecido que se deberia mover el rival respecto a nosotros
        
        
        double angle = Utils.normalAbsoluteAngle(Math.atan2(futureX-getX(), futureY-getY()));

        setTurnGunRightRadians(Utils.normalRelativeAngle(angle - getGunHeadingRadians()));
        


    }
}    

public void nearWall(){
    double d = 75;
    if (getX() > d && getY() > d && 
           getX() < getBattleFieldWidth()- d && 
            getY() < getBattleFieldHeight() -  d && 
            zonaperill == true) {
                zonaperill = false;
    }
    if (getX() <= d || getY() <= d || 
             getX() >=getBattleFieldWidth() - d || 
           getY() >=  getBattleFieldHeight() - d ) {
            if ( zonaperill == false){
                ChangeDirection();
                zonaperill = true;
            }
    } 
}


    public void RotateAround(double bear){
       
        System.out.println("FENT ROTATE");
        
        setTurnRight(Utils.normalRelativeAngleDegrees(bear+90));
        if(forwardmove){
            setAhead(50);
        }else{
            setBack(50);
           
        }
        nearWall();
    }
    public void ChangeDirection(){
        System.out.println("CHANGEDIR");
        if(forwardmove){
            setBack(50);
            forwardmove = false;
        }else{
            setAhead(50);
            forwardmove = true;
        }
      }
    public void onHitWall(HitWallEvent e) {
		// Bounce off!
                System.out.println("hitWALLLLL");
		ChangeDirection();
	}
   	public void onHitRobot(HitRobotEvent e) {
		// If we're moving the other robot, reverse!
		if (e.isMyFault()) {
			ChangeDirection();
		}
	}
}
