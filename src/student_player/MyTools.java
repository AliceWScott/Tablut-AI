package student_player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import boardgame.Board;
import coordinates.Coord;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutMove;

public class MyTools {
	
//	https://github.com/rameshvarun/Hnefatafl/blob/master/app/src/main/java/net/varunramesh/hnefatafl/ai/MinimaxStrategy.java
    
	
 	private static boolean outOfTime = false;
 	public static HashMap<TablutBoardState, Integer> stateCache = new HashMap<>();
	
	public static double getSomething() {
        return Math.random();
    }
    
    // time limited iterative depth first search, using alpha-beta pruning.
    public static int TimeLimitedIDS(TablutBoardState boardState, int player_id, long timeLimit) {
    
     	int depth = 1;
     	int best = Integer.MIN_VALUE;
     	long start = System.currentTimeMillis();
     	long end = start + timeLimit;
     	outOfTime = false;
     	
     	// if we already know the score for a certain board arrangement, just return its cached score
     	if(stateCache.containsKey(boardState)) {
     		return stateCache.get(boardState);
     	}
     	
     	while(true) {
     		long current = System.currentTimeMillis();
     		
     		if (end <= current) {
     			break;
     		}
     		int alpha = Integer.MIN_VALUE;
     		int beta = Integer.MAX_VALUE;
     		int moves_to_date = 1;
     		
     		int res = AlphaBeta(boardState, depth, moves_to_date, start, end, alpha, beta, player_id);
     		stateCache.put(boardState, res);
     		
     		
     		if (res > 500) {
     			return res;
     		}
     		
     		if(!outOfTime) {
     			best = res;
     		}
     		
     		depth++;
     	}
     	return best;	
    }
    
        
    public static int AlphaBeta(TablutBoardState bs, int depth, int num_moves, long start, long end, int alpha, int beta, int player_id) {
    		
  		ArrayList<TablutMove> legal_moves = bs.getAllLegalMoves();
  		boolean ourPlay = bs.getTurnNumber() == player_id;
  		
  		int score = scoreState(bs, player_id, num_moves);
  		
  		if((System.currentTimeMillis() - start) >= end) {
  			outOfTime = true;
  		}
  		
  		if(depth == 0 || legal_moves.size()==0 || 500 <= score || score <= -500 ) {
  			return score;
  		}
  		
  		if(ourPlay) {
  			for(TablutMove m: legal_moves) {
  				TablutBoardState child = (TablutBoardState) bs.clone();
  				child.processMove(m);
  				int newSearch = AlphaBeta(child, depth-1, num_moves+1, start, end, alpha, beta, player_id);
  				alpha = Math.max(newSearch, alpha);
  				if (alpha >= beta) {
  					break;
  				}
  			}
  			return alpha;
  		} else {
  			for(TablutMove m: legal_moves) {
  				TablutBoardState child = (TablutBoardState) bs.clone();
  				child.processMove(m);
  				int newSearch = AlphaBeta(child, depth-1, num_moves+1, start, end, alpha, beta, player_id);
  				beta = Math.min(newSearch, beta);
  				if (alpha >= beta) {
  					break;
  				}
  			}
  			return beta;
  		}
  		
    		
    }
    

    public static int scoreState(TablutBoardState bs, int player_id, int num_moves) {
    		
    		int opponent = bs.getOpponent();
	    	if(bs.getWinner() == Board.DRAW) {
	    		return 0;
	    } 
	    // check if in the board state, our last move caused a win
	    // prioritize wins that take a shorter number of total moves to reach
	    else if (bs.getWinner() == player_id) {
	    		return 1000 - num_moves;
	    	// otherwise check if it lead to our opponent winning
	    } else if (bs.getWinner() == opponent) { 
	    		return  -1000;
	    }
    	
    		int score = 0;
    		int numOfOpponentPieces = bs.getNumberPlayerPieces(opponent);
    		int numOfOurPieces = bs.getNumberPlayerPieces(player_id);


      //get pieces surrounding the king
       // if king is not in the middle, weigh surrounding opponent neighbours more
        // since we only need two to sandwich
        Coord kingPos = bs.getKingPosition();
		int distToCorner = Coordinates.distanceToClosestCorner(kingPos);
        List<Coord> kingNeighbours = Coordinates.getNeighbors(kingPos);
        int count_n = 0;
        if(Coordinates.isCenterOrNeighborCenter(kingPos)) {
	        	
        } 
        for(Coord n: kingNeighbours) { 
     		if(bs.isOpponentPieceAt(n) || Coordinates.isCenter(n) || Coordinates.isCorner(n)) {
     			if(Coordinates.isCenterOrNeighborCenter(kingPos)) {
     				count_n++;
     			} else {
     				count_n+=2;
     			}
     		}
    	 	}
        
        Coord KingsClosestCorner = getClosestCorner(kingPos);
        List<Coord> kingToCornerCoords = kingPos.getCoordsBetween(KingsClosestCorner);
        int intercepting = 0;
        for(Coord c: kingToCornerCoords) {
        		if(bs.isOpponentPieceAt(c)) {
        			intercepting++;
        		}
        }

        
        //if we are swedes, a larger distance between king and corner is bad
        if (player_id == TablutBoardState.SWEDE) {
        		score -= distToCorner/2;
        		score -= count_n;
        		score -= intercepting;
        } else {
        		score += distToCorner/2;
        		score += count_n;
        		score += intercepting;
        }
        
        
        
    		return score;
    }
    
    
 // Given a coordinate, returns the closest corner.
    public static Coord getClosestCorner(Coord pos) {
        List<Coord> corners = Coordinates.getCorners();
        int minDistance = Integer.MAX_VALUE;
        Coord closestCorner = null;
        for (Coord corner : corners) {
            int distance = pos.distance(corner);
            if (distance < minDistance) {
                minDistance = distance;
                closestCorner = corner;
            }
        }
        return closestCorner;
    }

}
