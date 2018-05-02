// file: Player*.java
// authors: Collin Anderson & Steven Wong
// date: May 2, 2014
//
// purpose: A client which makes moves in dots & boxes.


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
    //System.out.println("****" + board.openLines().size());
    
    if (board.gameOver())
      return null;
    //find the squares with 3 marked sides
    Set<Square> marked3SidesSquares = board.squaresWithMarkedSides(3);
    
    //find the squares with 1 marked side
    Set<Square> marked1SideSquares = board.squaresWithMarkedSides(1);
    
    //find the squares with 0 marked side
    Set<Square> notMarkedSquares = board.squaresWithMarkedSides(0);
    
    //get the first square with 3 marked side and mark that open line
    if (!marked3SidesSquares.isEmpty()) {
      Square square = marked3SidesSquares.iterator().next();
      return square.openLines().iterator().next();
    }
    
    //get a line from any of the square that has no marked side
    Line line = chooseRandomLine(notMarkedSquares, board);
    if (line != null)
      return line;
    
    //get a line from any of the square that has 1 marked side
    line = chooseRandomLine(marked1SideSquares, board);
    if (line != null)
      return line;
    
    // No choice but have to pick the first line from a square with 2
    // marked sides already.
    Set<Line> lines = board.openLines();
    List<Line> shuffledLines = new ArrayList<Line>(lines);
    Collections.shuffle(shuffledLines);
    return shuffledLines.get(0);
  }
  
  //check the square based on the given line to see if the square
  //has <2 marked side.  Return true if square has <2 marked side
  private boolean doesSideHaveLessThan2SidesMarked(Line line, Board board) {
    Set<Square> attachedSquaresSet = line.getSquares(board);
    Iterator<Square> squareIterator = attachedSquaresSet.iterator();
    while (squareIterator.hasNext()) {
      if (squareIterator.next().openLines().size() <= 2)
        return false;
    }
    return true;
  }
  
  //given the set of squares, find any open lines of the given square,
  //select the line if it has <2 marked side
  private Line chooseRandomLine(Set<Square> candidates, Board board) {
    List<Square> shuffledCandidates = new ArrayList<Square>(candidates);
    Collections.shuffle(shuffledCandidates);
    for (Square square : shuffledCandidates) {
      Iterator<Line> openLines = square.openLines().iterator();
      while (openLines.hasNext()) {
        Line line = openLines.next();
        if (doesSideHaveLessThan2SidesMarked(line, board))
          return line;
      }
    }
    return null;
  }
  
  public String teamName() { return "P1-East Dillon Lions"; }
  public String teamMembers() { return "Aaron Smith & Mei Hsu"; }
  public Color getSquareColor() { return Util.PLAYER2_COLOR; }
  public Color getLineColor() { return Util.PLAYER2_LINE_COLOR; }
  public int getId() { return 2; }
  public String toString() { return teamName(); }
}
