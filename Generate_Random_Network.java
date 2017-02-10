
/**
 * Created by loujian on 8/6/16.
 * This class could create some random networks, in the network edge appears with probability lambda
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Random;
import java.lang.Math;


public class Generate_Random_Network {

    public static void main(String[] args)throws Exception
    {

        File writename = new File("Random_Network_n46_lambda006.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        int n=46; //the number of players in the network
        double lambda=0.06; //the probability of a


        double epsilon=0.01; //Add some noise into the matrix

        int num_cases=100;

        for(int iter=1; iter<=num_cases; iter++) {

            Random rd = new Random();

            double[][] matrix = new double[n + 1][n + 1];
            int[] number_neighbor = new int[n+1];
            for(int i=1; i<=n; i++)
                number_neighbor[i]=0;

            for (int i = 1; i <= n; i++)//set the adjacent matrix for the network
            {
                for (int j = 1; j <= i; j++) {
                    if (i == j)
                        matrix[i][j] = -1.0;
                    else {
                        double tmp_double = rd.nextDouble();
                        if (tmp_double <= lambda)
                        {
                            matrix[i][j] = 1.0;
                            matrix[j][i] = 1.0;
                            number_neighbor[i]++;
                            number_neighbor[j]++;
                        }
                        else
                        {
                            matrix[i][j] = 0.0;
                            matrix[j][i] = 0.0;
                        }

                    }
                }
            }

            //We will add some noise into the network to get the ordinal profiles of the network
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {

                    if (i == j || matrix[i][j] == 0.0)
                        continue;

                    double noise = rd.nextGaussian();
                    noise *= epsilon;
                    matrix[i][j] += noise; //add some noise into the matrix
                }
            }

            boolean[][] position = new boolean[n+1][n+1];
            Integer[][] array = new Integer[n+1][n+1];
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
                        if( position[i][l]==false && matrix[i][l]> max_tmp)
                        {
                            max_tmp= matrix[i][l];
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













