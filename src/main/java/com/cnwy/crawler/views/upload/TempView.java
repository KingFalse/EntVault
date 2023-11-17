package com.cnwy.crawler.views.upload;

import com.cnwy.crawler.data.AssociateGW;
import com.cnwy.crawler.data.Enterprise;
import com.cnwy.crawler.data.EnterpriseRepository;
import com.cnwy.crawler.services.AssociateService;
import com.cnwy.crawler.services.ImportService;
import com.cnwy.crawler.util.VaadinUtil;
import com.cnwy.crawler.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@PageTitle("临时页面")
@Route(value = "temp", layout = MainLayout.class)
@AnonymousAllowed
public class TempView extends VerticalLayout {

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

    public TempView(AssociateService associateService, EnterpriseRepository enterpriseRepository, RedissonClient redissonClient, ImportService importService) {
        this.associateService = associateService;
        setMargin(true);
        setSpacing(false);

        Image img = new Image("images/Jenkins.svg", "placeholder plant");
        img.setWidth("200px");
        add(img);

        Button button = new Button("创建过滤器");
        button.addClickListener(event -> {
            // 创建企业库全局过滤器
            RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("--ENT-FULL-BLOOM-FILTER--", StringCodec.INSTANCE);
            bloomFilter.tryInit(500_0000, 0.0001);
            CompletableFuture.runAsync(() -> {
                int pageSize = 10000;
                int currentPage = 0;
                Pageable pageable = PageRequest.of(currentPage, pageSize);

                Page<Enterprise> page;
                do {
                    System.err.println("开始查询第:" + currentPage + "页");
                    page = enterpriseRepository.findAll(pageable);
                    List<Enterprise> entities = page.getContent();
                    List<String> ids = entities.stream().map(Enterprise::getQccID).toList();
                    bloomFilter.add(ids);
                    log.info("当前key总数:{}", bloomFilter.count());
                    currentPage++;
                    pageable = PageRequest.of(currentPage, pageSize);
                } while (page.hasNext());
                log.info("企业库全局过滤器初始化完成");
                log.info("当前key总数:{}", bloomFilter.count());
            });
        });
        add(button);


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
