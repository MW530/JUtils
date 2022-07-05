package priv.mw.entity;

/**
 * @Auther: MichaelWang
 * @Date: 2022/7/4 17:18
 * @Description: FileInfo
 * @Version 1.0.0
 */
public class FileInfo {
    private String name;
    private String url;

    public FileInfo(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
