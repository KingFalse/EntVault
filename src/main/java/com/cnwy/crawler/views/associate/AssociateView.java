package com.cnwy.crawler.views.associate;

import com.alibaba.fastjson2.JSON;
import com.cnwy.crawler.data.AssociateGW;
import com.cnwy.crawler.data.Enterprise;
import com.cnwy.crawler.data.EnterpriseRepository;
import com.cnwy.crawler.services.AssociateService;
import com.cnwy.crawler.util.VaadinUtil;
import com.cnwy.crawler.views.MainLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RJsonBucket;
import org.redisson.api.RSearch;
import org.redisson.api.RedissonClient;
import org.redisson.api.search.index.FieldIndex;
import org.redisson.api.search.index.IndexOptions;
import org.redisson.api.search.index.IndexType;
import org.redisson.api.search.query.QueryOptions;
import org.redisson.api.search.query.ReturnAttribute;
import org.redisson.api.search.query.SearchResult;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JacksonCodec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@PageTitle("官网自动绑定")
@Route(value = "associate", layout = MainLayout.class)
@AnonymousAllowed
public class AssociateView extends VerticalLayout {

    final
    AssociateService associateService;

    final
    EnterpriseRepository enterpriseRepository;

    final
    RedissonClient redissonClient;

    Dialog dialogLoading = null;
    Dialog dialogResult = new Dialog();

    public AssociateView(AssociateService associateService, EnterpriseRepository enterpriseRepository, RedissonClient redissonClient) {
        this.associateService = associateService;
        setMargin(true);
        setSpacing(false);

        Image img = new Image("images/Jenkins.svg", "placeholder plant");
        img.setWidth("200px");
        add(img);

        H2 header = new H2("请输入需要绑定的链接🤗");
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        add(header);
        add(new Paragraph(""));

        TextField start = new TextField();
        start.setPlaceholder("请输入需要绑定的链接...");
        start.setClearButtonVisible(true);
        start.setPrefixComponent(VaadinIcon.THUMBS_UP_O.create());
        start.setSuffixComponent(new Span(":)"));
        start.setWidth("60vw");
        start.setAutofocus(true);
        start.addKeyPressListener(Key.ENTER, keyPressEvent -> {
            showLoading();
            if (!start.getValue().startsWith("http")) {
                Notification notification = new Notification("似乎输入的不是正确的链接...", 5000, Notification.Position.TOP_END);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();
                start.focus();
                hideLoading();
                return;
            }
            UI current = UI.getCurrent();
            CompletableFuture.runAsync(() -> {
                try {
                    AssociateGW associateGW = associateService.doAssociate(start.getValue().strip());
                    current.access((Command) () -> {
                        hideLoading();
                        Notification notification = new Notification("自动绑定企业成功!" + associateGW.getReason(), 8000, Notification.Position.TOP_END);
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        notification.open();
                        showResult(associateGW);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    current.access((Command) () -> {
                        hideLoading();
                        Notification notification = new Notification("自动绑定失败!" + e.getMessage(), 8000, Notification.Position.TOP_END);
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        notification.open();
                    });
                }
            });
        });

        add(start);

        Button button = new Button("Test");
        add(button);
        button.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                // https://github.com/redisson/redisson/wiki/9.-distributed-services/#96-redisearch-service

                CompletableFuture.runAsync(()->{
                    int pageSize = 100;
                    int currentPage = 0;
                    Pageable pageable = PageRequest.of(currentPage, pageSize);

                    Page<Enterprise> page;
                    do {
                        page = enterpriseRepository.findAll(pageable);
                        List<Enterprise> entities = page.getContent();

                        for (Enterprise enterprise : entities) {
                            log.info("正在添加企业:" + enterprise.getName());
                            RJsonBucket<Enterprise> b = redissonClient.getJsonBucket("doc:" + enterprise.getId(), new JacksonCodec<>(Enterprise.class));
                            b.set(enterprise);
                        }

                        currentPage++;
                        pageable = PageRequest.of(currentPage, pageSize);
                    } while (page.hasNext());
                });


//                Page<Enterprise> all = enterpriseRepository.findAll();
//                for (Enterprise enterprise : all) {
//                    log.info("正在添加企业:" + enterprise.getName());
//                    RJsonBucket<Enterprise> b = redissonClient.getJsonBucket("doc:" + enterprise.getId(), new JacksonCodec<>(Enterprise.class));
//                    b.set(enterprise);
//                }
//
//                RSearch s = redissonClient.getSearch(StringCodec.INSTANCE);
//                s.dropIndex("idx");
//                s.createIndex("idx", IndexOptions.defaults()
//                                .on(IndexType.JSON)
//                                .prefix(List.of("doc:")),
////                        FieldIndex.numeric("$..arr").as("arr"),
////                        FieldIndex.text("$..value").as("val"));
//                        FieldIndex.text("$.name"));

//                SearchResult r = s.search("idx", "*", QueryOptions.defaults().returnAttributes(new ReturnAttribute("arr"), new ReturnAttribute("val")));
//                RSearch s = redissonClient.getSearch(StringCodec.INSTANCE);
//                SearchResult r = s.search("idx", "*象山*", QueryOptions.defaults());
//                log.info("---" + JSON.toJSONString(r));
            }
        });

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        this.enterpriseRepository = enterpriseRepository;
        this.redissonClient = redissonClient;
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
