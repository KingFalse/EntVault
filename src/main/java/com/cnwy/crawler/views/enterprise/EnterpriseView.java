package com.cnwy.crawler.views.enterprise;

import com.cnwy.crawler.data.Enterprise;
import com.cnwy.crawler.services.EnterpriseService;
import com.cnwy.crawler.util.VaadinUtil;
import com.cnwy.crawler.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
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

@PageTitle("国央企列表")
@Route(value = "enterprise", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class EnterpriseView extends Div {

    private final EnterpriseService enterpriseService;
    private final Filters filters;
    private Grid<Enterprise> grid;

    public EnterpriseView(EnterpriseService enterpriseService) {
        this.enterpriseService = enterpriseService;

        setSizeFull();
        addClassNames("enterprise-view");

        filters = new Filters(() -> refreshGrid());
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
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
        grid = new Grid<>(Enterprise.class, false);
        grid.addColumn("name").setResizable(true).setHeader("企业名称").setAutoWidth(true);
        grid.addColumn("alias").setResizable(true).setHeader("简称").setAutoWidth(true);
        grid.addColumn("website").setResizable(true).setHeader("官网").setAutoWidth(true);
        grid.addColumn("status").setResizable(true).setHeader("经营状态").setAutoWidth(true);
        grid.addColumn("type").setResizable(true).setHeader("平台企业类型标签").setAutoWidth(true);
        grid.addColumn("province").setResizable(true).setHeader("省").setAutoWidth(true);
        grid.addColumn("city").setResizable(true).setHeader("市").setAutoWidth(true);
        grid.addColumn("district").setResizable(true).setHeader("区/县").setAutoWidth(true);
        grid.addColumn("qccID").setResizable(true).setHeader("企查查ID").setAutoWidth(true);
        grid.addColumn("hitReason").setResizable(true).setHeader("入选原因").setAutoWidth(true);
        grid.addColumn(new LocalDateTimeRenderer<>(Enterprise::getUpdateTime, () -> VaadinUtil.dateTimeFormatter)).setHeader("更新时间").setSortable(true).setAutoWidth(true).setResizable(true);

        grid.setItems(query -> enterpriseService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

//    @Override
//    protected void onAttach(AttachEvent attachEvent) {
//        super.onAttach(attachEvent);
//        UI current = UI.getCurrent();
//        CompletableFuture.runAsync(() -> {
//            Long count = enterpriseService.count(filters);
//            current.access((Command) () -> {
//                Notification notification = new Notification("当前条件下企业总数为: " + count, 8000, Notification.Position.TOP_END);
//                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//                notification.open();
//            });
//        });
//    }

    public static class Filters extends Div implements Specification<Enterprise> {

        private final TextField name = new TextField("公司名称");
        private final TextField phone = new TextField("Phone");
        private final DatePicker startDate = new DatePicker("Date of Birth");
        private final DatePicker endDate = new DatePicker();
        private final MultiSelectComboBox<String> occupations = new MultiSelectComboBox<>("Occupation");
        private final CheckboxGroup<String> roles = new CheckboxGroup<>("Role");

        public Filters(Runnable onSearch) {

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            name.setPlaceholder("请输入关键词...");

            occupations.setItems("Insurance Clerk", "Mortarman", "Beer Coil Cleaner", "Scale Attendant");

            roles.setItems("Worker", "Supervisor", "Manager", "External");
            roles.addClassName("double-width");

            // Action buttons
            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                name.clear();
                phone.clear();
                startDate.clear();
                endDate.clear();
                occupations.clear();
                roles.clear();
                onSearch.run();
            });
            Button searchBtn = new Button("Search");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

//            add(name, phone, createDateRangeFilter(), occupations, roles, actions);
            add(name, actions);
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
        public Predicate toPredicate(Root<Enterprise> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

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
