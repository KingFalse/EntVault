package com.cnwy.crawler.util;

import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.messages.MessageInputI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class VaadinUtil {

    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static MessageInputI18n messageInputChineseI18n() {
        MessageInputI18n messageInputI18n = new MessageInputI18n();
        messageInputI18n.setMessage("输入关键词...");
        messageInputI18n.setSend("添加");
        return messageInputI18n;
    }

    public static void alertSuccess(String msg) {
        alert(msg).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private static Notification alert(String msg) {
        return Notification.show(msg, 3000, Notification.Position.TOP_END);
    }

    public static void alertError(String msg) {
        alert(msg).addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    public static DatePicker createDatePicker() {
        DatePicker datePicker = new DatePicker();
        datePicker.setI18n(datePickerI18nCN());
        return datePicker;
    }

    public static CrudI18n crudI18nCN() {
        CrudI18n i18n = CrudI18n.createDefault();

        i18n.setNewItem("新建");
        i18n.setEditItem("编辑");
        i18n.setSaveItem("保存");
        i18n.setCancel("取消");
        i18n.setDeleteItem("删除...");
        i18n.setEditLabel("编辑");

        CrudI18n.Confirmations.Confirmation delete = i18n.getConfirm().getDelete();
        delete.setTitle("删除项目");
        delete.setContent("您确定要删除此项目吗？删除后无法撤销。");
        delete.getButton().setConfirm("删除");
        delete.getButton().setDismiss("取消");

        CrudI18n.Confirmations.Confirmation cancel = i18n.getConfirm().getCancel();
        cancel.setTitle("放弃更改");
        cancel.setContent("该项目有未保存的更改。");
        cancel.getButton().setConfirm("放弃");
        cancel.getButton().setDismiss("取消");
        return i18n;
    }

    public static LoginI18n loginI18nCN() {
        LoginI18n i18n = LoginI18n.createDefault();
        LoginI18n.Header i18nHeader = new LoginI18n.Header();
        i18nHeader.setTitle("微信爬虫平台");
        i18nHeader.setDescription("crawler ❤️ wechat");
        i18n.setHeader(i18nHeader);

        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setTitle("登录");
        i18nForm.setUsername("用户名");
        i18nForm.setPassword("密码");
        i18nForm.setSubmit("登录");
        i18nForm.setForgotPassword("忘记密码？");
        i18n.setForm(i18nForm);

        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle("错误的用户名或密码");
        i18nErrorMessage.setMessage("检查用户名和密码是否正确，然后重试。");
        i18n.setErrorMessage(i18nErrorMessage);

        i18n.setAdditionalInformation(null);
        return i18n;
    }

    public static DatePicker.DatePickerI18n datePickerI18nCN() {
        DatePicker.DatePickerI18n datePickerI18nCN = new DatePicker.DatePickerI18n();
        datePickerI18nCN.setMonthNames(List.of("一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"));
        datePickerI18nCN.setWeekdays(List.of("周日", "周一", "周二", "周三", "周四", "周五", "周六"));
        datePickerI18nCN.setWeekdaysShort(List.of("周日", "周一", "周二", "周三", "周四", "周五", "周六"));
        datePickerI18nCN.setFirstDayOfWeek(1);
        datePickerI18nCN.setDateFormat("yyyy-MM-dd");
        datePickerI18nCN.setToday("今天");
        datePickerI18nCN.setCancel("取消");
        return datePickerI18nCN;
    }


}
