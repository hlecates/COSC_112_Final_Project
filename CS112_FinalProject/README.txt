To compile and run:
javac *.java
java Explorer

To use:
Control the player using ‘w’,‘a’,‘s’,‘d’ movement inputs. ‘x’ to stop moving. ‘e’ to dash. ‘c’ to shoot a projectile. 
And when dueling a friend, ‘q’ uses a powerup.

To choose which mode to play, move the player sprite to the desired mode, indicated by a button (Levels, or Arena)
In the Level mode, indicated by the “Levels” button, the player will progres through a series of levels. 
To progress from level to level the player must complete the task, and collect the key, Then move out of 
the bounds of the world. 
Level 1, contains several enemies which the player will defeat using their fired 
projectiles. 
Level 2, contains a randomly generated maze which the player must navigate from a randomly assigned 
position to a randomly generated key. Then move outside the bounds of the world. 
Level 3, has several nodes which 
the player must toggle using their projectiles. The goal is to get the percentage displayed at the top of the screen 
to 99.99. Some of the nodes will decrement and others will increment the percentage. 
Level 4 is the boss level. Here the player will have to defeat a boss, while avoiding the incoming projectiles. The boss’s phase is indicated 
by a moving block at the stop of the screen, and the boss’s health is indicated by the rectangle at the top of the 
screen. 
To skip levels the user can press ‘p’ which increments the level without being required to play it.

In the dueling mode, indicated by the “Arena” button, you will be joined by a secondary player. Here the second player 
will be controlled with ‘i’,‘j’,‘k’,‘l’ movement inputs. ‘m’ to stop moving. ‘u’ to dash. ‘n’ to shoot a projectile. 
And when dueling a friend, ‘o’ to use a powerup. In this mode there are randomly generate power ups which grant the 
following abilities: the bullet allows the player to fire projectiles much faster, the shield make the player 
immune to damage, and the heart give the player an additional 10 health. 