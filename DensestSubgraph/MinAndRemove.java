package DensestSubgraph;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/*
 * The program takes 4 arguments:
 *  - the location of the input graph, which should be a txt file containing on the first line #nodes #edges and on the following lines the edges, one edge per line.
 *  - a parameter which can be 0,1 or 2 if we want to run NaiveGreedy(0), MinAndRemove(1) or the FastDSLO(2) algorithm. The default is MinAndRemove.
 *  - the number of dense subgraph we want to extract. The default is 10.
 *  - the parameter alpha which sets the overlapping of subgraphs
 */
public class MinAndRemove
{
    public static void main(String[] args) throws IOException
    {
       
        String dataset = "";
      
        int k = 10;
        double alpha = 0.;
        int algo = 1;
        double error = 1.;
        if(args != null && args.length <=4)
        {
            dataset = args[0];
            if(args.length >=2)
                algo = Integer.parseInt(args[1]);
            if(args.length >=3)
               k = Integer.parseInt(args[2]);
            if(args.length >=4)
               alpha = Double.parseDouble(args[3]);
            if(algo == 1)  
               System.out.println("We perform min and remove on dataset "+ dataset + ", for k = " + k + " and alpha = " + alpha);  
            else if (algo == 0)
                System.out.println("We perform NaiveGreedy  on dataset "+ dataset + ", for k = " + k +  ".");
            else
                 System.out.println("We perform FASTDSLO on dataset "+ dataset + ", for k = " + k + " and alpha = " + alpha);   
    
        }
        else
        {
            System.out.println("The program takes 4 arguments: \n" + 
                               "- the location of the input graph, which should be a txt file containing on the first line #nodes #edges and on the following lines the edges, one edge per line.\n"+
                               "- a parameter which can be 0,1 or 2 if we want to run NaiveGreedy(0), MinAndRemove(1) or the FastDSLO(2) algorithm. The default is MinAndRemove.\n"+
			        "- the number of dense subgraphs we want to extract. The default is 10.\n"+
                               "- the parameter alpha which sets the threshold for the overlapping. The default is 0.0 (no overlap)."); 
            return;
        }
    
        MinAndRemove(dataset, k, alpha, algo, error);
        
    }
    

    public static void MinAndRemove(String dataset, int k, double alpha, int algo, double error) throws IOException
    {
        RealNetwork g = new RealNetwork(dataset);
        int initialN = g.n;
        int i = 1;
        double totMaximalDensity = 0.0; 
        double beggining = System.currentTimeMillis();
        double time = 0.0;
       
		while(!g.isDisconnected() && i<=k)
	        {
	            double start = System.currentTimeMillis();
	            System.out.print("Top"+i+" of "+k+" started...");
	            Set current = null;
	            if(algo == 0)
                        {
                         	current = ApproxAlgorithms.approxChar(g);
				alpha = 0.0;
			}
                    else
                    if (algo == 1)
                    {
	                current = ApproxAlgorithms.filtering(g);
                        RealNetwork subgraph = g.inducedSubgraph(current);
	                subgraph = LPAlgorithms.basicLP(subgraph);
	                subgraph = LPAlgorithms.makeMinimal(subgraph);
                        current = subgraph.listNodes();
	                //current.printTopINodes(dataset+alpha, i); 
	            }
                    else
			{
				current = ApproxAlgorithms.approxChar(g);
                                
			}
	            int n = current.size();
	            
			
			    
	 	    double m = removeWeakConnections(g, current, alpha);
	            if(n != 0) 
	                totMaximalDensity += m/n;
	          
	            time = System.currentTimeMillis()-start;
	            System.out.println("DONE!");
	            System.out.println("Time:"+(time/1000)+"secs"+"\t, n = "+n+"\tm="+m+"\td="+(m/n)+"\n");
	            i++;
	            
	        }
	        time = System.currentTimeMillis()-beggining;
	        System.out.println("For alpha = " + alpha + ", TOTAL MAXIMAL DENSITY=" + totMaximalDensity +  ", TOTAL TIME = " + (time/1000));
	    }
    
    public static Double removeWeakConnections(RealNetwork graph,Set<Integer> nodesSubgraph, double alpha)    {
    	/*
    	 * I compute the outer degrees of subraph in the graph g. 
    	 * I than remove upper 1-alpha of subgraph nodes in order to keep Jaccard coeficient restriction  */
    	        
                double m = 0.;
		int i,x,j, maxDeg = 0, minDeg = Integer.MAX_VALUE;
		int initialSize = nodesSubgraph.size();
		int allowed = (int)(initialSize*(alpha));
		HashMap<Integer,HashSet<Integer>> v = new HashMap<Integer,HashSet<Integer>>();
		for(int u:nodesSubgraph)
	    	{
		    	Set<Integer> intersection = new HashSet<Integer>(graph.listNeighbours(u));
                        m += intersection.size();
                        if(intersection.contains(u))
                             m++; // for auto loops, later we need to divide by 2 
		    	intersection.removeAll(nodesSubgraph);
		    	int deg = intersection.size();
                        m -=deg;
		        if(v.get(deg) == null)
		            v.put(deg, new HashSet<Integer>());
		        v.get(deg).add(u);
			if(maxDeg < deg)
			    maxDeg = deg;
			if(minDeg > deg)
		            minDeg = deg;
	
	    	}
		m = m/2;
		i = 0;
		j = maxDeg;
		while(i<allowed)
			{	
				
			  i++;
			  x = v.get(j).iterator().next();
			  v.get(j).remove(x);
			  nodesSubgraph.remove(x);
			  while(v.get(j) == null || v.get(j).isEmpty())
				j--;
	
			}
		
		System.out.println("Initial size of graph = " + initialSize + ", allowed to keep(n*alpha) = " + allowed + ", removed = " + nodesSubgraph.size() + ", minDeg = " + minDeg + ", maxDeg = " + maxDeg);
	graph.removeSubgraph(nodesSubgraph);
    	return m;

    }


   
}
