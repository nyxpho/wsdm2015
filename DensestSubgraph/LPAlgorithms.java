package DensestSubgraph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import gurobi.*;

public class LPAlgorithms {

	   private static Set<Integer> tryEnhance (int u, int n, double m, RealNetwork g)
	   {
		  Map<Integer, Map<Integer, Double>> matrix = g.cloneGraph();
		  HashSet<Integer> minimalG = new HashSet <Integer> ();
	                minimalG.addAll(matrix.keySet());
		

	        try
	        {
	                GRBEnv    env   = new GRBEnv("minchar.log");
	                GRBModel  model = new GRBModel(env);

	                model.getEnv().set(GRB.IntParam.OutputFlag, 0);
	                model.getEnv().set(GRB.IntParam.Method, 1);
	                /* get optimal density,  pick one node arbitrary,  check minimality, include weighted graphs */
	                Map<Integer, GRBVar> nodes = new HashMap<Integer, GRBVar>();
	                Map<String, GRBVar> edges = new HashMap<String, GRBVar>();
	                int nConstr = 0;

	                /* add variables - continuous, between 0 and 1 */
	                for(int x:matrix.keySet())
	                {
	                        GRBVar xVar = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "y" + String.valueOf(x));
	                        nodes.put(x, xVar);
	                        Map<Integer, Double> neigh = matrix.get(x);
	                        for(int y:neigh.keySet())
	                        {
	                                if( x < y)
	                                {
	                                        String edge = "x" + String.valueOf(x) + "x" + String.valueOf(y);
	                                        GRBVar xEdge = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, edge);
	                                        edges.put(edge, xEdge);
	                                }
	                        }
	                }
	                model.update();
	                GRBLinExpr nodeExp = new GRBLinExpr();
	                GRBLinExpr edgeExp = new GRBLinExpr();

	                /* add constraints*/
	                for(int x:matrix.keySet())
	                {
	                   Map<Integer, Double> neigh = matrix.get(x);
	                   nodeExp.addTerm(1.0, nodes.get(x));
	                   for(int y:neigh.keySet())
	                   {
	                        if( x < y )
	                        {
	                                String edge = "x" + String.valueOf(x) + "x" + String.valueOf(y);
	                                nConstr ++;
	                                GRBVar edgeVar = edges.get(edge);
	                                GRBLinExpr expr = new GRBLinExpr();
	                                expr.addTerm(1.0, edgeVar); expr.addTerm(-1.0, nodes.get(y));
	                                model.addConstr(expr, GRB.LESS_EQUAL, 0.0, "c" + String.valueOf(nConstr));

	                                nConstr ++;
	                                expr.remove(nodes.get(y));
	                                expr.addTerm(-1.0, nodes.get(x));
	                                model.addConstr(expr, GRB.LESS_EQUAL, 0.0, "c" + String.valueOf(nConstr));

	                                edgeExp.addTerm(n*matrix.get(x).get(y), edgeVar);
	                        }

	                   }
	                }
	                nConstr ++;
	                model.addConstr(nodeExp, GRB.LESS_EQUAL, 1.0, "c" + String.valueOf(nConstr));

	                nConstr ++;
	                model.addConstr(edgeExp, GRB.GREATER_EQUAL, m, "c" + String.valueOf(nConstr));

	                int yArbitrary = u;
	                GRBLinExpr expr = new GRBLinExpr();
	                expr.addTerm(1.0, nodes.get(yArbitrary) );
	                model.setObjective(expr, GRB.MAXIMIZE);
	                model.update();
	                model.optimize();
	                System.out.println("Arbitrary node= " + yArbitrary);

	                HashSet <Integer> enhanceSol = new HashSet <Integer>();
	                int status = model.get(GRB.IntAttr.Status);
	                if (status != GRB.Status.INF_OR_UNBD &&
	                        status != GRB.Status.INFEASIBLE &&
	                        status != GRB.Status.UNBOUNDED )
	                        {
	                                for(int x:matrix.keySet())
	                                {       
	                                        if (nodes.get(x).get(GRB.DoubleAttr.X) != 0 )
	                                        	enhanceSol.add(x);
	                                }
	                        }


	                
	                if ( (minimalG.size() > enhanceSol.size()) && enhanceSol.size() > 0 )
	                        {
	                        minimalG = enhanceSol;
	                        System.out.println("Found a smaller subgraph!!! With enhancement. ");
	               
	                        }
	             
