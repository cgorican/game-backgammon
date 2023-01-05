# Backgammon

[Backgammon](https://en.wikipedia.org/wiki/Backgammon) is a board game for two players.<br/>
Each player has fifteen pieces and moves them around twenty-four triangles according to the roll of two dice.<br/>
The goal of the game is to get all fifteen pieces off the playing surface as soon as possible, just like in the game you don't get angry.<br/>
The game is fast and dynamic, but also requires some skill.

![backgammon](img/backgammon.png)

<b>Starting layout:</b> (for each player)<br/>
- 2 on the 24th point
- 5 on the 13th point
- 3 on the 8th point
- 5 on the 6th point

## Dynamics
- 2 players
- Initial state (image)
- Starting roll (each player rolls 1 die) - the winner takes into account the value of both dice to start the game
- If the starting throw is tied, the value of the game is doubled and the throw is repeated
- Moving pieces to the house then off the board (game)
- Bright figures move clockwise
- Dark figures move counter-clockwise
- Moves can be repeated until the dice are returned to the mixing bowl
- Duplication cube (starting position 64 = x1)
- Winner gets (1 * doubling factor) points
- The winner gets 2 points if the opponent failed to remove any pieces and the doubling factor equals 1

## Mechanics
- Start (image)
- End (player succeeds in getting all his pieces into his house and then out of it)
- Roll the dice
- If the player rolls two identical numbers, his roll is doubled (3+3=6 => (3+3)*2=12)
- Movement of pieces according to the value of the meta (the value of the meta can be divided -> 8 | 5 + 3)
- Pieces on the field can be moved if there are no pieces on the barrier
- If we cannot move the pieces from the barrier, we must surrender the move
- The piece from the barrier continues on the 24th square
- If there is 1 (opponent's) figure on the field, it can be knocked down (the figure is moved to the barrier)
- If there are at least 2 (opponent's) figures on the field, we cannot move to that field
- If the player refuses to double, the opponent gets 1 point
- The player who doubles the value of the game becomes the owner of the doubling cube
- A player can increase a doubling if it has not been doubled by anyone before or if he owns the (doubling) die

## Elements
- 2 houses (the house of each player is on his right in the lower quadrant)
- 4 dice (each player has 2)
- 15 white pieces
- 15 black pieces
- 4 quadrants
- 24 (4 quadrants * 6 fields) fields or of triangles (<i>points</i>)
- barrier (<i>barrier / bar<i/>)
