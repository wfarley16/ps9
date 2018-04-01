package players.player2;

import players.*;
import board.*;
import util.*;
import java.util.*;
import javafx.scene.paint.Color;

public class Player2 implements Player {

  private DBG dbg;
  public Player2() {
    dbg = new DBG(DBG.PLAYERS, "Player2");
  }


  public Line makePlay(Board board, Line oppPlay, long timeRemaining) {
    Board b = board.clone();
    Set<Square> squares = b.squaresWithMarkedSides(3);
    if (squares.isEmpty()) squares = b.squaresWithMarkedSides(0);
    if (squares.isEmpty()) squares = b.squaresWithMarkedSides(1);
    if (!squares.isEmpty()) {
      Square square = squares.iterator().next();
      Set<Line> lines = square.openLines();
      return lines.iterator().next();
    }
    else
      return board.openLines().iterator().next();
  }

    public String teamName() { return "CSCI 1102 Staff"; }
    public Color getSquareColor()  { return Util.PLAYER2_COLOR; }
    public Color getLineColor() { return Util.PLAYER2_LINE_COLOR; }
    public String teamMembers() { return "Brian Kim & Emma Watson"; }
    public int getId()       { return 2; }
    public String toString() { return teamName(); }
}
