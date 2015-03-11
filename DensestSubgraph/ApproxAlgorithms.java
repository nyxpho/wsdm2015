package DensestSubgraph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.ListIterator;

public class ApproxAlgorithms {


	/* this function returns the subgraph after the filtering 
	of the nodes with degree smaller than the 2 approx density */
	 public static Set filtering(RealNetwork g)
        {
	 Map<Integer, Map<Integer, Double>> matrix = g.cloneGraph();
         int n = g.n;
         double m = g.m;
         LinkedList<Integer> nodesRemoved = new LinkedList<Integer>();
         LinkedList<Double> densities = new LinkedList<Double>();
         LinkedList<Integer> minDegrees = new LinkedList<Integer>();
         densities.add(m/n);
        
         Map<Integer,Integer> currentDegrees = new HashMap<Integer,Integer>();
         int maxDeg = 0, deg = 0;
         for(int u:matrix.keySet())
         {
                 deg = 0;
                 for(int w : matrix.get(u).keySet())
                         deg += matrix.get(u).get(w);
                 if(maxDeg < deg)
                         maxDeg = deg;
                 currentDegrees.put(u, deg);
         }

        System.out.println("Starting filtering on graph with n = " + n + ", and m = " + m + ", maxDegree = " + maxDeg);
        HashMap<Integer, LinkedList<Integer>> v = new HashMap<Integer, LinkedList<Integer>>();
        
        for(int u:matrix.keySet())
        {
            deg = currentDegrees.get(u);
            if(v.get(deg) == null)
                v.put(deg, new LinkedList<Integer>());
            v.get(deg).push(u);
        }
        int j = 0;
        while(v.get(j) == null)
        {
            j++;
        }
 
         
         int r = 5;
         int p = (n/(100/r)>0)?(n/(100/r)):1;
         int stop = n;
         for(int i=1; i<=stop; i++)
	         {
	             
	             if(i%p==0)
	             {
	                 int c = i/p;
	                 System.out.print(c*r+"%, ");
	             }
	         	 int x = -1;
		     x = v.get(j).pop();
		     minDegrees.add(j);
	             Map<Integer,Double> neighbors_x = matrix.get(x);
		     int maxDecrease = j;
	             for(int y:neighbors_x.keySet())
				     {
				
				         int deg_y = currentDegrees.get(y);
				         if(deg_y>0 && x != y)
				         {
			                     int w = neighbors_x.get(y).intValue();
				             
				             if(v.get(deg_y - w) == null)
				                v.put(deg_y - w, new LinkedList<Integer>());
				             if(deg_y - w < maxDecrease)
						maxDecrease = deg_y - w;    
				             v.get(deg_y - w).push(y);
				             currentDegrees.put(y, deg_y - w);
				         }
				     }
                             	
			     nodesRemoved.add(x);
			     currentDegrees.put(x, 0);
			     m -= j;
			     n -= 1;
                             double dens = 0;
                             if(n>0)
                                dens = m/n;
			     densities.add(dens);
                             
                              if(maxDecrease != j)
                                  j = maxDecrease;
                                else
                                {
                                        while(j<maxDeg && (v.get(j) == null || v.get(j).size() == 0))
                                                {
                                                        j++;
                                                }
                                }
                              
                              while(v.get(j).size() > 0 && (currentDegrees.get(v.get(j).peekFirst()) != j))
                              { 
                                  while(v.get(j).size() > 0 && (currentDegrees.get(v.get(j).peekFirst()) != j))
                                      v.get(j).pop();
                                  while(j<maxDeg && (v.get(j) == null || v.get(j).size() == 0))
                                                {
                                                        j++;
                                                }
                              }



	         }	
                 /* imax is for simple charikar, 
 		 * jmax is when I consider the subgraph with all the nodes bigger than dens approx */
		int imax = 0, jmax = 0;
		double dmax = Double.NEGATIVE_INFINITY;
		int i = 0;
		for(double dens:densities)
			{
			if(dens >= dmax)
				{
					 imax = i;
					 dmax = dens;
				}
			i++;
			}
		i=0;
		for(int degree: minDegrees)
				{
				if(degree > Math.floor(dmax))
					{
						 jmax = i;
						 break;
					}
				i++;
				}
		System.out.println("\nThe graph containing the optimum has " + (g.n - jmax) + " nodes, and the 2 approx graph has " + (g.n - imax) + " nodes. Density of 2 approx is " + dmax);
                
		return buildVertexSet(nodesRemoved,jmax);
		
		}
	 
