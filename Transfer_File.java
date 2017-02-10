/**
 * Created by loujian on 8/16/16.
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;



public class Transfer_File {

    public static void main(String[] args)throws Exception
    {
        Scanner cin=new Scanner(new File("karate_list.txt"));
        File writename = new File("karate_matrix.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        int n=34;
        int[][] array= new int[n+1][n+1];

        for(int i=1; i<=n; i++)
            for(int j=1; j<=n; j++)
                array[i][j]=0;

        while(cin.hasNext())
        {
            Integer x= cin.nextInt();
            Integer y= cin.nextInt();
            array[x][y]=1;
            array[y][x]=1;

        }

        for(int i=1; i<=n; i++) {
            for (int j = 1; j <= n; j++)
                out.write(array[i][j]+ " ");
            out.write("\r\n");
        }


        out.write("\r\n");
        out.write("\r\n");

        out.flush();
        out.close();
    }

}
