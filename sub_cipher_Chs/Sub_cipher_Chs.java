/**
 * java中（jvm和class文件都是unicode编码，一般电脑的默认的中文编码方式是GBK编码）
 * 程序从文件中读取中文时，（对于在程序外部手动输入的中文）要能正确的在程序中使用就必须要以GBK的方式将字节数组转为字符串
 * 程序写出的文件时，是以unicode的方式写入的，因此在程序间生成的文件进行读取操作时，要以UTF-8的编码方式进行读写
 */
package sub_cipher_Chs;

import sun.nio.cs.UnicodeEncoder;

import java.io.*;
import java.util.Scanner;

/**
 * Created by Suagr on 2017/4/4.
 */
public class Sub_cipher_Chs {
    public static void main(String[] args){
        File orFile = new File("src\\sub_cipher_Chs\\originalText.txt");
        File enFile = new File("src\\sub_cipher_Chs\\encodeText.txt");
        File deFile = new File("src\\sub_cipher_Chs\\decodeText.txt");
        File cipherFile = new File("src\\sub_cipher_Chs\\cipherText.txt");
        FileOutputStream fos = null;
        byte[] bytes = new byte[1024];
        // ----- 开始加密
        System.out.println("请选择加密（'en'）或者是解密（'de'）：");
        Scanner scan = new Scanner(System.in);
        String choose = scan.nextLine();

        if(choose.equals("en")){
            String enStr = outEncodeFile(orFile,cipherFile);
            if(enStr.length()>0) {
                System.out.println("加密成功！密文： ");
                System.out.println(enStr);
            }
            bytes = enStr.getBytes();
            try {
                fos = new FileOutputStream(enFile);
                fos.write(bytes);
                fos.flush();
            } catch(FileNotFoundException e){
                System.out.println("打开加密文件出错");
            } catch(IOException e){
                System.out.println("写入加密文件内容出错");
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    System.out.println("关闭加密文件流出错");
                }
            }
        } else if(choose.equals("de")){
            String deStr = outDecodeFile(enFile,cipherFile);
            if(deStr.length()>0) {
                System.out.println("解密成功！原文： ");
                System.out.println(deStr);
            }
            bytes = deStr.getBytes();
            try{
                fos = new FileOutputStream(deFile);
                fos.write(bytes);
                fos.flush();
            } catch(FileNotFoundException e){
                System.out.println("打开解密文件出错");
            } catch(IOException e){
                System.out.println("写入解密文件内容出错");
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    System.out.println("关闭解密文件流出错");
                }
            }
        } else{
            System.out.println("别搞事 →_→");
            System.exit(0);
        }
    }

    /**
     * 输出密文
     * @return
     */
    private static String outEncodeFile(File orFile,File cipherFile){
        String enStr = null;
        FileInputStream fis = null;
        byte[] bytes = null;// 创建一个1024字节的缓冲
        String oriStr = null;
        String cipherStr = null;
        try {
            bytes = new byte[(int)orFile.length()];
            // ------ 读取原始数据
            fis = new FileInputStream(orFile);
            fis.read(bytes);
            oriStr = new String(bytes,"gbk");
            bytes = new byte[(int)cipherFile.length()];
            // ------ 读取密钥
            fis = new FileInputStream(cipherFile);
            fis.read(bytes);
            cipherStr = new String(bytes,"gbk");
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("打开原文或者密钥文件出错");
        } catch (IOException e) {
            System.out.println("读取原文或者密钥文件内容出错");
        }
        System.out.println("原文： ");
        System.out.println(oriStr);
        System.out.println("------------------------");
        System.out.println("密钥： " + cipherStr);
        System.out.println("------------------------");
        // ------ 开始加密
        enStr = encode(oriStr,cipherStr);
        return enStr;
    }

    /**
     * 输出明文
     * @param enFile
     * @param cipherFile
     * @return
     */
    private static String outDecodeFile(File enFile,File cipherFile){
        String deStr = null;
        FileInputStream fis = null;
        byte[] bytes = null;
        String enStr = null;
        String cipherStr = null;
        try{
            // ----- 开始读取密文
            bytes = new byte[(int)enFile.length()];
            fis = new FileInputStream(enFile);
            fis.read(bytes);
            enStr = new String(bytes,"utf-8");
            bytes = new byte[(int)cipherFile.length()];
            // ------ 开始读取密钥
            fis = new FileInputStream(cipherFile);
            fis.read(bytes);
            cipherStr = new String(bytes,"gbk");
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("打开密文或者密钥文件出错");
        } catch (IOException e) {
            System.out.println("读取密文或者密钥文件内容出错");
        }
        System.out.println("密文： ");
        System.out.println(enStr);
        System.out.println("------------------------");
        System.out.println("密钥： " + cipherStr);
        System.out.println("------------------------");
        // ------ 开始解密
        deStr = decode(enStr,cipherStr);
        return deStr;
    }

    /**
     * 加密
     * @param oriStr
     * @param cipher
     * @return
     */
    private static String encode(String oriStr, String cipher){
        String enStr = null;
        StringBuffer str = new StringBuffer();
        int len = cipher.length();
        int[] encode = new int[len];
        int flag = 0;
        for(int i=0;i<len;i++){
            flag %= 4;
            String ch = Integer.toHexString((int)cipher.charAt(i));
            for(int j=ch.length(); j<4; j++)
                ch = "0" + ch;
            encode[i] = ch.charAt(flag) - '0';// 中间的其它符号也无所谓
            flag++;
        }
        flag = 0;
        for(int i=0;i<oriStr.length();i++) {
            flag %= len;
            char c = oriStr.charAt(i);
            String ch = Integer.toHexString((int)c);
            for(int j=ch.length(); j<4; j++)
                ch = "0" + ch;
            char data = (char) (Integer.parseInt(ch,16) + encode[flag]);
            str.append(data);
            flag++;
        }
        enStr = str.toString();
        return enStr;
    }

    /**
     * 解密
     * @param enStr
     * @param cipher
     * @return
     */
    private static String decode(String enStr,String cipher){
        String deStr = null;
        StringBuffer str = new StringBuffer();
        int len = cipher.length();
        int[] encode = new int[len];
        int flag = 0;
        for(int i=0;i<len;i++){
            flag %= 4;
            String ch = Integer.toHexString((int)cipher.charAt(i));
            for(int j=ch.length(); j<4; j++)
                ch = "0" + ch;
            encode[i] = ch.charAt(flag) - '0';// 中间的其它符号也无所谓
            flag++;
        }
        flag = 0;
        for(int i=0;i<enStr.length();i++) {
            flag %= len;
            char c = enStr.charAt(i);
            String ch = Integer.toHexString((int)c);
            for(int j=ch.length(); j<4; j++)
                ch = "0" + ch;
            char data = (char) (Integer.parseInt(ch,16) - encode[flag]);
            str.append(data);
            flag++;
        }
        deStr = str.toString();
        return deStr;
    }
}
