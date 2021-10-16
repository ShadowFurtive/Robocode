/**
 * Robocode. Act Prop 
 * @author Mario Konstanty Kochan Chmielik
 * @author Lucas Efrain Espinola Benitez
 **/
package meurobot;
import java.awt.Color;
import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import robocode.*;

public class LaTulaMecanica extends AdvancedRobot
{
    boolean forwardmove = true;
    boolean zonaperill;
    double lastEnemyHeading;
    int powerFire = 1;
    /**
     * Metodo que define el comportamiento de las diferentes partes del robot, ademas de definir el movimiento 
     * inicial del radar y del robot,este ultimo dependiendo de la distancia con el muro. 
     *  @see nearWall()
     */
    public void run(){
            setAdjustGunForRobotTurn(true);
            setAdjustRadarForRobotTurn(true);
            setAdjustRadarForGunTurn(true);
            setGunColor(new Color(0, 102, 0));
            setBodyColor(new Color(0,0,0));
            setRadarColor(new Color(0, 102, 0));
            setBulletColor((new Color(0,0,0)));
            setScanColor(new Color(0,0,0));

            
            /*if(getX() <= 50 || getY() <= 50 || 
                getBattleFieldWidth() - getX() <= 50 || 
                getBattleFieldHeight() - getY() <= 50) {

                zonaperill = true;
                ChangeDirection();
            }else{

                zonaperill = false;

            }*/
            //nearWall();
            while(true){
                
                if(getRadarTurnRemaining()== 0.0){
                    setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

                }
                System.out.println("111111");
                setAhead(10);
                nearWall();
                execute();  
            }



        }
            
     /**
     * Desde este metodo lo que hacemos es controlar el comportamiento del radar durante toda la partida,
     * ademas llamamos a las funciones de movimiento y disparo.
     * @see nearWall() Dispara(ScannedRobotEvent ) Moviment(ScannedRobotEvent , double)
     */
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


        Dispara(e);
        nearWall();//Extra
        Moviment(e,angle);
        

    }
    
    /**
     * Motodo el cual apartir de nuestra distancia con respecto al otro robot realiza diferente acciones tales como
     * llamar a la funcion para acercarse al enemigo o rotar alrededor suyo y disparar al rival con una potenciad dependiente a 
     * la distancia. Es llamado desde el metoedo onScanRobot.
     * 
     * @param e es el evento producido cuando el radar detecta a un rival.
     * @param angle es el angulo necesario para que nuestro robot pueda girar hacia el enemigo.
     * @see GoToEnemy(double ,double ) RotateAround(double )  onScannedRobot(ScannedRobotEvent )
    */
    public void Moviment(ScannedRobotEvent e,double angle){

        if(e.getDistance()<224){
            fire(3);
            powerFire = 3;
            RotateAround(e.getBearing());

            if(forwardmove){
                setBack(100);
            }
            else {
                setAhead(100);
            }            

            
            
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
    /**
     * Metodo que se encarga de realizar el movimiento del robot hacia el rival. Es llamado desde el metodo Moviment.
     * 
     * @param dist es la distancia que hay hacia el rival desde nuestro robot.
     * @param bear es el angulo normalizado para realizar el giro necesario para que el robot este mirando al rival.
     * 
     * @see Moviment(ScannedRobotEvent,double)
     */
    public void GoToEnemy(double dist,double bear){

        System.out.println("GOING TO ENEMMMMIII");
        setTurnRightRadians(bear);
        
        nearWall();
        setAhead(dist - 275);
        nearWall();


    }
    /**
     * Metodo encargado de hacer los calculos necesarios para la posicion de apuntado del arma y posteriormente apuntar a dicho sitio.
     * Los calculos son realizados apartir de las variables otorgadas por ScannedRobotEvent y aplicando sobre ellas un algoritmo basado en
     * "Circular Targeting".
     * @param e es el evento producido cuando el radar detecta a un rival.
     * 
     * @see onScannedRobot(ScannedRobotEvent )
     */
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
    /**
     * Metodo que sirve para detectar si el robot se encuentra en una zona "peligrosa" o segura.
     * Example:
     *      Si el robot se encuentra a una distancia < 120 hacia la pared indicamos que el robot
     *      esta en una zona de peligro cambiando el valor de una variable booleana global.
     */
    public void nearWall(){
        double d = 120;
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
                     /*Provessssss Esquina*/
                        
                    if(getX() >=getBattleFieldWidth() - d && 
                       getY() >=  getBattleFieldHeight() - d ){
                        setTurnRight(getHeading()+45);
                        waitFor(new TurnCompleteCondition(this));
                    }

                     /**************/
                    ChangeDirection();
                    zonaperill = true;
                }
        }
        

    }
    /**
     *Es el metodo encargado de hacer que el robot realize un giro alrededor del rival.
     * 
     * @param bear es el angulo normalizado para realizar el giro necesario para que el robot este mirando al rival.
     */
    public void RotateAround(double bear){

        System.out.println("FENT ROTATE");
        nearWall();///Extra
        setTurnRight(Utils.normalRelativeAngleDegrees(bear+90));
        if(forwardmove){
            setAhead(20);
        }else{
            setBack(20);

        }
        nearWall();
    }
   
   /**
    * Es un metodo sencillo, encargado de cambiar la dirección del robot en las ocasiones que se le sea llamado, hacemos uso de una variable global
    * dependiendo de su valor nos indica si estamos yendo hacia delante o hacia atras y eso nos permite invertir el movimiento.
    * 
    */ 
    public void ChangeDirection(){
        System.out.println("CHANGEDIR");
        if(forwardmove){
            setBack(20);
            forwardmove = false;
        }else{
            setAhead(20);
            forwardmove = true;
        }
        
      }
    
    /**
     * Metodo que determina la acción a realizar en caso de coalision contra un muro.
     * @see ChangeDirection()
     */
    public void onHitWall(HitWallEvent e) {

        System.out.println("hitWALLLLL");
        ChangeDirection();
    }
    /**
     * Metodo que determina la acción a realizar en caso de coalisionn contra un rival.
     * @see ChangeDirection()
     */
    public void onHitRobot(HitRobotEvent e) {
        if (e.isMyFault()) {
                ChangeDirection();
        }
    }
    
}
