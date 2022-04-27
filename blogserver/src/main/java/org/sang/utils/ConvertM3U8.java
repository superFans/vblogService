package org.sang.utils;


import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * mp4转换m3u8工具类
 * https://blog.csdn.net/qq_42910468/article/details/113382135
 * @author: Czw
 * @create: 2021-01-26 14:19
 **/
@Slf4j
@Component
public class ConvertM3U8 {
    // ffmPeg.exe的目录
    private final String ffmPegPath = "E:\\百度网盘下载\\ffmpeg-4.3.1-2020-10-01-full_build\\ffmpeg-4.3.1-2020-10-01-full_build\\bin\\ffmpeg";

    public boolean convertOss(String folderUrl, String fileName) {
        if (!checkFile(folderUrl + fileName)) {
            System.out.println("文件不存在!");
            return false;
        }

        //验证文件后缀
        String suffix = StringUtils.substringAfter(fileName, ".");
        String fileFullName = StringUtils.substringBefore(fileName, ".");
        if (!validFileType(suffix)) {
            return false;
        }
        return processM3U8(folderUrl, fileName, fileFullName);
    }

    /**
     * 验证上传文件后缀
     *
     * @param type 类型
     * @return 是否为mp4
     */
    private boolean validFileType(String type) {
        return "mp4".equals(type);
    }

    /**
     * 验证是否是文件格式
     *
     * @param path 路径
     * @return 是否存在
     */
    private boolean checkFile(String path) {
        File file = new File(path);
        return file.isFile();
    }

    //

    /**
     * ffmPeg程序转换m3u8
     * ffmPeg -i vue.mp4 -c:v libx264 -hls_time 20 -hls_list_size 0 -c:a aac -strict -2 -f hls vue.m3u8
     * ffmPeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
     *
     * @param folderUrl    文件路径
     * @param fileName     名称
     * @param fileFullName 全名
     * @return 是否成功
     */
    private boolean processM3U8(String folderUrl, String fileName, String fileFullName) {
        //这里就写入执行语句就可以了
        List<String> commend = new ArrayList<>();
        commend.add(ffmPegPath);
        commend.add("-i");
        commend.add(folderUrl + fileName);
        commend.add("-c:v");
        commend.add("libx264");
        commend.add("-hls_time");
        commend.add("20");
        commend.add("-hls_list_size");
        commend.add("0");
        commend.add("-c:a");
        commend.add("aac");
        commend.add("-strict");
        commend.add("-2");
        commend.add("-f");
        commend.add("hls");
        commend.add(folderUrl + fileFullName + ".m3u8");
        try {
            ProcessBuilder builder = new ProcessBuilder();//java
            builder.command(commend);
            Process p = builder.start();
            int i = doWaitFor(p);
            log.info("***i=【{}】***", i);
            p.destroy();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 监听ffmPeg运行过程
     *
     * @param p 进程
     * @return 直接结果
     */
    public int doWaitFor(Process p) {
        InputStream in = null;
        InputStream err = null;
        int exitValue = -1; // returned to caller when p is finished
        try {
            log.info("***检测ffmPeg运行***");
            in = p.getInputStream();
            err = p.getErrorStream();
            boolean finished = false; // Set to true when p is finished

            while (!finished) {
                try {
                    while (in.available() > 0) {
                        Character c = (char) in.read();
                        System.out.print(c);
                    }
                    while (err.available() > 0) {
                        Character c = (char) err.read();
                        System.out.print(c);
                    }

                    exitValue = p.exitValue();
                    finished = true;

                } catch (IllegalThreadStateException e) {
                    Thread.sleep(500);
                }
            }
        } catch (Exception e) {
            log.error("doWaitFor();: unexpected exception - "
                    + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            if (err != null) {
                try {
                    err.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return exitValue;
    }
}


