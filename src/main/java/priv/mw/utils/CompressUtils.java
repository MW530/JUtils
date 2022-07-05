package priv.mw.utils;

import priv.mw.entity.FileInfo;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @Auther: MichaelWang
 * @Date: 2022/7/4 16:42
 * @Description: CompressUtils
 * @Version 1.0.0
 */
public class CompressUtils {

    /***
     * @Author MichaelWang
     * @Date  2022/7/4
     * @Description fileUrls中，文件只会取文件名，文件夹则只会取最内层文件夹的名字。但子文件夹和其下的子文件就会递归压缩了。
     * @Param fileInfos:
     * @Param compressionLevel:
     * @Param filePath:
     * @Return void
     * @Version 1.0.0
    **/
    public static void zipFilesByZipStream(ArrayList<String> fileUrls, Integer compressionLevel, String filePath) throws IOException {
        // 判断外围文件夹是否存在，如果不存在则创建
        if(fileUrls == null || filePath.length() == 0){
            throw  new RuntimeException("文件列表不能为空！");
        }
        String filePathWithoutName = FileUtils.getOuterFolderPath(filePath);
        File pathFile = new File(filePathWithoutName);
        if(!pathFile.exists()){
            pathFile.mkdirs();
        }

        // 判断源文件存在，则删除
        File tempFile = new File(filePath);
        if(tempFile.exists()){
            tempFile.delete();
        }

        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(filePath), Charset.forName("GBK"));
        for (String url : fileUrls) {
            File file = new File(url);
            putEntry(zipOutputStream, file, "");
        }
        zipOutputStream.setLevel(compressionLevel);
        zipOutputStream.flush();
        zipOutputStream.close();
    };

    private static void putEntry(ZipOutputStream zipOutputStream, File file, String basePath) throws IOException {
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File subFile : files) {
                putEntry(zipOutputStream, subFile, basePath + File.separator + file.getName());
            }
        }else{
            ZipEntry entry = new ZipEntry(basePath + File.separator + file.getName());
            entry.setSize(file.length());
            zipOutputStream.putNextEntry(entry);
            byte[] fileByte = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileByte);
            zipOutputStream.write(fileByte);
            zipOutputStream.flush();
            fileInputStream.close();
        }
    }

    public static void extractZipByStream(String zipPath) throws IOException {
        File zipFile = new File(zipPath);
        String newOuterPath = zipFile.getParent() + File.separator + zipFile.getName().split("\\.")[0];
        File newOuterFolder = new File(newOuterPath);
        if(!newOuterFolder.exists()){
            newOuterFolder.mkdirs();
        }
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipPath), Charset.forName("GBK"));
        ZipEntry entry = zipInputStream.getNextEntry();
        while(entry != null){
            String newPath = newOuterPath + File.separator + entry.getName();
            File file =  new File(newPath);
            if(entry.isDirectory()){
                file.mkdirs();
                //文件
            }else{
                String parent = file.getParent();
                File outerFolder = new File(parent);
                if (!outerFolder.exists()){
                    outerFolder.mkdirs();
                }
                file.createNewFile();
                byte[] buff = new byte[1024];
                BufferedOutputStream bufferedInputStream =  new BufferedOutputStream(new FileOutputStream(file));
                int len;
                while ((len = zipInputStream.read(buff, 0 ,1024)) != -1) {
                    bufferedInputStream.write(buff, 0, len);
                }
                zipInputStream.read(buff);
                bufferedInputStream.flush();
                bufferedInputStream.close();
            }
            entry = zipInputStream.getNextEntry();
        }
        zipInputStream.closeEntry();
        zipInputStream.close();
    }

    public static void zipFileByZipFile(String zipPath) throws IOException {
        File NativeZipFile = new File(zipPath);
        String newOuterPath = NativeZipFile.getParent() + File.separator + NativeZipFile.getName().split("\\.")[0];
        ZipFile zipFile = new ZipFile(zipPath);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        for (Iterator<? extends ZipEntry> it = entries.asIterator(); it.hasNext(); ) {
            ZipEntry zipEntry = it.next();
            String newPath = newOuterPath + File.separator + NativeZipFile.getName();
            File file =  new File(newPath);
            if(zipEntry.isDirectory()){
                file.mkdirs();
                //文件
            }else{
                String parent = file.getParent();
                File outerFolder = new File(parent);
                if (!outerFolder.exists()){
                    outerFolder.mkdirs();
                }
                file.createNewFile();
                InputStream inputStream = zipFile.getInputStream(zipEntry);
                BufferedOutputStream bufferedInputStream =  new BufferedOutputStream(new FileOutputStream(file));
                bufferedInputStream.flush();
                bufferedInputStream.close();
            }
        }
    }
}
