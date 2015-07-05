# MOBA Concept

Klassiskt lane system med minions.  
Mitten finns vind kontroll, 2 små i topleft och bottomright hälften så starka.  
Värme vs. Kyla. Olika väder på olika halvor, torn som ger väder. Kan förstöra och göra torn.  
Världen ändras av vädret. Vägar öppnas/stängs. DYNAMISK VÄRLD.  

# Game

+ Character sprites + animation (files already in repository)
+ Dynamic objects
+ Special tile information (different collision shapes, stairs, teleports, etc)
+ Character rolling

# Map editor

+ Toolbox on the side where you can swap between visible layers, pick a tool, see your current tile, etc.
+ Edit one layer at a time.
+ Floodfill
+ Pipette
+ Change brush size
+ Advanced brushes
+ Templates (Pick between presaved and make your own)
+ Copy and paste area (kind of the same as templates)
+ Undo (not super important)
+ Metadata editor (teleports, monsters, objects, triggers, etc)
+ Close world (ask if you are sure you want to close; ask if you want to save first)
+ Collision editor!important

# Multiplayer

+ Client behöver getPlayerDataList som returnerar senaste PlayerDataList som servern skickat. (done?)
+ Client behöver sendPlayerData som skickar spelarens PlayerData till servern. (se punkt 4)
+ Server behöver skicka PlayerDataList till alla kopplade clients (ofta)
+ "1 Thread x Client"-server? bra idè eller?
+  Lägga till en "heartbeat", något sätt för clienten/servern att verifiera att clienten/servern är uppe om inte den får ett svar inom X sekunder... (10-15s?)