	              model.dispose();
	              env.dispose();
	        }
	                 catch (GRBException e) {
	                
	              System.out.println("tryEnhance: Error code: " + e.getErrorCode() + ". " +
	                         e.getMessage());
	        }

	        return minimalG;
	   }
	   
	   private static Set<Integer> tryRemove (int u,int n, double m, RealNetwork g)
	   {
		   Map<Integer, Map<Integer, Double>> matrix = g.cloneGraph();
		   HashSet <Integer> minimalG = new HashSet <Integer> ();
           	   minimalG.addAll(matrix.keySet());

		   try
		   {
		           GRBEnv    env   = new GRBEnv("minchar.log");
		           GRBModel  model = new GRBModel(env);
		
		           model.getEnv().set(GRB.IntParam.OutputFlag, 0);
		           model.getEnv().set(GRB.IntParam.Method, 1);
		           /* get optimal density,  pick one node arbitrary,  check minimality, include weighted graphs */
		           
		           
		           Map<Integer, GRBVar> nodes = new HashMap<Integer, GRBVar>();
		           Map<String, GRBVar> edges = new HashMap<String, GRBVar>();
		           int nConstr = 0;
		
		           /* add variables - continuous, between 0 and 1 */
		           for(int x:matrix.keySet())
		           {
		                   GRBVar xVar = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "y" + String.valueOf(x));
		                   nodes.put(x, xVar);
		                   Map<Integer, Double> neigh = matrix.get(x);
		                   for(int y:neigh.keySet())
		                   {
		                           if( x <= y)
		                           {
		                                   String edge = "x" + String.valueOf(x) + "x" + String.valueOf(y);
		                                   GRBVar xEdge = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, edge);
		                                   edges.put(edge, xEdge);
		                           }
		                   }
		           }
		           model.update();
		           GRBLinExpr nodeExp = new GRBLinExpr();
		           GRBLinExpr edgeExp = new GRBLinExpr();
		
		           /* add constraints*/
		           for(int x:matrix.keySet())
		           {
		              Map<Integer, Double> neigh = matrix.get(x);
		              nodeExp.addTerm(1.0, nodes.get(x));
		              for(int y:neigh.keySet())
		              {
		                   if( x <= y )
		                   {
		                           String edge = "x" + String.valueOf(x) + "x" + String.valueOf(y);
		                           nConstr ++;
		                           GRBVar edgeVar = edges.get(edge);
		                           GRBLinExpr expr = new GRBLinExpr();
		                           expr.addTerm(1.0, edgeVar); expr.addTerm(-1.0, nodes.get(y));
		                           model.addConstr(expr, GRB.LESS_EQUAL, 0.0, "c" + String.valueOf(nConstr));
		
		                           nConstr ++;
		                           expr.remove(nodes.get(y));
		                           expr.addTerm(-1.0, nodes.get(x));
		                           model.addConstr(expr, GRB.LESS_EQUAL, 0.0, "c" + String.valueOf(nConstr));
		
		                           edgeExp.addTerm(n*matrix.get(x).get(y), edgeVar);
		                   }
		
		              }
		           }
		           nConstr ++;
		           model.addConstr(nodeExp, GRB.LESS_EQUAL, 1.0, "c" + String.valueOf(nConstr));
		
		           nConstr ++;
		           model.addConstr(edgeExp, GRB.GREATER_EQUAL, m, "c" + String.valueOf(nConstr));
		
		           int yArbitrary = u;
		           GRBLinExpr expr = new GRBLinExpr();
		           expr.addTerm(1.0, nodes.get(yArbitrary) );
		          
		           nConstr ++;
		           model.addConstr(expr, GRB.EQUAL, 0, "c" + String.valueOf(nConstr));
		
		           expr.remove(nodes.get(yArbitrary));
		           expr.addTerm(0.0, nodes.get(yArbitrary));
		           model.setObjective(expr, GRB.MAXIMIZE);
		           model.update();
		           model.optimize();
		           HashSet <Integer> removeSol = new HashSet <Integer>();
		           int status = model.get(GRB.IntAttr.Status);
		           if (status != GRB.Status.INF_OR_UNBD &&
		                   status != GRB.Status.INFEASIBLE &&
		                   status != GRB.Status.UNBOUNDED )
		                   {
		                           for(int x:matrix.keySet())
		                           {
		                                   if (nodes.get(x).get(GRB.DoubleAttr.X) != 0 )
		                                           removeSol.add(x);
		                           }
		
		                   }
		           
		           if ( minimalG.size() > removeSol.size() && removeSol.size() > 0)
		                   {
		                   minimalG = removeSol;
		                   System.out.println("Found a smaller subgraph!!! With removal.");
		                   }
		           else 
		        	   minimalG.clear();
		         model.dispose();
		         env.dispose();
		}
            catch (GRBException e) {
            	minimalG.clear();
         System.out.println("tryRemove: Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
   }

   return minimalG;
   
	}
	
   public static RealNetwork makeMinimal(RealNetwork g)
	   {
                  int nn = g.n;
                  double mm = g.m;
		  if(g.isEmpty())
		  	return g;
		  while(true)
		  {
		
			  int u = g.getRandomNode();
			  Set firstSol = tryRemove(u,nn,mm,g);
			  Set secondSol = tryEnhance(u,nn,mm,g);
			  if (firstSol.size() == 0)
				  return g.inducedSubgraph(secondSol);
			  else
				  if(firstSol.size() < secondSol.size())
					  g = g.inducedSubgraph(firstSol);
				  else
					  g = g.inducedSubgraph(secondSol);
		  }
	   }
	   
   
   public static RealNetwork basicLP(RealNetwork g)
    {
	   Map<Integer, Map<Integer, Double>> matrix = g.g;
	   HashSet <Integer> solution = new HashSet <Integer>();
	   RealNetwork densest = null;

        try
        {
                GRBEnv    env   = new GRBEnv("basiclp.log");
                GRBModel  model = new GRBModel(env);

                int n = g.n;
		double m = g.m;
		System.out.println("basicLP: Initial size n = " + n + ", m = " + m);
                Map<Integer, GRBVar> nodes = new HashMap<Integer, GRBVar>();
                Map<String, GRBVar> edges = new HashMap<String, GRBVar>();
                int nConstr = 0;
                GRBVar yVar= null, xEdge = null, edgeVar = null;
                String edge = null;
                model.getEnv().set(GRB.IntParam.OutputFlag, 0);
                model.getEnv().set(GRB.IntParam.Method, 1);

                for(int y:matrix.keySet())
                {
                        yVar = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "y" + String.valueOf(y));
                        nodes.put(y, yVar);
                        Map<Integer, Double> neigh = matrix.get(y);
                        for(int x:neigh.keySet())
                        {
                                if( x <= y)
                                {
                                        edge = "x" + String.valueOf(x) + "x" + String.valueOf(y);
                                        xEdge = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, edge);
                                        edges.put(edge, xEdge);
                                }
                        }
                }
                model.update();
                GRBLinExpr nodeExp = new GRBLinExpr();
                GRBLinExpr edgeExp = new GRBLinExpr();

                for(int x:matrix.keySet())
                {
                   Map<Integer, Double> neigh = matrix.get(x);
                   nodeExp.addTerm(1.0, nodes.get(x));
                   for(int y:neigh.keySet())
                   {
                        if( x <= y )
                        {
                                edge = "x" + String.valueOf(x) + "x" + String.valueOf(y);
                                nConstr ++;
                                edgeVar = edges.get(edge);
                                GRBLinExpr expr = new GRBLinExpr();
                                expr.addTerm(1.0, edgeVar); expr.addTerm(-1.0, nodes.get(y));
                                model.addConstr(expr, GRB.LESS_EQUAL, 0.0, "c" + String.valueOf(nConstr));

                                nConstr ++;
                                expr.remove(nodes.get(y));
                                expr.addTerm(-1.0, nodes.get(x));
                                model.addConstr(expr, GRB.LESS_EQUAL, 0.0, "c" + String.valueOf(nConstr));

                                edgeExp.addTerm(matrix.get(x).get(y), edgeVar);
                        }

                   }
                }

                nConstr ++;
                model.addConstr(nodeExp, GRB.LESS_EQUAL, 1.0, "c" + String.valueOf(nConstr));

                model.setObjective(edgeExp, GRB.MAXIMIZE);
                model.update();
                model.write("lpcharikar.lp");

                model.optimize();

                int status = model.get(GRB.IntAttr.Status);
                if (status != GRB.Status.INF_OR_UNBD &&
                        status != GRB.Status.INFEASIBLE &&
                        status != GRB.Status.UNBOUNDED )
                        {
				
                                for(int x:matrix.keySet())
                                {
                                        if (nodes.get(x).get(GRB.DoubleAttr.X) != 0 )
                                                solution.add(x);
                                } 
                                
              			System.out.println("The objective value is "+ model.get(GRB.DoubleAttr.ObjVal));
              		
                        }

              model.dispose();
              env.dispose();
        
	}
              catch (GRBException e) {
              System.out.println("basicLP: Error code: " + e.getErrorCode() + ". " +
                         e.getMessage());
        }
	
	densest = g.inducedSubgraph(solution);      
	System.out.println("basicLP: Densest size n = " + densest.n + ", m = " + densest.m);
	return densest;

    }


}

