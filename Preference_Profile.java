import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by loujian on 3/19/16.
 */
public class Preference_Profile {

    private int n;//means the number of player in the profile
    private ArrayList<LinkedList<Integer>> profile = new ArrayList<LinkedList<Integer>>();
    private ArrayList<ArrayList<Integer>> value_profile= new ArrayList<ArrayList<Integer>>();
    private int[] final_payoff;
    private boolean[] flag_profile; //denote whether a player exists in the profile
    private LinkedList<Integer> order= new LinkedList<Integer>();


    Preference_Profile(int n, ArrayList<LinkedList<Integer>> profile, ArrayList<ArrayList<Integer>> value_profile, boolean[] flag_profile)
    {
        this.n=n;
        this.profile.addAll(profile);
        this.value_profile.addAll(value_profile);
        this.final_payoff =new int[n+1];
        for(int i=1; i<=n; i++)
            this.final_payoff[i]=0;
        this.order.clear();
        for(int i=0;i<n; i++)
            this.order.add(i+1);
        this.flag_profile=new boolean[n+1];
        for(int i=1; i<=n; i++)
            this.flag_profile[i]= flag_profile[i];
    }

    int[] soulmates()
    {
        Integer[] prefer= new Integer[n+1];
        for(int i=0 ;i<=n; i++)
            prefer[i]=0;
        boolean count=true;
        while(count==true && order.size()>1)
        {
            for (int i = 1; i <= n; i++)
            {
                if (flag_profile[i] == true)
                    prefer[i] = profile.get(i).getFirst();
            }
            count = false;
            for (Integer i = 1; i <= n; i++)
            {
                for (Integer j = i + 1; j <= n; j++)
                {
                    if (flag_profile[i] == true && flag_profile[j] == true && prefer[i] == j && prefer[j] == i)
                    {
                        final_payoff[i]= value_profile.get(i).get(j);
                        final_payoff[j]= value_profile.get(j).get(i);
                        count = true;
                        for (int k = 1; k <= n; k++)
                        {
                            profile.get(k).remove(i);
                            profile.get(k).remove(j);
                        }
                        order.remove(i);
                        order.remove(j);
                        flag_profile[i] = false;
                        flag_profile[j] = false;
                        i = n + 1;
                        j = n + 1;//we could use this way to break the two loops
                    }
                }
            }
        }

        return final_payoff;
    }



    boolean match_pair(int s, int t) //means the second player rank s_th for the first player, and the first player ranked t_st for the first player
    {
        boolean flag_pair= false;
        Integer[] s_th= new Integer[n+1];
        Integer[] t_th= new Integer[n+1];
        for(int i=0 ;i<=n; i++)
        {
            s_th[i]=0;
            t_th[i]=0;
        }

        for (int i = 1; i <= n; i++)
        {
            if (flag_profile[i] == true && profile.get(i).size()>=s)
                s_th[i] = profile.get(i).get(s-1);
            if (flag_profile[i] == true && profile.get(i).size()>=t)
                t_th[i] = profile.get(i).get(t-1);
        }

        for (Integer i = 1; i <= n; i++)
        {
            for (Integer j =1; j <= n&& j!=i; j++)
            {
                if (flag_profile[i] == true && flag_profile[j] == true && profile.get(i).size()>=s && profile.get(j).size()>=t && s_th[i] == j && t_th[j] == i)
                {
                    final_payoff[i]= value_profile.get(i).get(j);
                    final_payoff[j]= value_profile.get(j).get(i);

                    for (int k = 1; k <= n; k++)
                    {
                        profile.get(k).remove(i);
                        profile.get(k).remove(j);
                    }
                    order.remove(i);
                    order.remove(j);
                    flag_profile[i] = false;
                    flag_profile[j] = false;
                    flag_pair=true;
                    i = n + 1;
                    j = n + 1;//we could use this way to break the two loops
                }
            }
        }

        return flag_pair;
    }


    int[] Iterative_Eliminating()
    {
        while(order.size()>=2)
        {
            soulmates();
            int count=1;
            while(order.size()>=2 && count>=0)
            {
                for(int sum=3; sum<999999 ;sum++)
                {
                    for(int i=sum/2; i>=1; i--)
                    {
                        int j=sum-i;

                        boolean tmp_flag= match_pair(i, j);
                        if(tmp_flag==true)
                        {
                            i=0;
                            sum=999999999;
                            count=-1;

                        }
                    }

                }
            }
        }
        return final_payoff;

    }

}
