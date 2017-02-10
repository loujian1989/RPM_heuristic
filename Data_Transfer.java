/**
 * Created by loujian on 7/14/16.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Random;
import java.lang.Math;

public class Data_Transfer {

    public static void main(String[] args)throws Exception
    {
        Scanner cin=new Scanner(new File("Clean_Scale_Free_SN_1000_n30m3.txt"));
        File writename = new File("SN_Scale_Free_1000_n30m3.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));
        int n = 30;

        double epsilon=0.01;

        int num_cases=1000;

        for(int iter=1; iter<=num_cases; iter++)
        {


            ArrayList<ArrayList<Double>> value = new ArrayList<ArrayList<Double>>(); //simulate the preference profile of all players

            int[][]array= new int[n+1][n+1];

            value.clear();

            for (int i = 0; i <= n; i++) {
                ArrayList<Double> tmp = new ArrayList<Double>();
                tmp.clear();
                for (int j = 0; j <= n; j++) {
                    if(j==i)
                        tmp.add(0.0);
                    else
                        tmp.add(-2.0);
                }
                value.add(tmp);
            }


            int[] number_neighbor = new int[n+1];
            for(int i=1; i<=n; i++)
                number_neighbor[i]=0;

            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    Integer tmp1= cin.nextInt();
                    Double tmp = tmp1*1.0;
                    if(tmp!=0.0 && tmp!=-1.0)
                        number_neighbor[i]++;
                    value.get(i).set(j, tmp); //here we let it be the linear decreasing function
                }

            } //We want to make sure everything goes well for the array.


            Random rd = new Random();
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {

                    if(i==j || value.get(i).get(j)==0.0)
                        continue;

                    double noise= rd.nextGaussian();
                    noise*= epsilon;
                    value.get(i).set(j, value.get(i).get(j)+noise); //add some noise into the value array
                }
            }

            boolean[][] position = new boolean[n+1][n+1];
            for(int i=1; i<=n; i++) {
                for (int j = 1; j <= n; j++) {
                    position[i][j] = false; //at the very first, all position are available
                    if (i == j)
                        position[i][j] = true;
                }
            }


            for(int i=1; i<=n; i++)
            {
                for(int j=1; j<=number_neighbor[i]; j++)//every round, we add one player into the array
                {
                    double max_tmp=-9999;
                    int max_index=-1;
                    for(int l=1; l<=n; l++)
                    {
                        if( position[i][l]==false && value.get(i).get(l)> max_tmp)
                        {
                            max_tmp= value.get(i).get(l);
                            max_index= l;
                        }
                    }

                    position[i][max_index]=true;
                    array[i][j]= max_index;
                }
            }

            for(int i=1; i<=n; i++)
            {
                out.write(number_neighbor[i]+ " ");
                for(int j=1; j<=number_neighbor[i]; j++)
                {
                    out.write(array[i][j]+ " ");
                }
                out.write("\r\n");
            }
            out.write("\r\n");

        }





        out.flush();
        out.close();

    }
}













