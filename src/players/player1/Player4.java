// file: Player*.java
// authors: Will Farley & Liz Hanley
// date: April 29, 2018
//

package players.player1;

import players.*;
import board.*;
import util.*;

import java.util.*;
import javafx.scene.paint.Color;

public class Player4 implements Player {
  
  private DBG dbg;
  private final int width = 6; //number of possible moves to get
  private final int depth = 6; //number of moves to go into game tree logic
  private final int mid = 100; //threshold for starting late game logic
  private final long minTime = 3; //minimum milliseconds left per move for us to switch to quick moves
  private int[] order = {3,1,0,2}; //order of priority in moves
  //private boolean first; //did we go first
  
  public Player1() {
    dbg = new DBG(DBG.PLAYERS, "Player1");
  }
  
  private void printBoard(Square[][] board) {
    for (int row = 0; row < Util.N; row++) {
      for (int col = 0; col < Util.N; col++) {
        System.out.println(board[row][col].toString());
      }
    }
  }
  
  // Same function from the board class, but this accepts a 2D Array of Squares
  // instead of working on the instance of the class
  private Set<Line> openLines(Square[][] board) {
    Set<Line> lines = new HashSet<Line>();
    for (int row = 0; row < Util.N; row++) {
      for (int col = 0; col < Util.N; col++) {
        Set<Line> sqLines = board[row][col].openLines();
        lines.addAll(sqLines);
      }
    }
    return lines;
  }
  
  // Same function from the board class, but this accepts a 2D Array of Squares
  // instead of working on the instance of the class
  public Set<Square> squaresWithMarkedSides(Square[][] board, int n) {
    
    Set<Square> theSet = new HashSet<Square>();
    for(int row = 0; row < Util.N; row++)
      for(int col = 0; col < Util.N; col++) {
      Square square = board[row][col];
      if (square.hasNMarkedSides(n))
        theSet.add(square);
    }
    return theSet;
  }
  
  // Initializes an empty set to pass in
  public Set<Line> getPossibleMoves(Square[][] board) {
    Set<Line> empty = new HashSet<Line>();
    return getPossibleMoves(board, empty, 0);
  }
  
  // Gets a set of 
  private Set<Line> getPossibleMoves(Square[][] board, Set<Line> moves, int sides) {
    if (moves.size() >= width || sides > 3) return moves;
    else {
      Set<Square> squares = squaresWithMarkedSides(board, order[sides]);
      for (Square square : squares) {
        for (Line line : square.openLines()) {
          if (moves.size() < width)
            moves.add(line);
        }
      }
      return getPossibleMoves(board, moves, sides+1);
    }
  }
  
  private Line bestMove(Square[][] board, Set<Line> possibleLines) {
    
    Line best = possibleLines.iterator().next();
    int score = 0;
    
    for (Line possibleLine : possibleLines) {
      int result = doLotsOfTests(board, possibleLine, depth);
      //System.out.println("Line: " + possibleLine.toString() + " : " + result);
      if (result > score) {
        best = possibleLine;
        score = result;
      }
    }
    return best;
  }
  
  private int doLotsOfTests(Square[][] board, Line line, int swaps) {
    
    int score = 0;
    boolean turn = true;
    
    Line next = line;
    Stack<Line> moves = new Stack<Line>();
    
    //System.out.println("Takings turns");
    while (swaps > 0) {
      moves.push(next);
      int row = next.getRow();
      int col = next.getCol();
      Side side = next.getSide();
      board[row][col].setSide(side, true);
      // if it completed a square
      if (board[row][col].hasNMarkedSides(4)) {
        // score for team that had the turn
        if (turn) {
          //System.out.println("It's our turn. We just scored");
          score += 10;
        }
        else {
          //System.out.println("It's their turn. They just scored");
          score -= 10;
        }
      }
      // if it didn't complete a square
      else {
        // invert turn
        //System.out.println("Turn switching");
        turn = !turn;
        // decrement swaps
        swaps--;
      }
      // get next move
      if (!openLines(board).isEmpty()) {
        //next = bestMove(board, getPossibleMoves(board));
        
        // if there's anything that can be improved it's this
        next = pickLine(board);
      }
      // if we're out of lines to mark, break the loop
      else break;
    }
    
    // undo moves we made
    while (!moves.isEmpty()) {
      Line move = moves.pop();
      board[move.getRow()][move.getCol()].setSide(move.getSide(), false);
    }
    
    return score;
  }
  
