# Network-Topology
Task :
Find the least cost path between all pair of nodes
with following constraints
1. Each node is connected to atleast 3 other nodes
2. Maximum distance between any two nodes is atmost 4 hops.

Solution :
The approach to solving this network topology problem involved two phases. The first phase involved creating an initial solution using the simplest heuristic and then using two different heuristic algorithms and comparing them. 
First random co-ordinates were chosen based on the n value and a minimum spanning tree connecting all the co-ordinates was found. This meant, all the nodes had at least one edge from them. Then iterating from each node, we check if the node has 3 edges or not. If it didn’t we add as many edges (the edge joined the remaining of the nearest node) such that it had a minimum of 3 edges from it. Then another constraint was checked to see if all the nodes from a node could be reached in 4 hops or less (Diameter of the graph should be at most 4). If  the constraint was not satisfied between any two nodes, then an edge between them was added and later both the constraints were satisfied in a greedy approach. This initial solution was later optimised to get the minimum cost and using two different heuristic algorithms.

Considered Heuristic Algorithms
1. HILL CLIMBING ALGORITHM 
2. TABU SEARCH ALGORITHM 
