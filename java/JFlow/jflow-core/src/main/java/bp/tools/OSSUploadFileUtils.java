package bp.tools;

import bp.da.DataType;
import bp.da.Log;
import bp.difference.SystemConfig;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import com.aliyun.oss.model.GetObjectRequest;

import java.io.*;

/**
 * @date 2022年
 * @describe
 */

public class OSSUploadFileUtils implements ProgressListener {
    // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
    private static final String accessKeyId = SystemConfig.getAppSettings().get("OSSAccessKeyId").toString();
    private static final String accessKeySecret = SystemConfig.getAppSettings().get("OSSAccessKeySecret").toString();
    // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
    private static final String endpoint = SystemConfig.getAppSettings().get("OSSEndpoint").toString();
    // 填写Bucket名称，例如examplebucket。
    private static final String bucketName = SystemConfig.getAppSettings().get("OSSBucketName").toString();

    /*上传下载进度条参数*/
    private long bytesRead = 0;
    private long totalBytes = -1;
    private boolean succeed = false;

    /**
     * 检查OSS服务的配置是否完整
     */
    private static void CheckOSSConfig() throws Exception {
        if (DataType.IsNullOrEmpty(bp.difference.SystemConfig.getOSSEndpoint()))
            throw new Exception("err@检测到没有配置OSSEndpoint，请您配置OSSEndpoint后重新上传文件");
        if (DataType.IsNullOrEmpty(bp.difference.SystemConfig.getOSSAccessKeyId()))
            throw new Exception("err@检测到没有配置OSSAccessKeyId，请您配置OSSAccessKeyId后重新上传文件");
        if (DataType.IsNullOrEmpty(bp.difference.SystemConfig.getOSSAccessKeySecret()))
            throw new Exception("err@检测到没有配置OSSAccessKeySecret，请您配置OSSAccessKeySecret后重新上传文件");
        if (DataType.IsNullOrEmpty(bp.difference.SystemConfig.getOSSBucketName()))
            throw new Exception("err@检测到没有配置OSSBucketName，请您配置OSSBucketName后重新上传文件");
    }

    /**
     * 通过InputStream输入流的方式上传文件
     *
     * @param fileName    上传到服务器的文件名
     * @param inputStream 文件的输入流
     */
    public static void uploadFile(String fileName, InputStream inputStream) throws Exception {
        //检查配置是否完整
        CheckOSSConfig();
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        if(fileName.startsWith("/"))
            fileName = fileName.substring(1);

        fileName = fileName.replace(File.separatorChar, '/');
        try {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, fileName, inputStream);
        } catch (OSSException oe) {
            String err = "捕获到OSSException，这意味着您的请求发送到OSS，但由于某种原因被错误响应拒绝。";
            err += "\nError Message:" + oe.getErrorMessage();
            err += "\nError Code:" + oe.getErrorCode();
            err += "\nRequest ID:" + oe.getRequestId();
            err += "\nHost ID:" + oe.getHostId();
            Log.DebugWriteError(err);
            throw oe;
        } catch (ClientException ce) {
            String err = "捕获到ClientException，这意味着客户端在尝试与OSS通信时遇到了严重的内部问题，例如无法访问网络。";
            err += "\nError Message:" + ce.getMessage();
            Log.DebugWriteError(err);
            throw ce;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 从阿里云下载文件到本地
     *
     * @param filename 阿里云对象存储的文件的名字
     * @param filePath 下载到本地的路径，路径包括文件名字
     * @return
     * @throws IOException
     */
    public static void downloadFile(String filename, String filePath) throws Exception {
        //检查配置是否完整
        CheckOSSConfig();
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        if(filename.startsWith("/"))
            filename = filename.substring(1);
        filename = filename.replace(File.separatorChar, '/');
        try {
            // 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
            // 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
            ossClient.getObject(new GetObjectRequest(bucketName, filename).
                            <GetObjectRequest>withProgressListener(new OSSUploadFileUtils()),
                    new File(filePath));
        } catch (OSSException oe) {
            String err = "捕获到OSSException，这意味着您的请求发送到OSS，但由于某种原因被错误响应拒绝。";
            err += "\nError Message:" + oe.getErrorMessage();
            err += "\nError Code:" + oe.getErrorCode();
            err += "\nRequest ID:" + oe.getRequestId();
            err += "\nHost ID:" + oe.getHostId();
            Log.DebugWriteError(err);
            throw new RuntimeException(err);
        } catch (ClientException ce) {
            String err = "捕获到ClientException，这意味着客户端在尝试与OSS通信时遇到了严重的内部问题，例如无法访问网络。";
            err += "\nError Message:" + ce.getMessage();
            Log.DebugWriteError(err);
            throw new RuntimeException(err);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 上传下载进度条的方法
     *
     * @param progressEvent
     */
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
            case TRANSFER_STARTED_EVENT:
                Log.DebugWriteInfo("开始下载......");
                break;
            case RESPONSE_CONTENT_LENGTH_EVENT:
                this.totalBytes = bytes;
                Log.DebugWriteInfo("总共 " + this.totalBytes + " 字节将下载到本地文件");
                //System.out.println(this.totalBytes + " bytes in total will be downloaded to a local file");
                break;
            case RESPONSE_BYTE_TRANSFER_EVENT:
                this.bytesRead += bytes;
                if (this.totalBytes != -1) {
                    int percent = (int) (this.bytesRead * 100.0 / this.totalBytes);
                    Log.DebugWriteInfo("已读取 " + bytes + " 字节 , 下载进度: " +
                            percent + "%(" + this.bytesRead + "/" + this.totalBytes + ")");
                } else {
                    Log.DebugWriteInfo("已读取 " + bytes + " , 下载比率：未知" +
                            "(" + this.bytesRead + "/...)");
                }
                break;
            case TRANSFER_COMPLETED_EVENT:
                this.succeed = true;
                Log.DebugWriteInfo("下载成功！ 总共已传输 " + this.bytesRead + " 字节");
                break;
            case TRANSFER_FAILED_EVENT:
                Log.DebugWriteInfo("下载失败！已传输 " + this.bytesRead + " 字节");
                break;
            default:
                break;
        }
    }

    public boolean isSucceed() {
        return succeed;
    }

    /**
     * 删除云端文件
     *
     * @param fileName 要删除的文件名字
     */
    public static void deleteFile(String fileName) throws Exception {
        //检查配置是否完整
        CheckOSSConfig();
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 删除文件或目录。如果要删除目录，目录必须为空。
            ossClient.deleteObject(bucketName, fileName);
        } catch (OSSException oe) {
            String err = "捕获到OSSException，这意味着您的请求发送到OSS，但由于某种原因被错误响应拒绝。";
            err += "\nError Message:" + oe.getErrorMessage();
            err += "\nError Code:" + oe.getErrorCode();
            err += "\nRequest ID:" + oe.getRequestId();
            err += "\nHost ID:" + oe.getHostId();
            Log.DebugWriteError(err);
            throw new RuntimeException(err);
        } catch (ClientException ce) {
            String err = "捕获到ClientException，这意味着客户端在尝试与OSS通信时遇到了严重的内部问题，例如无法访问网络。";
            err += "\nError Message:" + ce.getMessage();
            Log.DebugWriteError(err);
            throw new RuntimeException(err);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}