package com.cnwy.crawler.util;

import com.vaadin.flow.component.upload.UploadI18N;

import java.util.Arrays;

public class UploadChinaI18N extends UploadI18N {
    public UploadChinaI18N() {
        setDropFiles(new DropFiles().setOne("将文件拖放到这里")
                .setMany("将文件拖放到这里"));
        setAddFiles(new AddFiles().setOne("选择文件...")
                .setMany("选择文件..."));
        setError(new Error().setTooManyFiles("选择的文件过多.")
                .setFileIsTooBig("文件过大.")
                .setIncorrectFileType("不支持的文件类型."));
        setUploading(new Uploading()
                .setStatus(new Uploading.Status().setConnecting("连接中...")
                        .setStalled("等待")
                        .setProcessing("正在上传...").setHeld("等待中"))
                .setRemainingTime(new Uploading.RemainingTime()
                        .setPrefix("剩余时间: ")
                        .setUnknown("剩余时间不确定"))
                .setError(new Uploading.Error()
                        .setServerUnavailable(
                                "上传失败,请重试一次.")
                        .setUnexpectedServerError(
                                "由于服务器错误，上传失败")
                        .setForbidden("上传被禁止")));
        setUnits(new Units().setSize(Arrays.asList("B", "kB", "MB", "GB", "TB",
                "PB", "EB", "ZB", "YB")));
    }
}