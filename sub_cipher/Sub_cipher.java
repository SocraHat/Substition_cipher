
package sub_cipher;

import java.io.*;
import java.util.Scanner;

/**
 * 主要思路是每一个字母对应一个偏移量，同时密钥的长度是奇数时，大小写相反，密钥长度偶数时，大小写相同
 * 密钥长度不超过26
 * Created by Suagr on 2017/4/3.
 */
public class Sub_cipher {

    public static void main(String[] args){
        File orFile = new File("src\\sub_cipher\\originalText.txt");
        File enFile = new File("src\\sub_cipher\\encodeText.txt");
        File deFile = new File("src\\sub_cipher\\decodeText.txt");
        File cipherFile = new File("src\\sub_cipher\\cipherText.txt");
        FileOutputStream fos = null;
        byte[] bytes = new byte[1024];
        // ----- 开始加密
        System.out.println("请选择加密（'en'）或者是解密（'de'）：");
        Scanner scan = new Scanner(System.in);
        String choose = scan.nextLine();

        if(choose.equals("en")){
            String enStr = outEncodeFile(orFile,cipherFile);
            if(enStr.length()>0){
                System.out.println("加密成功！密文： " );
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
        } else if(choose.equals("de")) {
            String deStr = outDecodeFile(enFile, cipherFile);
            if (deStr.length() > 0){
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
     *  输出加密的密文
     * @return
     */
    private static String outEncodeFile(File orFile,File cipherFile){
        String enStr = null;
        FileInputStream fis = null;
        byte[] bytes = null;// 创建一个1024字节的缓冲
        String oriStr = null;
        String cipherStr = null;
        try {
            // ------ 读取原始数据
            bytes = new byte[(int)orFile.length()];
            fis = new FileInputStream(orFile);
            fis.read(bytes);
            oriStr = new String(bytes);
            bytes = new byte[(int)cipherFile.length()];
            // ------ 读取密钥
            fis = new FileInputStream(cipherFile);
            fis.read(bytes);
            cipherStr = new String(bytes);
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("打开原文或者密钥文件出错");
        } catch (IOException e) {
            System.out.println("读取原文或者密钥文件内容出错");
        }
        System.out.println("原文： ");
        System.out.println(oriStr);
        System.out.println("--------------------");
        System.out.println("密钥： " + cipherStr);
        System.out.println("--------------------");
        // ----- 开始加密
        enStr = encode(oriStr,cipherStr);
        return enStr;
    }

    /**
     * 输出明文
     * @param enFile
     * @param cipherFile
     * @return
     */
    private static String outDecodeFile(File enFile, File cipherFile) {
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
            enStr = new String(bytes);
            bytes = new byte[(int)cipherFile.length()];
            // ------ 开始读取密钥
            fis = new FileInputStream(cipherFile);
            fis.read(bytes);
            cipherStr = new String(bytes);
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("打开密文或者密钥文件出错");
        } catch (IOException e) {
            System.out.println("读取密文或者密钥文件内容出错");
        }
        System.out.println("密文： " );
        System.out.println(enStr);
        System.out.println("--------------------");
        System.out.println("密钥： " + cipherStr);
        System.out.println("--------------------");
        // ------ 开始解密
        deStr = decode(enStr,cipherStr);
        return deStr;
    }

    /**
     * 加密，根据密钥来计算偏移
     * @param originalString 原始串
     * @param cipherCode 密钥
     * @return
     */

    private static String encode(String originalString, String cipherCode){
        String encodeString = null;
        int flag = 0;
        int len = cipherCode.length();
        int[] code = new int[len];
        char[] res = new char[originalString.length()];
        for(int i=0;i<len;i++){
            char c = cipherCode.charAt(i);
            if(c>='A' && c<='Z'){// 大写字母
                code[i] = c - 65;
            } else if(c>='a' && c<='z'){
                code[i] = c - 97 + len;
            }
        }
        for(int i=0;i<originalString.length();i++){
            char c = originalString.charAt(i);
            if(flag==code.length){
                flag = 0;
            }
            if(c>='A' && c<='Z'){
                if(len%2==0)
                    res[i] = (char)('A'+ ((c - 65) + code[flag])%26);
                else
                    res[i] = (char)('a'+ ((c - 65) + code[flag])%26);
                flag++;
            } else if(c>='a' && c<='z'){
                if(len%2==0)
                    res[i] = (char)('a'+ ((c - 97) + code[flag])%26);
                else
                    res[i] = (char)('A'+ ((c - 97) + code[flag])%26);
                flag++;
            } else{ // 其它字符
                res[i] = c;
            }
        }
        encodeString = new String(res);
        return encodeString;
    }

    /**
     * 解密
     * @param encode
     * @param cipherCode 密钥
     * @return
     */
    private static String decode(String encode, String cipherCode){
        String zz = null;
        String decodeString = null;
        int len = cipherCode.length();
        int[] code = new int[len];
        int flag = 0;
        char[] res = new char[encode.length()];
        for(int i=0;i<len;i++){
            char c = cipherCode.charAt(i);
            if(c>='A' && c<='Z'){// 大写字母
                code[i] = c - 65;
            } else if(c>='a' && c<='z'){
                code[i] = c - 97 + len;
            }
        }
        for(int i=0;i<encode.length();i++){
            char c = encode.charAt(i);
            if(flag==code.length){
                flag = 0;
            }
            if(c>='A' && c<='Z'){
                if(len%2==0) {
                    res[i] = (char) ('A' + (c - 65) - code[flag]);
                    if(res[i]<'A')
                        res[i] += 26;
                } else{
                    res[i] = (char)('a'+ (c - 65) - code[flag]);
                    if(res[i]>='A' && res[i]<='Z') {
                        res[i] += 26;
                        if(res[i]>'Z' && res[i]<'a')
                            res[i] += 26;
                    } else if(res[i]<'A')
                        res[i] += 52;
                    else if(res[i]>'Z' && res[i]<'a')
                        res[i] += 26;
                    else if(res[i]>'z')
                        res[i] -= 26;
                }
                flag++;
            } else if(c>='a' && c<='z'){
                if(len%2==0) {
                    res[i] = (char) ('a' + ((c - 97) - code[flag]));
                    if (res[i] >= 'A' && res[i] <= 'Z') {
                        res[i] += 26;
                        if(res[i]>'Z' && res[i]<'a')
                            res[i] += 26;
                    } else if(res[i]<'A'){
                        res[i] += 52;
                    } else if(res[i]>'Z' && res[i]<'a'){
                        res[i] += 26;
                    } else if(res[i]>'z')
                        res[i] -= 26;
                } else {
                    res[i] = (char) ('A' + ((c - 97) - code[flag]));
                    if(res[i]<'A')
                        res[i] += 26;
                }
                flag++;
            } else{ // 其它字符
                res[i] = c;
            }
        }
        decodeString = new String(res);
        return decodeString;
    }
}
