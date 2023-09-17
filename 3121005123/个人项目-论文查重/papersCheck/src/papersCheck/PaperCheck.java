package papersCheck;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public interface PaperCheck {
    String fileName = "result.txt";
    Path path = Paths.get(fileName);

    public static void main(String[] args) throws IOException {
        //获取文件地址
        File[] file=new File[2];
        file[0]=new File("copy.txt");
        file[1]=new File("papers.txt");
        writeTxt("------开始对比------","","",1);
        String[][] pages=new String[2][];//存储每一段的内容
        String[][][] lines=new String[2][][];//存储每一句的内容
        int[] chars=new int[2];//存储每篇文章的字符数

        //对每篇文章进行比较准备
        for(int i=0;i<2;i++) {
            //判断文件是否获取成功并获取文章段落数
            int pagesNum=isFind(file[i]);
            //将每个段落读出并存储起来
            String[] page=new String[pagesNum];
            page=getTxt(file[i],pagesNum,page);
            pages[i]=page;
            //将段落划分为句
            String[][] line=new String[pagesNum][];
            line=getLine(pages[i],line);
            lines[i]=line;
            //展示文章信息并统计字符数量
            chars[i]=showData(lines[i]);
        }
        //开始对比文章，查找重复内容
        writeTxt("------搜索到的重复内容------","","",2);
        int cou=0;//记录重复内容的总字符数
        if(lines[0].length>lines[1].length) {
            for(int i=0;i<lines[0].length;i++) {
                for(int j=0;j<lines[1].length;j++) {
                    cou+=compareTxt(pages[0][i],pages[1][j],lines[0][i],lines[1][j]);
                }
            }
        }else {
            for(int i=0;i<lines[1].length;i++) {
                for(int j=0;j<lines[0].length;j++) {
                    cou+=compareTxt(pages[1][i],pages[0][j],lines[1][i],lines[0][j]);
                }
            }
        }
        //查找结果的统计处理
        float rat;//用于记录查重率
        //判断哪篇文章的字符数最少
        if(chars[0]<chars[1]) {
            rat=(float)cou/chars[0];
        }else {
            rat=(float)cou/chars[1];
        }
        //输出结果
        writeTxt("------最终的查重结果------","","",2);
        String str="该文章相同字符比为："+rat;
        writeTxt(str,"","",2);
    }

    //将结果写入结果文件
    public static void writeTxt(String str1,String str2,String str3,int type) throws IOException{
        if(type==1) {
            try (BufferedWriter writer =
                         Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                writer.write(str1+str2+str3+"\r\n");
            }
        }else {
            try (BufferedWriter writer =
                         Files.newBufferedWriter(path,
                                 StandardCharsets.UTF_8,
                                 StandardOpenOption.APPEND)){
                writer.write(str1+str2+str3+"\r\n");
            }
        }

    }

    //比对函数
    public static int compareTxt(String page1,String page2,String[] lines1,String[] lines2) throws IOException {
        int num=0;//记录重复字段的字符数
        //段落字符数对比
        if(page1.length()<=10||page2.length()<10) {
            return 0;
        }
        //逐句对比
        for(String line1:lines1) {
            for(String line2:lines2) {
                if(line1.indexOf(line2)!=-1) {
                    writeTxt(line2,"","",2);
                    num+=line2.length();
                }else if(line2.indexOf(line1)!=-1) {
                    writeTxt(line1,"","",2);
                    num+=line1.length();
                }
            }
        }

        return num;
    }
    //展示每篇文章的处理信息
    public static int showData(String[][] lines) throws IOException {
        int j=0;//段落数
        int k=0;//句
        int c=0;//字符数
        for(;j<lines.length;j++) {
            for(;k<lines[j].length;k++) {
                c+=lines[j][k].length();
            }
        }
        writeTxt("共",String.valueOf(j),"个段落", 2);
        writeTxt("共",String.valueOf(k),"个句子", 2);
        writeTxt("共",String.valueOf(c),"个字符", 2);
        return c;

    }
    //对没个段落进行分句处理函数
    public static String[][] getLine(String[] page,String[][] lines) {

        int num=0;//记录该句子为该段落中的第几句
        for(String p : page) {
            String[] temp= {};
            temp=p.split(",|\\.|\\?|，|。|？|！");
            lines[num]=temp;
            num++;
        }
        return lines;
    }
    //对文章进行分段处理函数
    public static String[] getTxt(File file,int num,String[]  pages) throws IOException {
        int i=0;
        String encoding = "utf-8";
        //创建读文件对象
        try (InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
             BufferedReader bufferedReader = new BufferedReader(read)) {
            //用于记录每一行的内容
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                pages[i]=lineTxt;
                i++;
            }
        }catch (Exception e) {
            writeTxt("读取文件内容出错","","", 2);
        }
        return pages;

    }
    //判断文件是否存在函数
    public static int isFind(File file) throws IOException {
        writeTxt("------读取文件",file.getName(),"的内容------", 2);
        int num=0;//用于记录文章字数
        String encoding = "utf-8";
        try (InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
             BufferedReader bufferedReader = new BufferedReader(read)) {
            //判断文件是否存在
            if (file.isFile() && file.exists()) {
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    writeTxt(lineTxt,"","", 2);
                    num++;
                }
                int i=0;
                String[]  lines=new String[num];
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    lines[i]=lineTxt;
                    i++;
                }

            } else {
                writeTxt("找不到指定的文件","","", 2);
            }
        } catch (Exception e) {
            writeTxt("读取文件内容出错","","", 2);
        }
        return num;
    }

}