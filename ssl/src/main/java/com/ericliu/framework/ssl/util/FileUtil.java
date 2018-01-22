package com.ericliu.framework.ssl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author: <a mailto="liuhaoeric@didichuxing.joyme.com">liuhaoeric</a>
 * Create time: 2018/01/22
 * Description:
 */
public class FileUtil {

    public static String loadKeyString(InputStream in) throws Exception {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("输入流为空");
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }
}