	  public static HashSet<Integer> buildVertexSet(LinkedList<Integer> nodesRemoved, int imax) 
	    {
                
	        HashSet<Integer> s = new HashSet<Integer>();
                if (imax > nodesRemoved.size())
                    return s;
		ListIterator<Integer> i = nodesRemoved.listIterator(imax);
                //int length = 0;
	        while(i.hasNext() /*(length < imax + 1)*/)
	        {
                    //length ++;
		    Integer node = (Integer)i.next();
	            s.add(node);
	        }
	        return s;
	    }
	  
	  
	  /* this function returns the subgraph computed using the Charikar 2 approx algorithm */
	 public static Set approxChar(RealNetwork g)
     {
	 Map<Integer, Map<Integer, Double>> matrix = g.cloneGraph();
         int n = g.n;
	 double m = g.m;
	 LinkedList<Integer> nodesRemoved = new LinkedList<Integer>();
         LinkedList<Double> densities = new LinkedList<Double>();
         LinkedList<Integer> minDegrees = new LinkedList<Integer>();
         densities.add(m/n);
        
        
         Map<Integer, Integer> currentDegrees = new HashMap<Integer, Integer>();
         int maxDeg = 0, deg = 0;
         for(int u:matrix.keySet())
         {
                 deg = 0;
                 for(int w : matrix.get(u).keySet())
                         deg += matrix.get(u).get(w);
                 if(maxDeg < deg)
                         maxDeg = deg;
                 currentDegrees.put(u, deg);
         }
         

	HashMap<Integer, LinkedList<Integer>> v = new HashMap<Integer, LinkedList<Integer>>();
        
        for(int u:matrix.keySet())
        {
            deg = currentDegrees.get(u);
            if(v.get(deg) == null)
                v.put(deg, new LinkedList<Integer>());
            v.get(deg).add(u);
        }
        
	int j = 0;
        while(v.get(j) == null)
        {
            j++;
        }



         
         System.out.println("Nodes = " + n + ", edges = " + m + ", density = " + m/n  + ", maxDegree = " + maxDeg);
         int r = 5;
         int p = (n/(100/r)>0)?(n/(100/r)):1;
         int stop = n;
         for(int i=1; i<=stop; i++)
	         {
	             
		     
		     if(i%p==0)
	             {
	                 int c = i/p;
	                 System.out.print(c*r+"%, ");
	             }
	         	 int x = -1;
                     x = v.get(j).pop();
		     minDegrees.add(j);
	             Map<Integer,Double> neighbors_x = matrix.get(x);
		     int maxDecrease = j;
	             for(int y:neighbors_x.keySet())
				     {
				
				         int deg_y = currentDegrees.get(y);
				         if(deg_y>0 && x != y)
				         {
				             int w = neighbors_x.get(y).intValue();
				             if(v.get(deg_y - w) == null)
				                v.put(deg_y - w, new LinkedList<Integer>());
				             if(deg_y - w < maxDecrease)
						maxDecrease = deg_y - w;     
				             v.get(deg_y - w).push(y);
				             currentDegrees.put(y, deg_y - w);
				         }
				     }
			     nodesRemoved.add(x);
			     currentDegrees.put(x, 0);
			     m -= j;
			     n -= 1; 
                             double dens = 0;
                             if(n>0)
                                dens = m/n;
                             densities.add(dens);

                              if(maxDecrease != j)
				  j = maxDecrease ;
            			else
            			{
                			while(j<maxDeg && (v.get(j) == null || v.get(j).isEmpty()))
                				{
                  	  				j++;
             			   		}
            			}
                              while(v.get(j).size() > 0 && (currentDegrees.get(v.get(j).peekFirst()) != j))
                              {
                                  while(v.get(j).size() > 0 && (currentDegrees.get(v.get(j).peekFirst()) != j))
                                      v.get(j).pop();
                                  while(j<maxDeg && (v.get(j) == null || v.get(j).size() == 0))
                                                {
                                                        j++;
                                                }
                              }


	         }
         /* imax is for simple charikar, 
 		 * jmax is when I consider the subgraph with all the nodes bigger than dens approx */
		int imax = 0, jmax = 0;
		double dmax = Double.NEGATIVE_INFINITY;
	        int i = 0;
                for(double dens:densities)
                        {
                        if(dens >= dmax)
                                {
                                         imax = i;
                                         dmax = dens;
                                }
                        i++;
                        }
                i=0;
                for(int degree: minDegrees)
                                {
                                if(degree > Math.floor(dmax))
                                        {
                                                 jmax = i;
                                                 break;
                                        }
                                i++;
                                }
                System.out.println("The graph containing the optimum has " + (g.n - jmax) + " nodes, and the 2 approx graph has" + (g.n - imax) + " nodes. Density of 2 approx is " + dmax);
		return buildVertexSet(nodesRemoved,imax);
		}

}
