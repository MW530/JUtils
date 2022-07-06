package priv.mw.utils;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.jetbrains.annotations.NotNull;
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
     * @Param fileInfos: 要压缩的文件地址
     * @Param compressionLevel: 压缩率
     * @Param filePath: 压缩文件目标地址
     * @Return void
     * @Version 1.0.0
    **/
    public static void compressZipFilesByZipStream(ArrayList<String> fileUrls, Integer compressionLevel, String filePath) throws IOException {
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

    /***
     * @Author MichaelWang
     * @Date  2022/7/6
     * @Description 给zipOutputStream放每个文件的entry。
     * @Param zipOutputStream: 压缩文件流
     * @Param file: 被放入的文件
     * @Param basePath: 外围文件夹的地址（主要是entry的名字就包含了文件夹的层级，所以需要递归放入）
     * @Return void
     * @Version 1.0.0
    **/
    private static void putEntry(ZipOutputStream zipOutputStream, @NotNull File file, String basePath) throws IOException {
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

    /*** 
     * @Author MichaelWang 
     * @Date  2022/7/6
     * @Description 通过ZipInputStream解压文件
     * @Param zipPath: 压缩文件地址
     * @Return void
     * @Version 1.0.0
    **/
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

    /***
     * @Author MichaelWang
     * @Date  2022/7/6
     * @Description 通过Java自带的ZipFile类实现文件解压缩
     * @Param zipPath: 压缩文件目标地址
     * @Return void
     * @Version 1.0.0
    **/
    public static void  extractZipFileByZipFile(String zipPath) throws IOException {
        File NativeZipFile = new File(zipPath);
        String newOuterPath = NativeZipFile.getParent() + File.separator + NativeZipFile.getName().split("\\.")[0];
        ZipFile zipFile = new ZipFile(zipPath);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        for (Iterator<? extends ZipEntry> it = entries.asIterator(); it.hasNext(); ) {
            ZipEntry zipEntry = it.next();
            String newPath = newOuterPath + File.separator + zipEntry.getName();
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
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(inputStream.readAllBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
    }

    /***
     * @Author MichaelWang
     * @Date  2022/7/6
     * @Description 压缩文件逻辑
     * @Param zipFile: 被压缩的文件
     * @Param fileUrls: 被压缩文件列表
     * @Param compressLevel: 压缩率
     * @Return void
     * @Version 1.0.0
    **/
    private static void _compressZipFileByZip4j(net.lingala.zip4j.ZipFile zipFile, @NotNull ArrayList<String> fileUrls, CompressionLevel compressLevel) throws ZipException {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_128);
        zipParameters.setCompressionLevel(compressLevel);

        for (String fileUrl : fileUrls) {
            zipFile.addFile(fileUrl, zipParameters);
        }
    }

    /***
     * @Author MichaelWang
     * @Date  2022/7/6
     * @Description 通过zip4j进行无密码的压缩
     * @Param zipPath: 压缩文件目标地址
     * @Param fileUrls: 被压缩文件的列表
     * @Param compressLevel:  压缩率
     * @Return void
     * @Version 1.0.0
    **/
    public static void compressZipFileByZip4j(String zipPath, ArrayList<String> fileUrls, CompressionLevel compressLevel) throws ZipException {
        net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(zipPath);
        _compressZipFileByZip4j(zipFile, fileUrls, compressLevel);
    }

    /***
     * @Author MichaelWang
     * @Date  2022/7/6
     * @Description 通过zip4j进行有密码压缩
     * @Param zipPath: 压缩文件目标的地址
     * @Param fileUrls: 要被压缩的文件列表
     * @Param compressLevel: 压缩率
     * @Param password: 密码
     * @Return void
     * @Version 1.0.0
    **/
    public static void compressZipFileByZip4j(String zipPath, ArrayList<String> fileUrls, CompressionLevel compressLevel, @NotNull String password) throws ZipException {
        net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(zipPath, password.toCharArray());
        _compressZipFileByZip4j(zipFile, fileUrls, compressLevel);
    }

    /***
     * @Author MichaelWang
     * @Date  2022/7/6
     * @Description 通过zip4j进行无密码压缩，然后重命名被压缩的文件
     * @Param zipPath: 压缩文件目标的地址
     * @Param fileUrls: 要被压缩的文件列表
     * @Param compressLevel: 压缩率
     * @Param renames: 重命名map，<originalName, newName>
     * @Return void
     * @Version 1.0.0
    **/
    public static void compressZipFileByZip4j(String zipPath, ArrayList<String> fileUrls, CompressionLevel compressLevel, Map<String, String> renames) throws ZipException {
        net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(zipPath);
        _compressZipFileByZip4j(zipFile, fileUrls, compressLevel);
        zipFile.renameFiles(renames);
    }

    /***
     * @Author MichaelWang
     * @Date  2022/7/6 通过zip4j进行有密码压缩，然后重命名被压缩的文件
     * @Description
     * @Param zipPath: 压缩文件目标的地址
     * @Param fileUrls: 要被压缩的文件列表
     * @Param compressLevel: 压缩率
     * @Param password: 密码
     * @Param renames: 重命名map，<originalName, newName>
     * @Return void
     * @Version 1.0.0
    **/
    public static void compressZipFileByZip4j(String zipPath, ArrayList<String> fileUrls, CompressionLevel compressLevel, @NotNull String password, Map<String, String> renames) throws ZipException {
        net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(zipPath, password.toCharArray());
        _compressZipFileByZip4j(zipFile, fileUrls, compressLevel);
        zipFile.renameFiles(renames);
    }

    /***
     * @Author MichaelWang
     * @Date  2022/7/6
     * @Description 通过zip4j进行无密码解压
     * @Param zipPath: 压缩文件目标的地址
     * @Param targetPath: 解压释放目标地址
     * @Return void
     * @Version 1.0.0
    **/
    public static void extractZipFileByZip4j(String zipPath, String targetPath) throws ZipException {
        net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(zipPath);
        zipFile.extractAll(targetPath);
    }

    /***
     * @Author MichaelWang
     * @Date  2022/7/6
     * @Description 通过zip4j进行有密码解压
     * @Param zipPath: 压缩文件目标的地址
     * @Param targetPath: 解压释放目标地址
     * @Param password: 密码
     * @Return void
     * @Version 1.0.0
    **/
    public static void extractZipFileByZip4j(String zipPath, String targetPath, @NotNull String password) throws ZipException {
        net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(zipPath, password.toCharArray());
        zipFile.extractAll(targetPath);
    }

    /***
     * @Author MichaelWang
     * @Date  2022/7/6
     * @Description 通过zip4j进行无密码解压，并且只解压给出列表的文件
     * @Param zipPath: 压缩文件目标的地址
     * @Param targetPath: 解压释放目标地址
     * @Param extractFiles: 要解压的地址
     * @Return void
     * @Version 1.0.0
    **/
    public static void extractZipFileByZip4j(String zipPath, String targetPath, List<String> extractFiles) throws ZipException {
        net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(zipPath);
        for (String extractFile : extractFiles) {
            zipFile.extractFile(extractFile, targetPath);
        }
    }

    /***
     * @Author MichaelWang
     * @Date  2022/7/6
     * @Description 通过zip4j进行有密码解压，并且只解压给出列表的文件
     * @Param zipPath: 压缩文件目标的地址
     * @Param targetPath: 解压释放目标地址
     * @Param password: 密码
     * @Param extractFiles: 要解压的地址
     * @Return void
     * @Version 1.0.0
    **/
    public static void extractZipFileByZip4j(String zipPath, String targetPath, @NotNull String password, List<String> extractFiles) throws ZipException {
        net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(zipPath, password.toCharArray());
        for (String extractFile : extractFiles) {
            zipFile.extractFile(extractFile, targetPath);
        }
    }
}
