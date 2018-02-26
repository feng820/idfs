import java.util.*;

class State implements Comparable<State> {
	int[] board;
	State parentPt;
	int depth;

	@Override
	public int compareTo(State o) {
		for (int i = 0; i < board.length; i++) {
			if (board[i] == o.board[i])
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
		int[] direction = { -1, 1 };
		int index = 0;
		int pos = 0;
		for (int i = 0; i < board.length; i++) {
			if (board[i] == 0) {
				pos = i;
				break;
			}
		}

		int col = pos % 3;
		int row = pos / 3;

		for (int i : direction) {
			int[] a1 = copyArray();
			int[] a2 = copyArray();

			int curr_col = col + i;
			int curr_row = row + i;

			if (curr_col < 0)
				curr_col = 2;
			else
				curr_col %= 3;

			if (curr_row < 0)
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
		} else if (option == 2) {
			System.out.println(this.getBoard());
		} else if(option == 3) {
			System.out.println(this.getBoard() + " parent " + parentPt.getBoard());
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
	public static boolean idfs(State start, int cutoff, int option) {
		int[] fakeParentArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		start.parentPt = new State(fakeParentArray);

		ArrayList<State> prefix = new ArrayList<State>();
		prefix.add(start.parentPt);

		Stack<State> stack = new Stack<State>();

		stack.push(start);
		while (!stack.isEmpty()) {
			// pop out parent
			State currentState = stack.pop();

			// find out parent
			int parentIndex = 0;
			for (State parent : prefix) {
				if (parent.equals(currentState.parentPt))
					break;
				parentIndex++;
			}

			for (int i = parentIndex + 1; i < prefix.size(); i++)
				prefix.remove(i);

			prefix.add(currentState);
			//handle 4 in a different way
			if(option != 4) {
				currentState.printState(option);
			}
			
			if(option == 4 && currentState.depth == cutoff) {
				for(State parent : prefix) {
					if(parent.equals(start.parentPt))
						continue;
					
					parent.printState(2);
				}
				return false;
			}
			
			// goal check
			if (currentState.isGoalState())
				return true;

			// push successors if is not in the cut off level
			if (currentState.depth < cutoff) {
				State[] successors = currentState.getSuccessors();
				pushSuccessor: for (State successor : successors) {
					//set parent
					successor.parentPt = currentState;
					//increase depth
					successor.depth = currentState.depth + 1;

					// prevent circle
					for (State s : prefix)
						if (s.compareTo(successor) == 0)
							continue pushSuccessor;

					stack.push(successor);
				}

			}
		}

		return false;
	}

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
			int maxStackSize = Integer.MIN_VALUE;
			int depth = 0;
			int goalCheck = 0;
			int[] fakeParentArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			State start = new State(board);
			start.parentPt = new State(fakeParentArray);

			if(option != 5)
				idfs(new State(board), cutoff, option);
			// needed for Part E
			while (true) {
				if (option != 5)
					break;

				ArrayList<State> prefix = new ArrayList<State>();
				prefix.add(start.parentPt);

				Stack<State> stack = new Stack<State>();

				stack.push(start);
				while (!stack.isEmpty()) {
					// pop out parent
					State currentState = stack.pop();

					// find out parent
					int parentIndex = 0;
					for (State parent : prefix) {
						if (parent.equals(currentState.parentPt))
							break;
						parentIndex++;
					}
					
					ArrayList<State> tmp_prefix = new ArrayList<State>(); 
					for(int i = 0; i < parentIndex + 1; i++) {
						tmp_prefix.add(prefix.get(i));
					}
					
					prefix = tmp_prefix;

					prefix.add(currentState);
					//handle 4 in a different way
					if(option != 4) {
						currentState.printState(option);
					}
					
					if(option == 4 && currentState.depth == depth) {
						for(State parent : prefix) {
							if(parent.equals(start.parentPt))
								continue;
							
							parent.printState(2);
						}
					}
					
					// goal check
					goalCheck++;
					if (currentState.isGoalState()) {
						ArrayList<State> solution = new ArrayList<State>();
						while(currentState != start.parentPt) {
							solution.add(currentState);
							currentState = currentState.parentPt;
						}
						for(int i = solution.size() - 1; i >= 0; i--) {
							System.out.println(solution.get(i).getBoard());
						}
						System.out.println("Goal-check " + goalCheck);
						System.out.println("Max-stack-size " + maxStackSize);
						return;
					}

					// push successors if is not in the cut off level
					if (currentState.depth < depth) {
						State[] successors = currentState.getSuccessors();
						pushSuccessor: for (State successor : successors) {
							//set parent
							successor.parentPt = currentState;
							//increase depth
							successor.depth = currentState.depth + 1;

							// prevent circle
							for (State s : prefix)
								if (s.compareTo(successor) == 0)
									continue pushSuccessor;

							stack.push(successor);
							if(maxStackSize < stack.size())
								maxStackSize = stack.size();
						}

					}
				}

				depth++;
			}

		}
	}
}
