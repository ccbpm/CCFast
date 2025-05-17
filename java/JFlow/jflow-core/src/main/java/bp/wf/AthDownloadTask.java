package bp.wf;

import bp.da.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载附件的线程类
 */
public class AthDownloadTask implements Runnable {
    private final String fileUrl;
    private final String tempPath;

    public AthDownloadTask(String fileUrl, String tempPath) {
        this.fileUrl = fileUrl;
        this.tempPath = tempPath;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // 注意：这里不应该设置con.setRequestMethod(con.getRequestMethod());，因为默认就是GET方法
            // 除非你需要显式地改变它（比如设置为POST）
            con.setRequestProperty("User-Agent", "Mozilla/4.76");
            con.setRequestProperty("connection", "keep-alive");

            BufferedInputStream in = new BufferedInputStream(con.getInputStream());
            File file = new File(tempPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }

            in.close();
            bos.close();
            System.out.println("File downloaded to: " + tempPath);

        } catch (Exception e) {
            e.printStackTrace();
            Log.DebugWriteError("err@下载附件出现错误");
        }
    }
}