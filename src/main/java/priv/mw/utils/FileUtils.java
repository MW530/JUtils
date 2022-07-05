package priv.mw.utils;

import java.io.File;

/**
 * @Auther: MichaelWang
 * @Date: 2022/7/4 16:44
 * @Description: FileUtils
 * @Version 1.0.0
 */
public class FileUtils {
    public static String getOuterFolderPath(String path){
        String[] split = path.split("/");
        if (split.length == 1){
            split = path.split("\\\\");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            sb.append(split[i]);
            sb.append("/");
        }
        return sb.toString();
    }
}
