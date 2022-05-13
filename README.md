# ROBOCODE - PROP
## Creación de nuestro propio Robocode - La Tula Mecánica

[![N|Solid](https://www.upc.edu/comunicacio/ca/identitat/descarrega-arxius-grafics/fitxers-marca-principal/upc-positiu-p3005.png)](https://www.epsevg.upc.edu/ca/escola)

![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)

Proyecto realizado en parejas para la primera actividad de la asignatura PROP de la EPSEVG-UPC. 

- Autores: Mario Kochan y [Lucas Efrain](https://github.com/Lucaslluc)
- Fecha: 18/09/2021
- Profesor: Bernat Orellana

## Características

- Programación en [Java](https://www.java.com/es/)
- Uso del programa [Robocode](https://robocode.sourceforge.io/)
- Uso de [GITHUB](https://github.com/)
- Uso de [NetBeans](https://netbeans.apache.org/download/index.html)

## La Tula Mecánica
### Estrategia
La tula mecánica empieza la partida iniciando el radar y girandolo 360º. Cuando encuentra un objetivo, inicia un movimiento rectilíneo hacia él, hasta llegar a cierta distancia. Cuando se halla en esta distancia, empieza a moverse en círculos alrededor suya. 
El sistema de disparo se basa en 2 métodos: Si el objetivo está quieto, se dispara a máxima potencia. Si el objetivo se mueve, se usa el algoritmo mencionado anteriormente, Circular Targeting, para calcular las posiciones futuras del objetivo y así disparar con más precisión.
Durante el movimiento, puede ocurrir que nuestro robot se halle cerca de los muros. Hemos diseñado una función que detecta cuando está cerca del muro, para así poder alejarse. 
### Algoritmo del radar
El radar mpieza girando 360º hacia la derecha, de forma positiva. Gira de forma infinita hasta que encuentra un objetivo. En el momento de encontrar un objetivo, calcula el giro necesario para que el radar se coloque sobre él  y a la vez, en ángulo de abertura del radar para poder mantener bajo observación el robot hostil en todo momento. 

### Estrategia del movimiento
Dependiendo de la distancia que se encuentre nuestro robot, la potencia de fuego varía. A cual mayor distancia contra el objetivo, la potencia de fuego es menor. A medida que se va acercando la potencia de fuego aumenta. Hay un condicionante, y es que cuando está una distancia lejana y tiene poca salud, no dispara. Si está cerca y tiene poca salud, dispara igualmente.
#### Cerca del enemigo
Cuando nuestro robot se halla cerca del enemigo, a una distancia arbitraria	seleccionada por los programadores, se aleja del objetivo en la dirección contraria a la que se acerca. 
Planteamos que los choques siempre son frontales (respecto a la dirección de movimiento del carro), ya que no tenemos en cuenta ni los choques laterales ni traseros.

#### Lejos del enemigo
Nuestro robot detecta que está lejos del enemigo, a una distancia arbitrari	seleccionada por los programadores. Procede a acercarse al enemigo. Obtiene el ángulo que debe girar respecto al eje de coordenadas para encararse al robot hostil y avanza una distancia. Esta distancia está compuesta por la	distancia que hay entre nuestro robot y el enemigo y un valor arbitrario que hay que restar, para así poder mantener una distancia constante entre el contrincante y nuestro robot.
#### Distancia prudente al enemigo
Cuando se encuentra a una distancia prudente, que dicha distancia está seleccionada de forma arbitraria por los programadores, se mantiene a esa distancia	realizando giros alrededor del objetivo. 

Para mantener una rotación continua, simplemente se debe sumar 90º al ángulo de giro del enemigo. Así se obtiene el ángulo que debe girar el robot para mantenerse alrededor del hostil. El robot se debe mantener en movimiento para que los giros se realicen correctamente.
![N|SOLID](https://lh3.googleusercontent.com/pw/AM-JKLWC1OJPac1d2_CM4FZOpEpbASD5zd2m0G6nuMOzM0VevtzvY4qCJN54gM4GULMtdHGFSOWYrrZWukbD6jU0V0MediZD0X8-kBb8s0C4Oze9CiRFtGsPMZRy1JGuGsQWDz7e2ZhDgSH-sMbNPCVgMRZV=w665-h345-no?authuser=0)


### Estrategia de disparo
#### Cuando el objetivo está quieto
Cuando el objetivo está quieto se realiza un cálculo para obtener el ángulo de giro que debe girar la torreta para colocarse sobre el enemigo. Luego, se le resta el ángulo propio del cañón. Asi se obtiene el ángulo completo de giro del cañón para que pueda apuntar al contrario.

#### Cuando está en movimiento
Como hemos mencionado varias veces, nos basamos en el algoritmo “Circular targeting” para realizar los cálculos:
1. Obtenemos el ratio de giro del enemigo. Se obtiene restando la dirección actual de movimiento con la anterior. 
2. Obtenemos el ángulo total entre nuestro robot y el hostil
3. Obtenemos las posiciones relativas del enemigo mediante la distancia que nos encontremos y el ángulo total, que este ángulo se debe multiplicar por sin o cos, dependiendo de qué posición en el eje de coordenadas queremos obtener
4. Obtenemos las posiciones futuras del enemigo. Para hacerlo, tenemos las coordenadas relativas del enemigo y el ratio de giro, por ende, hacemos cálculos de posibles posiciones futuras que se finalizará cuando la distancia que se encuentra el robot es menor a lo que tarda una bala del cañón a moverse o cuando se encuentre el robot hostil en las esquinas o cerca de la pared.  
5. Una vez obtenemos unas coordenadas futuras del enemigo, se calcula el arcotangente de dichas coordenadas con las nuestras y se coloca el cañón sobre el enemigo. 

### Cerca del muro
Cuando el robot se acerca al muro lo detecta y se aleja de él. La distancia es arbitraria seleccionada por los programadores. Hay algunas veces que no funciona, pero se debe a que se encuentra en una esquina. ¿Qué detecta? Dos muros y el robot, al detectarlos, se intenta alejar lo máximo posible de ellos, de los dos a la vez, quedándose en un bucle el cual provoca que se quede en standby sin saber qué hacer. No nos ha dado tiempo a arreglar del todo este error, pero hemos pensado en hacer lo siguiente:

1. Cuando detecte que se encuentra en las esquinas, realizar un giro de 180 grados respecto a la esquina que se encuentra, priorizando dicho movimiento sobre lo demás. Lo malo de esta solución, es que si el robot hostil nos atrapa en la esquina y justamente se encuentra en nuestra vía de escape nos topamos con él.
2. Generar un detector de muros respecto a un radio, por ende, cuando detecta que hay 2 muros en el mismo radio, sabe que está en una esquina y realiza un giro suave de 45 giros respecto al eje de coordenadas para salir de la zona de peligro. 

![N|SOLID](https://lh3.googleusercontent.com/pw/AM-JKLVTOeFlbeEWIro5DbuEc06ZUSFJXay4S0C0ENtWrtrv7PCIcPWbmOEZGLIHe5Ti6yWVB_pBiQgyBrYEZoGQGWGlkupHoWE4r5i7ejewH7P2uZWF-a34VV5g93xyY-R7eRloc2aglKvdloeNhdO1HlCP=w545-h497-no?authuser=0)
Este punto 2 sería el cual hemos intentado hacer una pequeña implementación, nos da cierta mejora respecto a las esquinas pero no es del todo eficaz debido a otros algoritmos que implementamos, como por ejemplo, el de girar alrededor del enemigo, es decir, si el enemigo se encuentra a una distancia mínima la cual consideramos para realizar dicho giro, el ruestro robot prioriza el giro alrededor del objetivo antes que alejarse de la esquina.


### Importación del modelo

Para cargar el modelo, hay que crear el .class apartir del .java que se encuentra en [/Robocode/src/meurobot](https://github.com/ShadowFurtive/Robocode/blob/main/Robocode/src/meurobot/LaTulaMecanica.java) con nombre LaTulaMecanica.java.
Para crear el .class podemos hacer uso de javac.
```sh
javac LaTulaMecanica.java
```
Una vez tenemos el .class, podemos añadirlo al roboocde mediante las opciones de Preferences>Development Options.


## Notas, posición y comentarios.
	
- COMBATIVITAT:5,975pts /
- DOCU:1,92pts /
- ESTRATEGIA+EXTRES:0,645pts /
- Comentaris: No esperava trobar-me "WC-art" a la portada
- Posició: 4a (El 3r nos sacó un punto 😔 👊 )
- Nota final: 8.5

La portada en cuestión:
![N|SOLID](https://lh3.googleusercontent.com/pw/AM-JKLU6wGs4u2t-rLCd6FokP9qSow6llL8dT9VfycVnzWaGM28eUk--w3a6EZf9VPCLK5JqWary7BbUOsL3fqMk7aft8Maoyl68d3w7MnUN0GSQXr9XPKrkEebvnXgKNszlEn4uYLo5pHWDFV_0VG4Onm3K=w707-h550-no?authuser=0)



