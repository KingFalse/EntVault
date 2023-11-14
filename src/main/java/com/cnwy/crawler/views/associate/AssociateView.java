package com.cnwy.crawler.views.associate;

import com.cnwy.crawler.data.AssociateGW;
import com.cnwy.crawler.services.AssociateService;
import com.cnwy.crawler.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import java.util.List;

@PageTitle("官网自动绑定")
@Route(value = "associate")
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class AssociateView extends VerticalLayout {

    final
    AssociateService associateService;

    Dialog dialogLoading = new Dialog();
    Dialog dialogResult = new Dialog();

    public AssociateView(AssociateService associateService) {
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
            if (!start.getValue().startsWith("http")) {
                Notification notification = new Notification("似乎输入的不是正确的链接...", 5000, Notification.Position.TOP_END);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();
                start.focus();
                return;
            }
            showLoading();
            try {
                AssociateGW associateGW = associateService.doAssociate(start.getValue().strip());
                dialogLoading.close();
                Notification notification = new Notification("自动绑定企业成功!" + associateGW.getReason(), 8000, Notification.Position.TOP_END);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.open();
                showResult(associateGW);
            } catch (Exception e) {
                e.printStackTrace();
                dialogLoading.close();
                Notification notification = new Notification("自动绑定失败!" + e.getMessage(), 8000, Notification.Position.TOP_END);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();
            }
        });

        add(start);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    private void showLoading() {
        dialogLoading.setCloseOnEsc(false);
        dialogLoading.setCloseOnOutsideClick(false);
        dialogLoading.setWidth("80vw");
        dialogLoading.setHeaderTitle("正在自动绑定中...");

        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        dialogLoading.add(progressBar);

        dialogLoading.open();
    }

    private void showResult(AssociateGW associateGW) {
        dialogResult.setWidth("80vw");
        dialogResult.setHeaderTitle("绑定成功");

        Grid<AssociateGW> grid = new Grid<>();
        grid.addColumn(AssociateGW::getDomain).setHeader("域名");
        grid.addColumn(AssociateGW::getEnterpriseID).setHeader("企业ID");
        grid.addColumn(AssociateGW::getReason).setHeader("绑定原因");
        grid.addColumn(AssociateGW::getCreateTime).setHeader("操作时间");
        grid.setItems(List.of(associateGW));
        dialogResult.add(grid);

        dialogResult.open();
    }

}
