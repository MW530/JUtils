import org.junit.Test;
import priv.mw.entity.FileInfo;
import priv.mw.utils.CompressUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipInputStream;

/**
 * @Auther: MichaelWang
 * @Date: 2022/7/4 16:47
 * @Description: CompressTest
 * @Version 1.0.0
 */
public class CompressTest {

    @Test
    public void zipCompressTest() throws IOException {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("E:/xpan/panFile/2022-06-22/IMAGE-0/09bf02ccde1b865e5cebe60e7286b51c.jpg");
        strings.add("E:/xpan/panFile/2022-06-22/IMAGE-0/e8a58f92ba667757a3a392a692ad645a.jpg");
        strings.add("E:/xpan/panFile/2022-06-22/UNSUPPORTED-0");
        CompressUtils.compressZipFilesByZipStream(strings, 2, "E:/xpan/panFile/2022-06-22/COMPRESSS/aa.zip");
    }

    @Test
    public void zipInputStreamTest() throws IOException {
        File file = new File("E:\\xpan\\panFile\\2022-06-22\\COMPRESSS\\COMPRESSS.zip");
        CompressUtils.extractZipByStream("E:\\xpan\\panFile\\2022-06-22\\COMPRESSS\\COMPRESSS.zip");
    }

    @Test
    public void zipFileTest() throws IOException {
        File file = new File("E:\\xpan\\panFile\\2022-06-22\\COMPRESSS\\COMPRESSS.zip");
        CompressUtils.extractZipFileByZipFile("E:\\xpan\\panFile\\2022-06-22\\COMPRESSS\\aa.zip");
    }
}
