package DensestSubgraph;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.LinkedList;

public class RealNetwork {

	Map<Integer,Map<Integer,Double>> g;
	public int n;
	public double m; 

	public RealNetwork(Map<Integer,Map<Integer,Double>> matrix)
	{
		 n = matrix.size();
                 m = 0;
       		 for(int u:matrix.keySet())
		    for(int v:matrix.get(u).keySet())
        		{
            			m += matrix.get(u).get(v);
                                if(u == v)
                                    m += matrix.get(u).get(v);
        		}
	        m = m/2;
		g = matrix;
	}

	public RealNetwork(String path) throws IOException
		    {
		 	 n= 0;
		 	 m = 0;
		        System.out.print("Loading graph...");
		        if(g == null)
		        	g = new HashMap<Integer,Map<Integer,Double>>();
		        
		        BufferedReader br = new BufferedReader(new FileReader(path));
		        String line = br.readLine();
		        while(line != null)
		        {
		            StringTokenizer st = new StringTokenizer(line,"\t ,;");
		            int u = Integer.parseInt(st.nextToken());
		            int v = Integer.parseInt(st.nextToken());
		            double w = 1.0;
		            if(st.hasMoreTokens())
		            {
		                w = Double.parseDouble(st.nextToken());
		            }
		            
		            if(!g.containsKey(u))
		            {
		                g.put(u, new HashMap<Integer,Double>());
		                n++;
		            }
		            if(!g.containsKey(v))
		            {
		                g.put(v, new HashMap<Integer,Double>());
		                n++;
		            }
                            double oldW = 0.;
		            if(null == g.get(u).get(v))
		            	//oldW = g.get(u).get(v);
		            	m+=1;
		            g.get(u).put(v, oldW + w);
		            g.get(v).put(u, oldW + w);
                            //m+=w;		            
		            line = br.readLine();
					
		        }
		        br.close(); 
		        System.out.println("DONE!\n");
		        
		    }

 	
	
	public int getRandomNode()
	{
		
		int position = (int)(Math.random()*n);
		Object[] array = g.keySet().toArray();
		int u = (Integer)array[position];
		return u;
	}

	public boolean isEmpty()
	{
		if(n==0)
			return true;
		return false;
	} 

	public boolean isDisconnected()
	{
		if(m==0)
                        return true;
                return false;

	}
    public void removeSubgraph(Set<Integer> ds) 
    {
    	
        for(int x:ds)
        {
            if(!g.containsKey(x))
            	continue;
            Map<Integer,Double> neighbors_x = g.get(x);
            for(int y:neighbors_x.keySet())
            {

		m -= g.get(y).get(x); 
                if (x == y)
                    continue; 
                g.get(y).remove(x);
                if(g.get(y).size() == 0)
                	{
                		g.remove(y);
                		n--;
                	}
            }
            g.remove(x);
            n--;
        }
        
    }
	
	    	
    public void printTopI(String output, int i, boolean weighted) throws IOException
	    {
	        BufferedWriter bw = new BufferedWriter(new FileWriter(output+"_Top"+i+".txt"));
	        
	        //int[] size = size();
	        bw.write(""+n+" "+m);
	        bw.newLine();
	        
	        for(int u:g.keySet())
	        {
	            for(int v:g.get(u).keySet())
	            {
	                if (u<v)
	                {
	                    bw.write(""+u+" "+v);
	                    if(weighted)
	                    {
	                        bw.write(" "+g.get(u).get(v));
	                    }
	                    bw.newLine();
	                }
	            }
	        }
	        bw.flush();
	        bw.close();        
	    }

	    public void printTopINodes(String output, int i) throws IOException
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(output+"_Top"+i+".txt"));

                //int[] size = size();
                //bw.write(""+size[0]+" "+size[1]);
                //bw.newLine();

                for(int u:g.keySet())
                {
		    bw.write(""+u);
		    bw.newLine();
                }
                bw.flush();
                bw.close();
            }


    public Set<Integer> listNodes()
    {
    	Set<Integer> listNodes = new HashSet<Integer>(g.keySet());
    	return listNodes;
    }
    
    public Set<Integer> listNeighbours(int u)
    {
    	Set<Integer> listNodes = new HashSet<Integer>(g.get(u).keySet());
    	return listNodes;
    }
    
  
    public int outsideDegree(Set<Integer> nodes)
	{
		int out = 0;
		 for(int x:nodes)
                	{
				Set<Integer> nx = new HashSet(g.get(x).keySet());
				nx.removeAll(nodes);
				for(int y:nx)
					out += g.get(x).get(y);
			}
		return out;
	} 
    
    public RealNetwork inducedSubgraph(Set<Integer> s)
	    {
                double edges = 0.;
	        Map<Integer, Map<Integer, Double>> gprime = new HashMap<Integer, Map<Integer, Double>>();
	        for(int u:s)
	        {
	            Map<Integer,Double> nx = g.get(u);
	            if(nx == null)
	            {
			continue;
	            }
                   
	            for(int v:nx.keySet())
	            {
	                if(s.contains(v))
	                {
	                    if(!gprime.containsKey(u))
	                    {
	                        gprime.put(u, new HashMap<Integer,Double>());
	                    }
	                    if(!gprime.containsKey(v))
	                    {
	                        gprime.put(v, new HashMap<Integer,Double>());
	                    }
                            double w = nx.get(v);
	                    gprime.get(u).put(v, w);
			    gprime.get(v).put(u, w);
                            edges += w;
	                }
	            }            
	        } 
	        return new RealNetwork(gprime);        
	    }
	    
    public Map<Integer, Map<Integer, Double>> buildGraph( int[] nodesRemoved, int imax) 
	    {
	        Map<Integer,Map<Integer,Double>> gprime = cloneGraph();
	        
	        for(int i=1; i<=imax; i++)
	        {
	            int x = nodesRemoved[i];
	            for(int y:gprime.get(x).keySet())
	            {
	                gprime.get(y).remove(x);
	            }
	            gprime.remove(x);
	        }
	        
	        return gprime;
	    }

    public Map<Integer, Map<Integer, Double>> cloneGraph()
	    {
	        Map<Integer, Map<Integer, Double>> gprime =  new HashMap<Integer, Map<Integer, Double>>();
	        for(int x:g.keySet())
	        {
	           Map<Integer, Double> nx = g.get(x);
	           Map<Integer, Double> mprime = new HashMap<Integer, Double>();
	           for(int y:nx.keySet())
	           {
	               mprime.put(y, nx.get(y));
	           }
	           gprime.put(x, mprime);
	        }
	        
	        return gprime;
	    }

    public Map<Integer, Map<Integer, Double>> cloneGraph(Map<Integer, Map<Integer, Double>> matrix)
    {
        Map<Integer, Map<Integer, Double>> gprime =  new HashMap<Integer, Map<Integer, Double>>();
        
	for(int x:matrix.keySet())
        {
	  
           Map<Integer, Double> nx = matrix.get(x);
           Map<Integer, Double> mprime = new HashMap<Integer, Double>();
           for(int y:nx.keySet())
           {
               mprime.put(y, nx.get(y));
           }
           gprime.put(x, mprime);
        }
        
        return gprime;
    }
	

}
