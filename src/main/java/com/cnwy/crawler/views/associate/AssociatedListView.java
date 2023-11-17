package com.cnwy.crawler.views.associate;

import com.cnwy.crawler.data.AssociateGW;
import com.cnwy.crawler.services.AssociateGWService;
import com.cnwy.crawler.util.VaadinUtil;
import com.cnwy.crawler.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@PageTitle("已绑定官网列表")
@Route(value = "associated", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class AssociatedListView extends Div {

    private final AssociateGWService associateGWService;
    private final Filters filters;
    private Grid<AssociateGW> grid;

    public AssociatedListView(AssociateGWService associateGWService) {
        this.associateGWService = associateGWService;

        setSizeFull();
        addClassNames("enterprise-view");

        filters = new Filters(() -> refreshGrid());
//        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    private Component createGrid() {
        grid = new Grid<>(AssociateGW.class, false);
        grid.addColumn("id").setResizable(true).setHeader("ID").setAutoWidth(true);
        grid.addColumn("domain").setResizable(true).setHeader("官网二级域名").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(Anchor::new, (anchorDetail, associateGW) -> {
            anchorDetail.setText(associateGW.getEnterpriseID()+"");
            anchorDetail.setHref("enterprise/%s".formatted(associateGW.getEnterpriseID()));
            anchorDetail.setTarget(AnchorTarget.BLANK);
        })).setAutoWidth(true).setHeader("已绑定企业ID");
        grid.addColumn("reason").setResizable(true).setHeader("绑定原因").setAutoWidth(true);
        grid.addColumn(new LocalDateTimeRenderer<>(AssociateGW::getCreateTime, () -> VaadinUtil.dateTimeFormatter)).setHeader("操作时间").setSortable(true).setAutoWidth(true).setResizable(true);
        createOperateColumn();

        grid.setItems(query -> associateGWService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private void createOperateColumn() {
        grid.addColumn(new ComponentRenderer<>(HorizontalLayout::new, (horizontalLayout, associateGW) -> {
//            Anchor anchorQcc = new Anchor("https://www.qcc.com/firm/%s.html".formatted(associateGW.getId()), "企查查", AnchorTarget.BLANK);
//            Anchor anchorDetail = new Anchor("enterprise/%s".formatted(associateGW.getEnterpriseID()), "查看详情", AnchorTarget.BLANK);

            Button del = new Button(VaadinIcon.TRASH.create());
            del.addClickListener(event -> {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("确定要删除吗?");
                dialog.setText("确定要删除这条关联吗");

                dialog.setCancelable(true);

                dialog.setConfirmText("删除");
                dialog.setConfirmButtonTheme("error primary");
                dialog.addConfirmListener(ev -> {
                    associateGWService.delete(associateGW.getId());
                    grid.getDataProvider().refreshAll();
                });
                dialog.open();
            });

            horizontalLayout.add(del);
//            horizontalLayout.add(anchorQcc);
            horizontalLayout.setSizeFull();
        })).setKey("Operate").setWidth("10em").setHeader("操作");

    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
        alertCount();
    }

    private void alertCount() {
        UI current = UI.getCurrent();
        CompletableFuture.runAsync(() -> {
            Long count = associateGWService.count(filters);
            current.access((Command) () -> {
                Notification notification = new Notification("当前条件下企业总数为: " + count, 8000, Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.open();
            });
        });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        alertCount();
    }

    public static class Filters extends Div implements Specification<AssociateGW> {

        private final TextField name = new TextField("公司名称");
        private final TextField city = new TextField("城市");
        private final TextField district = new TextField("区县");
        private final TextField phone = new TextField("Phone");
        private final DatePicker startDate = new DatePicker("Date of Birth");
        private final DatePicker endDate = new DatePicker();
        private final MultiSelectComboBox<String> occupations = new MultiSelectComboBox<>("Occupation");
        private final CheckboxGroup<String> roles = new CheckboxGroup<>("Role");
        ComboBox<String> hitReason = new ComboBox<>("入选原因");

        public Filters(Runnable onSearch) {

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            name.setPlaceholder("请输入关键词...");

            occupations.setItems("Insurance Clerk", "Mortarman", "Beer Coil Cleaner", "Scale Attendant");
            hitReason.setItems("企查查搜索分类:事业单位", "企查查搜索分类:国有企业", "企查查搜索分类:机关单位");
            hitReason.setValue("");

            roles.setItems("Worker", "Supervisor", "Manager", "External");
            roles.addClassName("double-width");

            // Action buttons
            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                name.clear();
                city.clear();
                district.clear();
                hitReason.clear();
                phone.clear();
                startDate.clear();
                endDate.clear();
                occupations.clear();
                roles.clear();
                onSearch.run();
            });
            Button searchBtn = new Button("搜索");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

//            add(name, phone, createDateRangeFilter(), occupations, roles, actions);
            add(name, city, district, hitReason, actions);
        }

        private Component createDateRangeFilter() {
            startDate.setPlaceholder("From");

            endDate.setPlaceholder("To");

            // For screen readers
            startDate.setAriaLabel("From date");
            endDate.setAriaLabel("To date");

            FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

            return dateRangeComponent;
        }

        @Override
        public Predicate toPredicate(Root<AssociateGW> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!city.isEmpty()) {
                String cityFilter = city.getValue().strip();
                Predicate firstNameMatch = criteriaBuilder.equal(root.get("city"), cityFilter);
                predicates.add(criteriaBuilder.or(firstNameMatch));
            }
            if (!district.isEmpty()) {
                String cityFilter = district.getValue().strip();
                Predicate firstNameMatch = criteriaBuilder.equal(root.get("district"), cityFilter);
                predicates.add(criteriaBuilder.or(firstNameMatch));
            }
            if (!hitReason.getValue().isEmpty()) {
                String hitReasonFilter = hitReason.getValue().strip();
                Predicate firstNameMatch = criteriaBuilder.equal(root.get("hitReason"), hitReasonFilter);
                predicates.add(criteriaBuilder.or(firstNameMatch));
            }
            if (!name.isEmpty()) {
                String nameFilter = name.getValue().strip();
                Predicate firstNameMatch = criteriaBuilder.like(root.get("name"), "%" + nameFilter + "%");
                predicates.add(criteriaBuilder.or(firstNameMatch));
            }
//            if (!phone.isEmpty()) {
//                String databaseColumn = "phone";
//                String ignore = "- ()";
//
//                String lowerCaseFilter = ignoreCharacters(ignore, phone.getValue().toLowerCase());
//                Predicate phoneMatch = criteriaBuilder.like(
//                        ignoreCharacters(ignore, criteriaBuilder, criteriaBuilder.lower(root.get(databaseColumn))),
//                        "%" + lowerCaseFilter + "%");
//                predicates.add(phoneMatch);
//
//            }
//            if (startDate.getValue() != null) {
//                String databaseColumn = "dateOfBirth";
//                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(databaseColumn),
//                        criteriaBuilder.literal(startDate.getValue())));
//            }
//            if (endDate.getValue() != null) {
//                String databaseColumn = "dateOfBirth";
//                predicates.add(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.literal(endDate.getValue()),
//                        root.get(databaseColumn)));
//            }
//            if (!occupations.isEmpty()) {
//                String databaseColumn = "occupation";
//                List<Predicate> occupationPredicates = new ArrayList<>();
//                for (String occupation : occupations.getValue()) {
//                    occupationPredicates
//                            .add(criteriaBuilder.equal(criteriaBuilder.literal(occupation), root.get(databaseColumn)));
//                }
//                predicates.add(criteriaBuilder.or(occupationPredicates.toArray(Predicate[]::new)));
//            }
//            if (!roles.isEmpty()) {
//                String databaseColumn = "role";
//                List<Predicate> rolePredicates = new ArrayList<>();
//                for (String role : roles.getValue()) {
//                    rolePredicates.add(criteriaBuilder.equal(criteriaBuilder.literal(role), root.get(databaseColumn)));
//                }
//                predicates.add(criteriaBuilder.or(rolePredicates.toArray(Predicate[]::new)));
//            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }

        private String ignoreCharacters(String characters, String in) {
            String result = in;
            for (int i = 0; i < characters.length(); i++) {
                result = result.replace("" + characters.charAt(i), "");
            }
            return result;
        }

        private Expression<String> ignoreCharacters(String characters, CriteriaBuilder criteriaBuilder,
                                                    Expression<String> inExpression) {
            Expression<String> expression = inExpression;
            for (int i = 0; i < characters.length(); i++) {
                expression = criteriaBuilder.function("replace", String.class, expression,
                        criteriaBuilder.literal(characters.charAt(i)), criteriaBuilder.literal(""));
            }
            return expression;
        }

    }

}