  //Simple logic, quick pick
  private Line pickLine(Square[][] board) {
    
    Set<Square> squares = squaresWithMarkedSides(board, 3);
    if (!squares.isEmpty())
      return squares.iterator().next().openLines().iterator().next();
    
    squares = squaresWithMarkedSides(board, 0);
    Line random = chooseRandomLine(squares, board);
    if (random != null) return random;
    
    squares = squaresWithMarkedSides(board, 1);
    random = chooseRandomLine(squares, board);
    if (random != null) return random;
    
    squares = squaresWithMarkedSides(board, 2);
    return squares.iterator().next().openLines().iterator().next();
  }
  
  // Function that gets called for us to make our move
  public Line makePlay(Board board, Line oppPlay, long timeRemaining) {
    if (board.gameOver())
      return null;
    
    int linesRemaining = board.openLines().size();
    
    Square[][] copy = board.toArray();
    
    if (linesRemaining >= mid || (timeRemaining/linesRemaining) < minTime) {
      // In the early game (before a certain number of lines are filled)
      // Going to make a quick play with simple logic to save time
      //try {
        return pickLine(copy);
      //} catch (Exception e) {
        //System.out.println("Error: " + e.toString());
        //printBoard(copy);
        //return pickLine(copy);
      //}
    } else {
      //try {
        // Late Game Logic
        Set<Line> poss = getPossibleMoves(copy);
        return bestMove(copy, poss);
      //} catch (Exception e) {
        // Debug
        //System.out.println("Error: " + e.toString());
        //printBoard(copy);
        // Early Game Logic
        //return pickLine(copy);
      //}
    }
  }
  
  // Returns set of squares that a line is attached to
  public Set<Square> getSquares(Square[][] board, Line line) {
    Set<Square> answer = new HashSet<Square>();
    
    int row = line.getRow();
    int col = line.getCol();
    Side side = line.getSide();
    
    answer.add(board[row][col]);
    
    if(side == Side.NORTH && row > 0)
      answer.add(board[row - 1][col]);
    if(side == Side.WEST && col > 0)
      answer.add(board[row][col - 1]);
    if(side == Side.SOUTH && row < Util.N - 1)
      answer.add(board[row + 1][col]);
    if(side == Side.EAST && col < Util.N - 1)
      answer.add(board[row][col + 1]);
    
    return answer;
  }
  
//check the square based on the given line to see if the square
//has <2 marked side.  Return true if square has <2 marked side
  private boolean doesSquareHaveLessThan2SidesMarked(Line line, Square[][] board) {
    Set<Square> attachedSquaresSet = getSquares(board, line);
    Iterator<Square> squareIterator = attachedSquaresSet.iterator();
    while (squareIterator.hasNext()) {
      if (squareIterator.next().openLines().size() <= 2)
        return false;
    }
    return true;
  }
  
//given the set of squares, find any open lines of the given square,
//select the line if it has <2 marked side
  private Line chooseRandomLine(Set<Square> candidates, Square[][] board) {
    //List<Square> shuffledCandidates = new ArrayList<Square>(candidates);
    //Collections.shuffle(shuffledCandidates);
    //return shuffledCandidates.iterator().next().openLines().iterator().next();
    
    List<Square> shuffledCandidates = new ArrayList<Square>(candidates);
    Collections.shuffle(shuffledCandidates);
    for (Square square : shuffledCandidates) {
      Iterator<Line> openLines = square.openLines().iterator();
      while (openLines.hasNext()) {
        Line line = openLines.next();
        if (doesSquareHaveLessThan2SidesMarked(line, board))
          return line;
      }
    }
    return null;
  }
  
  public String teamName() { return "The Kit Kats"; }
  public String teamMembers() { return "Will Farley & Liz Hanley"; }
  public Color getSquareColor() { return Util.PLAYER1_COLOR; }
  public Color getLineColor() { return Util.PLAYER1_LINE_COLOR; }
  public int getId() { return 1; }
  public String toString() { return teamName(); }
}
