package meurobot;
import java.awt.Color;
import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import robocode.*;
/*
 * Robocode. Act Prop
 * Authors: 
 * Mario Konstanty Kochan Chmielik
 * Lucas Efrain Espinola Benitez
 */
public class LaTulaMecanica extends AdvancedRobot
{
    boolean forwardmove = true;
    boolean zonaperill;
    double lastEnemyHeading;
    int powerFire = 1;
    
    public void run(){
            setAdjustGunForRobotTurn(true);
            setAdjustRadarForRobotTurn(true);
            setAdjustRadarForGunTurn(true);
            setBodyColor(new Color(255, 0, 0));
            if(getX() <= 50 || getY() <= 50 || 
                getBattleFieldWidth() - getX() <= 50 || 
                getBattleFieldHeight() - getY() <= 50) {

                zonaperill = true;
            }else{

                zonaperill = false;

            }
            forwardmove = true;

            while(true){
                if(getRadarTurnRemaining()== 0.0){

                    setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

                }
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
            powerFire = 3;
        }
        else{	
            if(e.getDistance()>425){
                GoToEnemy(e.getDistance(),Utils.normalRelativeAngle(angle - getHeadingRadians()));
            }
            if(e.getDistance()<424){
                RotateAround((e.getBearing()));
                fire(2);
                powerFire = 2;
            }else{
                if(getEnergy()>50){
                    fire(1);
                    powerFire = 1;
                }
            }
        }

    }
    public void GoToEnemy(double dist,double bear){

        System.out.println("GOING TO ENEMMMMIII");
        setTurnRightRadians(bear);

        setAhead(dist - 275);


    }
    public void Dispara(ScannedRobotEvent e){

        if(e.getVelocity()==0){

            double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
            setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians()));

        }
        else{
            double enemyHeading = e.getHeadingRadians();
            /*
            *TurnRatio defineix la variacio de angle que fa falta girar el nostre gun, es a dir,
            * la variacio en angle del moviment de l'enemic.
            */
            double turnRatio = enemyHeading - lastEnemyHeading;
            double bearing = getHeadingRadians() + e.getBearingRadians();
            /*
            *Posicio actual relativa del enemigo.
            */
            double relativeX = getX()+e.getDistance()*Math.sin(bearing);
            double relativeY = getY()+e.getDistance()*Math.cos(bearing);
            /*
            *A causa de que en ocasiones nos producia error de "demasiados accesos a getX() o getY()"
            *  decidimos declarar al pincipio una variable myX y myY de manera que se accederia a la funcion
            *  en menos ocasiones.
            */
            double myX = getX();
            double myY = getY();
            /*
            *Future(X,Y) defineix la variable on posem la nostra predicció de la futura posicio del rival
            *En un principi l'igualem a la posició relativa, despres es veura modificada.
            */
            double futureX = relativeX;
            double futureY = relativeY;
            /*
            *Distance = modul.
            */
            double distance = Math.sqrt((myX-relativeX)*(myX-relativeX) + (myY-relativeY)*(myY-relativeY));

            lastEnemyHeading = enemyHeading;

            //Depende del tamaño, va mas rapido o mas lento la bala
            double bulletTime = (20-(3*powerFire));
            double time = 0;
            while((time*bulletTime)<distance){

                /*
                *Predicción de la posicio futura del rival con 1 segundo de temps.
                *Si quisiesemos que fuera de 2 segundos habria que multiplicar por n turnRatio.
                *Donde n es el numero de segundos.
                */       
                futureX +=  e.getVelocity()*Math.sin(enemyHeading);
                futureY +=  e.getVelocity()*Math.cos( enemyHeading);
                enemyHeading = enemyHeading + turnRatio;

                distance = Math.sqrt((myX-futureX)*(myX-futureX) + (myY-futureY)*(myY-futureY));  
                double dist = bulletTime+4.0;
                /*
                *Si alguno de las distancias predichas son menores al calculo del tiempo de la bala
                *  en recorrer cierta distancia(con un margen de 4, valor a base de prueba i error)
                *Entonces dichas distancias se ven guardadas. Se pueden ver sobreescritas en el caso de que
                *  nos encontremos en las posiciones contrarias de las coordenadas (0,0), basicamente, cerca del 
                *  widht y/o del height del battlefield.
                */
                if(futureX<=dist ||
                    futureY<=dist ){

                    if(futureX > getBattleFieldWidth()-dist){
                       futureX = getBattleFieldWidth()-dist;
                    }
                      if(futureY > getBattleFieldHeight()-dist){
                         futureY = getBattleFieldHeight()-dist;
                    }
                    break;

                }else if(futureX > getBattleFieldWidth()-dist ||
                         futureY > getBattleFieldHeight()-dist){

                    if(futureX > getBattleFieldWidth()-dist){

                       futureX = getBattleFieldWidth()-dist;
                    }
                      if(futureY > getBattleFieldHeight()-dist){

                         futureY = getBattleFieldHeight()-dist;
                    }
                    break;

                }
                time++;

            }


        /*
        *Angulo predecido que se deberia mover el rival respecto a nosotros
        *Aqui lo que hace es conseguir un angulo entre las posibles futuras posiciones del robot, y 
        *  nuestras posiciones actuales.
        *Luego se le resta respecto al eje de coordenada del Battlefield, los grados ya realizados de la torreta
        *  para encontrar asi el angulo retante necesario para el giro correcto.
        */  
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
                    /*
                    *De esta manera se produce fallos al encontrarse 
                    *encerrado en una esquina
                    */
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
