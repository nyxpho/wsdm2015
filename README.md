This code implements the algorithms presented in the paper "Finding Subgraphs with Maximum Total Density and Limited Overlap".

In order to run the code you need to install Gurobi, the solver which is used for the linear programming part of the algorithms.
http://www.gurobi.com/products/gurobi-optimizer/gurobi-overview
For researchers working in universities the optimizer can be installed at no charge with the use of an academic licence (see site).


Files:

DensestSubgraph
	The source code. The class MinAndRemove is used as a main class. 


 The program takes 4 arguments:
   - the location of the input graph, which should be a txt file containing the edges, one edge per line. The nodes can be separated by a space bar or the tab key. 
   - the number of dense subgraphs we want to extract. The default is 10.
   - the parameter alpha which sets the threshold for the overlapping of subgraphs. The default is 0.
   - a parameter which can be 0,1 or 2 if we want to run NaiveGreedy(0), MinAndRemove(1) or the FastDSLO(2) algorithm. The default is MinAndRemove.
 
Example: 

java DensestSubgraph/MinAndRemove ../datasets/amazon.graph

java DensestSubgraph/MinAndRemove ../datasets/amazon.graph 2 10 0.1 

The console output will contain statistics about the subgraphs, the number of nodes, edges and density.
In the files path_dataset+"alph"+ alpha + "alg" + algo + step, you can find the nodes of each dense subgraph computed. 


