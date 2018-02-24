import java.util.*;

class State implements Comparable<State>{
	int[] board;
	State parentPt;
	int depth;


	@Override
	public int compareTo(State o) {
		for(int i = 0; i < board.length; i++) {
			if(board[i] == o.board[i])
				continue;
			return board[i] - o.board[i];
		}

		return 0;
	}
	
	public State(int[] arr) {
		this.board = Arrays.copyOf(arr, arr.length);
		this.parentPt = null;
		this.depth = 0;
	}
	
	private int[] copyArray() {
		int[] a = new int[board.length];
		for (int i = 0; i < board.length; i++) {
			a[i] = board[i];
		}
		return a;
	}
	
	public void swap(int[] arr, int row1, int col1, int row2, int col2) {
		int index1 = row1 * 3 + col1;
		int index2 = row2 * 3 + col2;
		
		arr[index1] ^= arr[index2];
		arr[index2] ^= arr[index1];
		arr[index1] ^= arr[index2];
 	}
	
	public State[] getSuccessors() {
		State[] successors = new State[4];
		int[] direction = {-1, 1};
		int index = 0;
		int pos = 0;
		for (int i = 0; i < board.length; i ++) {
			if (board[i] == 0) {
				pos = i;
				break;
			}
		}
		
		int col = pos % 3;
		int row = pos / 3;
		
		for(int i : direction) {
			int[] a1 = copyArray();
			int[] a2 = copyArray();
			
			int curr_col = col + i;
			int curr_row = row + i;
			
			if(curr_col < 0)
				curr_col = 2;
			else
				curr_col %= 3;
			
			if(curr_row < 0)
				curr_row = 2;
			else
				curr_row %= 3;
			
			swap(a1, row, col, row, curr_col);
			swap(a2, row, col, curr_row, col);
			
			successors[index++] = new State(a1);
			successors[index++] = new State(a2);
		}
		
		Arrays.sort(successors);
		
		return successors;
	}

	public void printState(int option) {
		
		if (option == 1) {
			System.out.println(this.getBoard());
		}
		
	}

	public String getBoard() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			builder.append(this.board[i]).append(" ");
		}
		return builder.toString().trim();
	}

	public boolean isGoalState() {
		for (int i = 0; i < 9; i++) {
			if (this.board[i] != (i + 1) % 9)
				return false;
		}
		return true;
	}

	public boolean equals(State src) {
		for (int i = 0; i < 9; i++) {
			if (this.board[i] != src.board[i])
				return false;
		}
		return true;
	}
}

public class Torus {

	public static void main(String args[]) {
		if (args.length < 10) {
			System.out.println("Invalid Input");
			return;
		}
		int flag = Integer.valueOf(args[0]);
		int[] board = new int[9];
		for (int i = 0; i < 9; i++) {
			board[i] = Integer.valueOf(args[i + 1]);
		}
		int option = flag / 100;
		int cutoff = flag % 100;
		if (option == 1) {
			State init = new State(board);
			State[] successors = init.getSuccessors();
			for (State successor : successors) {
				successor.printState(option);
			}
		} else {
			State init = new State(board);
			Stack<State> stack = new Stack<>();
			List<State> prefix = new ArrayList<>();
			int goalChecked = 0;
			int maxStackSize = Integer.MIN_VALUE;

			// needed for Part E
			while (true) {				
				stack.push(init);
				while (!stack.isEmpty()) {
					int count = 0;
					State a = stack.pop();
					prefix.add(a);
					if(a.isGoalState()) {
						break;
					}
					goalChecked++;
					count++;
					if(count>cutoff) {
						for (State successor : prefix) {
							successor.printState(option);
						}
						break;
					}
					State[] successors = a.getSuccessors();
					for (int i = 0; i < successors.length; i ++) {
						stack.push(successors[i]);
					}
					
				}
				
				if (option != 5)
					break;
				
				//TO DO: perform the necessary steps to start a new iteration
			        //       for Part E

			}
		}
	}
}