package com.cnwy.crawler.views.associate;

import com.cnwy.crawler.data.AssociateGW;
import com.cnwy.crawler.data.Enterprise;
import com.cnwy.crawler.data.EnterpriseRepository;
import com.cnwy.crawler.services.AssociateService;
import com.cnwy.crawler.util.VaadinUtil;
import com.cnwy.crawler.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@PageTitle("官网自动绑定")
@Route(value = "associate", layout = MainLayout.class)
@AnonymousAllowed
public class AssociateView extends VerticalLayout {

    final
    AssociateService associateService;

    final
    EnterpriseRepository enterpriseRepository;

    Dialog dialogLoading = null;
    Dialog dialogResult = new Dialog();

    public AssociateView(AssociateService associateService, EnterpriseRepository enterpriseRepository) {
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
            CompletableFuture.runAsync(()->{
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

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        this.enterpriseRepository = enterpriseRepository;
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
