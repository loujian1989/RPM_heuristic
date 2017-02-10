import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by loujian on 2/22/16.
 */
public class SoulMateElimination {

    public static void main(String[] args)throws Exception
    {
        int n; //n means the number of players in the game.
        int depth;// depth means search depth
        LinkedList<Integer> order= new LinkedList<Integer>(); //means the list of players in the order

        Scanner cin=new Scanner(new File("preference_input"));

        n=cin.nextInt(); //the number of players in the game
        depth=cin.nextInt();//
        for(int i=0; i<n; i++)
            order.add(cin.nextInt()); //input the order in the game.

        ArrayList<LinkedList<Integer>> profile = new ArrayList<LinkedList<Integer>>();
        ArrayList<LinkedList<Integer>> profile_copy = new ArrayList<LinkedList<Integer>>();
        profile.clear();
        profile_copy.clear();
        //LinkedList<Integer> tmp1= new LinkedList<Integer>();
        //profile.add(tmp1);

        for(int i=0;i<=n; i++)
        {
            LinkedList<Integer> tmp= new LinkedList<Integer>();
            LinkedList<Integer> tmp1= new LinkedList<Integer>();
            for (int j = 0; j <= n; j++)
            {
                tmp.add(0);
                tmp1.add(0);
            }
            profile.add(tmp);
            profile_copy.add(tmp1);
        }

        for(int i=1; i<=n; i++) //for n different players
        {
            for(int j=0; j<n-1; j++) //preference of a corresponding player
            {
                Integer tmp=cin.nextInt();
                profile.get(i).set(j, tmp); //set the preference profiles
                profile_copy.get(i).set(j, tmp);
            }
        }

        Boolean[] flag= new Boolean[n+1];
        for(int i=0; i<=n; i++)
            flag[i]=true;

        Integer[] prefer= new Integer[n+1];

        for(int i=0 ;i<=n; i++)
            prefer[i]=0;


        File writename = new File("preprocess_out.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));
        boolean count=true;
        while(count==true)
        {
            for(int i=1; i<=n; i++)
            {
                prefer[i]= profile.get(i).getFirst();
            }
            count=false;
            for(Integer i=1; i<=n; i++)
            {
                for(Integer j=i+1; j<=n; j++)
                {
                    if(flag[i]==true && flag[j]== true && prefer[i]==j && prefer[j]==i)
                    {
                        count=true;
                        out.write(i + " with " + j+"\r\n");
                        for(int k=1; k<=n; k++)
                        {
                            profile.get(k).remove(i);
                            profile.get(k).remove(j);
                        }
                        order.remove(i);
                        order.remove(j);
                        flag[i]=false;
                        flag[j]=false;
                        i=n+1;
                        j=n+1;//we could use this way to break the two loops
                    }
                }
            }
        }
        out.write("\r\n");
        out.write(n+ " " + order.size()+"\r\n");
        out.write(depth+"\r\n");
        for(int i=0; i<order.size(); i++)
            out.write(order.get(i)+ " ");
        out.write("\r\n");

        for(int i=1; i<=n; i++)
        {
            for(int j=0; j<n-1; j++)
                out.write(profile_copy.get(i).get(j) + " ");
            out.write("\r\n");
        }

        out.flush();
        out.close();
    }

}
