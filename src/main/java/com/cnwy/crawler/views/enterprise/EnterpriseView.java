package com.cnwy.crawler.views.enterprise;

import com.cnwy.crawler.services.EnterpriseService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@AnonymousAllowed
@PageTitle("企业详情")
@Route(value = "enterprise/:enterpriseID")
public class EnterpriseView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final EnterpriseService enterpriseService;
    private Long enterpriseID;

    public EnterpriseView(EnterpriseService enterpriseService) {
        this.enterpriseService = enterpriseService;
        log.error("对象出实话完成");
    }

    private void setup() {
        enterpriseService.get(enterpriseID).ifPresent(enterprise -> {
            VerticalLayout layoutColumn2 = new VerticalLayout();
            H2 name = new H2(enterprise.getName());

            HorizontalLayout layoutTags = new HorizontalLayout();
            if (enterprise.getTags()!=null){
                Arrays.stream(enterprise.getTags().split(",")).forEach(tag -> {
                    Span badge = new Span(tag);
                    badge.getElement().getThemeList().add("badge small");
                    layoutTags.add(badge);
                });
            }
            HorizontalLayout layoutHref = new HorizontalLayout();
            if (enterprise.getQccID()!=null){
                Anchor anchor = new Anchor("https://www.qcc.com/firm/%s.html".formatted(enterprise.getQccID()), "跳转企查查");
                layoutHref.add(anchor);
            }
            if (enterprise.getTycID()!=null){
                Anchor anchor = new Anchor("https://www.qcc.com/firm/%s.html".formatted(enterprise.getTycID()), "跳转天眼查");
                layoutHref.add(anchor);
            }


            FormLayout formLayout2Col = new FormLayout();
            formLayout2Col.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("0", 1),
                    new FormLayout.ResponsiveStep("500px", 4)
            );

            TextField unifiedSocialCreditCode = new TextField();
            unifiedSocialCreditCode.setLabel("统一社会信用代码");
            unifiedSocialCreditCode.setValue(enterprise.getUnifiedSocialCreditCode());
            unifiedSocialCreditCode.setReadOnly(true);
            formLayout2Col.add(unifiedSocialCreditCode);
            formLayout2Col.setColspan(unifiedSocialCreditCode,2);

            TextField status = new TextField();
            status.setLabel("经营状态");
            status.setValue(enterprise.getStatus());
            status.setReadOnly(true);
            formLayout2Col.add(status);

            TextField type = new TextField();
            type.setLabel("央国企类型");
            type.setValue(Optional.ofNullable(enterprise.getType()).orElse(""));
            type.setReadOnly(true);
            formLayout2Col.add(type);

            TextField alias = new TextField();
            alias.setLabel("别名/简称");
            alias.setValue(Optional.ofNullable(enterprise.getAlias()).orElse(""));
            alias.setReadOnly(true);
            formLayout2Col.add(alias);

            TextField email = new TextField();
            email.setLabel("电子邮箱");
            email.setValue(Optional.ofNullable(enterprise.getEmail()).orElse(""));
            email.setReadOnly(true);
            formLayout2Col.add(email);

            TextField telephone = new TextField();
            telephone.setLabel("联系电话");
            telephone.setValue(Optional.ofNullable(enterprise.getTelephone()).orElse(""));
            telephone.setReadOnly(true);
            formLayout2Col.add(telephone);

            TextField regNo = new TextField();
            regNo.setLabel("工商注册号");
            regNo.setValue(Optional.ofNullable(enterprise.getRegNo()).orElse(""));
            regNo.setReadOnly(true);
            formLayout2Col.add(regNo);

            TextField gw = new TextField();
            gw.setLabel("官网");
            gw.setValue(enterprise.getWebsite());
            gw.setReadOnly(true);
            formLayout2Col.add(gw);
            formLayout2Col.setColspan(gw,2);

            TextField orgType = new TextField();
            orgType.setLabel("组织机构类型");
            orgType.setValue(enterprise.getOrgType());
            orgType.setReadOnly(true);
            formLayout2Col.add(orgType);
            formLayout2Col.setColspan(orgType,2);

            TextField province = new TextField();
            province.setLabel("所在省");
            province.setValue(Optional.ofNullable(enterprise.getProvince()).orElse(""));
            province.setReadOnly(true);
            formLayout2Col.add(province);

            TextField city = new TextField();
            city.setLabel("所在市");
            city.setValue(Optional.ofNullable(enterprise.getCity()).orElse(""));
            city.setReadOnly(true);
            formLayout2Col.add(city);

            TextField district = new TextField();
            district.setLabel("所在区/县");
            district.setValue(Optional.ofNullable(enterprise.getDistrict()).orElse(""));
            district.setReadOnly(true);
            formLayout2Col.add(district);
            formLayout2Col.add(new Div());

            TextField address = new TextField();
            address.setLabel("详细地址");
            address.setValue(Optional.ofNullable(enterprise.getAddress()).orElse(""));
            address.setReadOnly(true);
            formLayout2Col.add(address);
            formLayout2Col.setColspan(address,4);

            TextArea description = new TextArea();
            description.setLabel("简介");
            description.setValue(Optional.ofNullable(enterprise.getDescription()).orElse(""));
            description.setReadOnly(true);
            description.addClassName("no-line");
            formLayout2Col.add(description);
            formLayout2Col.setColspan(description,4);

            DatePicker datePicker = new DatePicker();
            TextField textField3 = new TextField();
            EmailField emailField = new EmailField();
            TextField textField4 = new TextField();
            HorizontalLayout layoutRow = new HorizontalLayout();
            Button buttonPrimary = new Button();
            Button buttonSecondary = new Button();
            getContent().setWidth("100%");
            getContent().getStyle().set("flex-grow", "1");
            getContent().setJustifyContentMode(JustifyContentMode.START);
            getContent().setAlignItems(Alignment.CENTER);
            layoutColumn2.setWidth("100%");
            layoutColumn2.setMaxWidth("800px");
            layoutColumn2.setHeight("min-content");
            name.setWidth("100%");
            formLayout2Col.setWidth("100%");
            datePicker.setLabel("Birthday");
            textField3.setLabel("Phone Number");
            emailField.setLabel("Email");
            textField4.setLabel("Occupation");
            layoutRow.addClassName(Gap.MEDIUM);
            layoutRow.setWidth("100%");
            layoutRow.getStyle().set("flex-grow", "1");
            buttonPrimary.setText("Save");
            buttonPrimary.setWidth("min-content");
            buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            buttonSecondary.setText("Cancel");
            buttonSecondary.setWidth("min-content");
            getContent().add(layoutColumn2);
            layoutColumn2.add(name);
            layoutColumn2.add(layoutTags);
            layoutColumn2.add(layoutHref);

            layoutColumn2.add(formLayout2Col);
//            formLayout2Col.add(unifiedSocialCreditCode);
//            formLayout2Col.add(type);
//            formLayout2Col.add(datePicker);
//            formLayout2Col.add(textField3);
//            formLayout2Col.add(emailField);
//            formLayout2Col.add(textField4);
//            layoutColumn2.add(layoutRow);
//            layoutRow.add(buttonPrimary);
//            layoutRow.add(buttonSecondary);
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        enterpriseID = beforeEnterEvent.getRouteParameters().getLong("enterpriseID").get();
        setup();
    }
}