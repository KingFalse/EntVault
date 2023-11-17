package com.cnwy.crawler.views.upload;

import com.cnwy.crawler.data.AssociateGW;
import com.cnwy.crawler.data.Enterprise;
import com.cnwy.crawler.data.EnterpriseRepository;
import com.cnwy.crawler.services.AssociateService;
import com.cnwy.crawler.services.ImportService;
import com.cnwy.crawler.util.UploadChinaI18N;
import com.cnwy.crawler.util.VaadinUtil;
import com.cnwy.crawler.views.MainLayout;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@PageTitle("数据上传")
@Route(value = "upload", layout = MainLayout.class)
@AnonymousAllowed
public class UploadView extends VerticalLayout {

    final
    AssociateService associateService;

    final
    EnterpriseRepository enterpriseRepository;

    final
    RedissonClient redissonClient;

    final
    ImportService importService;

    Dialog dialogLoading = null;
    Dialog dialogResult = new Dialog();

    public UploadView(AssociateService associateService, EnterpriseRepository enterpriseRepository, RedissonClient redissonClient, ImportService importService) {
        this.associateService = associateService;
        setMargin(true);
        setSpacing(false);

        Image img = new Image("images/Jenkins.svg", "placeholder plant");
        img.setWidth("200px");
        add(img);

//        H2 header = new H2("请输入需要绑定的链接🤗");
//        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
//        add(header);
//        add(new Paragraph(""));

        MemoryBuffer buffer = new MemoryBuffer();
        H4 title = new H4("上传导出的Excel");
        title.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        Paragraph hint = new Paragraph(
                "文件不能超过100M!");

        Upload upload = new Upload(buffer);
        int maxFileSizeInBytes = 100 * 1024 * 1024; // 100MB
        upload.setMaxFileSize(maxFileSizeInBytes);
        upload.setAcceptedFileTypes(
                // Microsoft Excel (.xls)
                "application/vnd.ms-excel", ".xls",
                // Microsoft Excel (OpenXML, .xlsx)
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"
                // Comma-separated values (.csv)
                // "text/csv", ".csv"
        );

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            Notification notification = Notification.show(errorMessage, 5000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        upload.addFinishedListener(event -> {
            log.info("上传完成,开始读取excel..." + event.getFileName());
            InputStream inputStream = buffer.getInputStream();
            CompletableFuture.runAsync(()->{
                importService.importExcel(inputStream);
            });

        });

        UploadChinaI18N i18n = new UploadChinaI18N();
        i18n.getAddFiles().setOne("选择需要上传的Excel...");
        i18n.getDropFiles().setOne("或者拖放到这里...");
        i18n.getError().setIncorrectFileType("请上传支持的后缀 (.xls, .xlsx).");
        upload.setI18n(i18n);

        add(title, hint, upload);


        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        this.enterpriseRepository = enterpriseRepository;
        this.redissonClient = redissonClient;
        this.importService = importService;
    }

    private void showLoading() {
        if (dialogLoading == null) {
            dialogLoading = new Dialog();
            dialogLoading.setCloseOnEsc(false);
            dialogLoading.setCloseOnOutsideClick(false);
            dialogLoading.setWidth("60vw");
            dialogLoading.setHeaderTitle("正在自动绑定中...");
            dialogLoading.removeAll();

            ProgressBar progressBar = new ProgressBar();
            progressBar.setIndeterminate(true);
            dialogLoading.add(progressBar);
        }
        dialogLoading.open();
    }

    private void hideLoading() {
        dialogLoading.close();
    }

    private void showResult(AssociateGW associateGW) {
        dialogResult.removeAll();
        dialogResult.setWidth("60vw");
        dialogResult.setHeaderTitle("绑定成功");
        Optional<Enterprise> ent = enterpriseRepository.findById(associateGW.getEnterpriseID());

        UnorderedList content = new UnorderedList(
                new ListItem("官网域名: " + associateGW.getDomain()),
                new ListItem("企业ID: " + associateGW.getEnterpriseID()),
                new ListItem("企业名称: " + ent.get().getName()),
                new ListItem("企业企查查链接: https://www.qcc.com/firm/" + ent.get().getQccID() + ".html"),
                new ListItem("绑定原因: " + associateGW.getReason()),
                new ListItem("操作时间: " + VaadinUtil.dateTimeFormatter.format(associateGW.getCreateTime()))
        );
        dialogResult.add(content);

        dialogResult.open();
    }


}
