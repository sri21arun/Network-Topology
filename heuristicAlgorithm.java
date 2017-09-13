import java.util.Random;
import java.util.Scanner;

public class heuristicAlgorithm {
	// return the node nearest from the set of vertices that have been visited
	static int minKey(int key[], Boolean mstSet[]) {
		int min = Integer.MAX_VALUE, min_index = -1;

		for (int v = 0; v < key.length; v++)
			if (mstSet[v] == false && key[v] < min) {
				min = key[v];
				min_index = v;
			}
		return min_index;
	}

	// minimum spanning tree
	static int[][] aStarSearch(int graph[][]) {
		int parent[] = new int[graph.length];
		int key[] = new int[graph.length];
		Boolean mstSet[] = new Boolean[graph.length];
		for (int i = 0; i < graph.length; i++) {
			key[i] = Integer.MAX_VALUE;
			mstSet[i] = false;
		}
		key[0] = 0;
		parent[0] = -1;
		for (int count = 0; count < graph.length - 1; count++) {
			int u = minKey(key, mstSet);
			mstSet[u] = true;
			for (int v = 0; v < graph.length; v++)
				if (graph[u][v] != 0 && mstSet[v] == false && graph[u][v] < key[v]) {
					parent[v] = u;
					key[v] = graph[u][v];
				}
		}
		int[][] result = populate(parent, graph.length, graph);
		return result;
	}

	// this function is called after finding the minimum spanning tree
	private static int[][] populate(int[] parent, int length, int[][] graph) {
		int[] join = new int[length];
		int[] count = new int[length];
		int[][] con = new int[length][length];
		int u;
		for (int i = 1; i < length; i++) {
			con[parent[i]][i] = 1;
			con[i][parent[i]] = 1;
		}
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				if (con[i][j] == 1)
					count[i]++;
			}
		}

		// first hard constraint - whether all nodes have minimum of 3 edges
		for (int i = 0; i < length; i++) {
			if (count[i] < 3) {
				for (int k = count[i]; k < 3; k++) {
					u = min(graph, con, i);
					con[i][u] = 1;
					con[u][i] = 1;
				}
			}
		}
		System.out.println("----------------");
		// 2nd hard constraint - check if all the nodes can be reached from a
		// particular node in 4 hops or less.
		for (int i = 0; i < length; i++) {
			join = hopPath(i, con);
			for (int j = 0; j < length; j++) {
				if (join[j] > 4) {
					con[i][j] = 1;
					con[j][i] = 1;
				}
			}
		}
		return con;
	}

	// returns the number of hops to all nodes from a particular node
	private static int[] hopPath(int src, int[][] con) {
		int dist[] = new int[con.length];
		Boolean sptSet[] = new Boolean[con.length];
		for (int i = 0; i < con.length; i++) {
			dist[i] = Integer.MAX_VALUE;
			sptSet[i] = false;
		}
		dist[src] = 0;
		for (int count = 0; count < con.length - 1; count++) {
			int u = minDistance(dist, sptSet);
			sptSet[u] = true;
			for (int v = 0; v < con.length; v++) {
				if (!sptSet[v] && con[u][v] != 0 && dist[u] != Integer.MAX_VALUE && dist[u] + con[u][v] < dist[v])
					dist[v] = dist[u] + con[u][v];
			}
		}
		return dist;
	}

	private static int minDistance(int[] dist, Boolean[] sptSet) {
		int min = Integer.MAX_VALUE, min_index = -1;

		for (int v = 0; v < dist.length; v++)
			if (sptSet[v] == false && dist[v] <= min) {
				min = dist[v];
				min_index = v;
			}

		return min_index;
	}

	private static int min(int[][] graph, int[][] con, int i) {
		int v = 0;
		int min = Integer.MAX_VALUE;
		for (int j = 0; j < con.length; j++) {
			if (i != j) {
				if (con[i][j] != 1 && graph[i][j] < min) {
					min = graph[i][j];
					v = j;
				}
			}
		}
		con[i][v] = 1;
		return v;
	}
	
 //First heuristic algorithm where each step taken is better than or equal to the previous step 
	private static int[][] hillClimbing(int[][] answer, int[][] dist) {
		int[] count = new int[answer.length];
		int n = answer.length * (answer.length - 1);
		int[] xy = new int[2];
		int counter = 1;
		while (counter <= n) {
			xy = randF(answer, answer.length - 1); // returns x and y
			if (answer[xy[0]][xy[1]] != 0) {
				for (int i = 0; i < answer.length; i++)
					count[i] = 0;
				answer[xy[0]][xy[1]] = 0;
				answer[xy[1]][xy[0]] = 0;
				boolean isSat = constraintCheck(answer);																// constraint
				if (!isSat) {
					int[][] x = modifyMe(answer, dist, xy);
					int[] count1 = new int[answer.length];
					for (int i = 0; i < answer.length; i++)
						count1[i] = 0;
					boolean isCon = constraintCheck(x);
					if (!isCon) {
						answer[xy[0]][xy[1]] = 1;
						answer[xy[1]][xy[0]] = 1;
					}
				}
				counter++;
			}

		}
		return answer;
	}

	// check if the edge does not exist and its not the same edge currently
	// being taken off and if distance is lesser
	private static int[][] modifyMe(int[][] answer, int[][] dist, int[] xy) {
		for (int i = 0; i < answer.length; i++) {
			if (answer[xy[0]][i] == 0 && dist[xy[0]][i] != dist[xy[0]][xy[1]] && dist[xy[0]][i] < dist[xy[0]][xy[1]] && i!=xy[0]) {
				answer[xy[0]][i] = 1;
				answer[i][xy[0]] = 1;
				break;
			}
		}
		return answer;
	}


	private static int[] randF(int[][] answer, int l) {
		Random rand = new Random();
		int x = rand.nextInt(l) + 1;
		int y = rand.nextInt(l) + 1;
		int[] xy = new int[2];
		xy[0] = x;
		xy[1] = y;
		return xy;
	}
	
	//Local search method to check for immediate neighbours and try to improve the solution from the current one.
	private static int[][] tabu(int[][] ans,int[][] dist) {
		int count=0;
		int k=0;
		int[][][] dummy = new int[500][ans.length][ans.length];
		int[][] overMini = copy(ans);
		while(count<=500){
			ans = heu(ans,dist);	
			boolean see = ifUnique(ans,k,dummy);
			if(see){
				if(findCost(overMini,dist)>findCost(ans,dist)){
					overMini = copy(ans);		
				}	
				k++;
			}
			count++;
		}
		return overMini;
	}

	private static boolean ifUnique(int[][] ans, int m,int[][][] dummy) {
		if(m==0){
			for(int j=0;j<ans.length;j++){
				for(int k=0;k<ans.length;k++){
					dummy[0][j][k]=ans[j][k];
				}
			}
		}
		else{
			int[] count=new int[m];
			for(int i=0;i<m;i++){
				for(int j=0;j<ans.length;j++){
					for(int k=0;k<ans.length;k++){
						if(dummy[i][j][k]==ans[j][k])	
							count[i]++;
					}
				}
			}
			for(int i=0;i<m;i++)
				if(count[i]==ans.length*ans.length)
					return false;
			for(int j=0;j<ans.length;j++){
				for(int k=0;k<ans.length;k++){
					dummy[m][j][k]=ans[j][k];
				}
			}					
		}
		return true;
	}


	private static int[][] heu(int[][] ans,int[][] dist) {
		int n = ans.length;
		int min=Integer.MAX_VALUE;
		int val=0;
		int[][] mini = null;
		for(int i=0;i<n;i++){
			for(int j=i;j<n;j++){
				if(i==j)continue;
				int[][] better = copy(ans);
				if(better[i][j]==1){
					better[i][j]=0;
					better[j][i]=0;
					boolean check = constraintCheck(better);
					if(check){
						val = findCost(better,dist);
						if(min>val){
							min=val;
							mini = copy(better);
						}
					}
				}
				else{
					better[i][j]=1;
					better[j][i]=1;			
					val = findCost(better,dist);
					if(min>val){
						min=val;
						mini = copy(better);
					}
				}
			}
		}

		return mini;
	}	
	private static int[][] copy(int[][] ans) {
		int[][] a = new int[ans.length][ans.length];
		for(int i=0;i<ans.length;i++){
			for(int j=0;j<ans.length;j++)
				a[i][j] = ans[i][j];
		}
		return a;
	}

	private static int findCost(int[][] better,int[][] dist) {
		int sum=0;
		for(int i=0;i<better.length;i++){
			for(int j=i;j<better.length;j++)
				sum+=better[i][j]*dist[i][j];
		}
		return sum;
	}

	private static boolean constraintCheck(int[][] answer1) {
		int[] count = new int[answer1.length];
		for(int i=0;i<answer1.length;i++){
			for(int j=0;j<answer1.length;j++)
				if(answer1[i][j]==1)
					count[i]++;
		}		
		int[] join = new int[answer1.length];
		//check if there are atleast 3 edges from each node
		for (int i = 0; i < answer1.length; i++) {
			if (count[i] < 3) {
				return false;
			}
		}
		// 2nd hard constraint - check if all the nodes can be reached from a
		// particular node in 4 hops or less.
		for (int i = 0; i < answer1.length; i++) {
			join = hopPath(i, answer1);
			for (int j = 0; j < answer1.length; j++) {
				if (join[j] > 4) {
					return false;
				}
			}
		}
		return true;
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int[] x = new int[n];
		int[] y = new int[n];
		int[] visited = new int[n];
		for (int i = 0; i < n; i++)
			visited[i] = 0;
		float[][] matrix = new float[n][n];
		int[][] dist = new int[n][n];
		Random rand = new Random();
		for (int i = 0; i < n; i++) {
			x[i] = rand.nextInt(50) + 1;
			y[i] = rand.nextInt(50) + 1;
		}
		System.out.println("The randomly generated "+n+" co-ordinates are");
		for (int i = 0; i < n; i++)
			System.out.println(x[i]+" "+y[i]);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i == j)
					matrix[i][j] = 0;
				else {
					matrix[i][j] = (float) Math.sqrt(Math.pow(x[i] - x[j], 2) + Math.pow(y[i] - y[j], 2));
				}
				if (matrix[i][j] % 1 >= 0.5)
					dist[i][j] = (int) Math.ceil(matrix[i][j]);
				else
					dist[i][j] = (int) Math.floor(matrix[i][j]);
			}
		}
		//Distance matrix
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(dist[i][j] + " ");
			}
			System.out.println();
		}
		//Printing the initial solution
		int[][] answer = aStarSearch(dist);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++)
				System.out.print(answer[i][j] + " ");
			System.out.println();
		}
		int[][] copyOfAnswer = copy(answer);
		int solution = 0;
		for (int i = 0; i < n; i++) {
			for (int j = i; j < n; j++)
				solution += answer[i][j] * dist[i][j];
		}
		System.out.println("Initial solution " + solution);
		System.out.println();
		
		int[][] answer2 = hillClimbing(answer, dist);
		int count2 = 0;
		int[][] dummy1 = new int[n][n];
		// count2 is the cost of Hill climbing
		for (int i = 0; i < answer2.length; i++) {
			for (int j = 0; j < answer2.length; j++)
				dummy1[i][j] = answer2[i][j] * dist[i][j];
		}
		System.out.println("-----------");
		for(int i=0;i<dummy1.length;i++){
			for(int j=0;j<dummy1.length;j++)
				System.out.print(dummy1[i][j]+" ");
			System.out.println();
		}
		for (int i = 0; i < answer2.length; i++) {
			for (int j = i; j < answer2.length; j++)
				count2 += dummy1[i][j];
		}
		System.out.println("Cost of Heuristic algorithm 1 -> " + count2);
		System.out.println();
		
		int[][] interMediate = tabu(copyOfAnswer,dist);				
		int count3=0;
		int[][] dummy2 = new int[n][n];
		// count2 is the cost of Hill climbing
		for (int i = 0; i < interMediate.length; i++) {
			for (int j = 0; j < interMediate.length; j++)
				dummy2[i][j] = interMediate[i][j] * dist[i][j];
		}
		System.out.println("-----------");
		for(int i=0;i<dummy2.length;i++){
			for(int j=0;j<dummy2.length;j++)
				System.out.print(dummy2[i][j]+" ");
			System.out.println();
		}
		
		for (int i = 0; i < answer2.length; i++) {
			for (int j = i; j < answer2.length; j++)
				count3 += dummy2[i][j];
		}
		System.out.println("Cost of Heuristic algorithm 2 -> " + count3);

		sc.close();
	}
}