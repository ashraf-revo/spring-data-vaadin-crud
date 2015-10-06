package org.revo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;
import org.revo.domain.Person;
import org.revo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.viritin.SortableLazyList;
import org.vaadin.viritin.button.ConfirmButton;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 *
 */
@Title("PhoneBook CRUD example")
@Theme("valo")
@SpringUI
public class MainUI extends UI {

    @Autowired
    PersonRepository repo;

    private MTable<Person> list = new MTable<>(Person.class)
            .withProperties("id", "name", "email")
            .withColumnHeaders("id", "Name", "Email")
            .setSortableProperties("name", "email")
            .withFullWidth();

    private Button addNew = new MButton(FontAwesome.PLUS, this::add);
    private Button edit = new MButton(FontAwesome.PENCIL_SQUARE_O, this::edit);
    private Button delete = new ConfirmButton(FontAwesome.TRASH_O,
            "Are you sure you want to delete the entry?", this::remove);

    @Override
    protected void init(VaadinRequest request) {
        setContent(
                new MVerticalLayout(
                        new RichText().withMarkDownResource("/welcome.md"),
                        new MHorizontalLayout(addNew, edit, delete),
                        list
                ).expand(list)
        );
        listEntities();
        list.addMValueChangeListener(e -> adjustActionButtonState());
    }

    protected void adjustActionButtonState() {
        boolean hasSelection = list.getValue() != null;
        edit.setEnabled(hasSelection);
        delete.setEnabled(hasSelection);
    }

    static final int PAGESIZE = 45;

    private void listEntities() {
        list.setBeans(new SortableLazyList<>(
                (firstRow, asc, sortProperty) -> {
                    PageRequest pageable = new PageRequest(firstRow / PAGESIZE, PAGESIZE,
                            asc ? Sort.Direction.ASC : Sort.Direction.DESC,
                            sortProperty == null ? "id" : sortProperty);
                    return repo.findAllBy(pageable);
                },
                () -> (int) repo.count(),
                PAGESIZE
        ));
        adjustActionButtonState();
    }

    public void add(ClickEvent clickEvent) {
        edit(new Person());
    }

    public void edit(ClickEvent e) {
        edit(list.getValue());
    }

    public void remove(ClickEvent e) {
        repo.delete(list.getValue());
        list.setValue(null);
        listEntities();
    }

    protected void edit(final Person phoneBookEntry) {
        PhoneBookEntryForm phoneBookEntryForm = new PhoneBookEntryForm(
                phoneBookEntry);
        phoneBookEntryForm.openInModalPopup();
        phoneBookEntryForm.setSavedHandler(this::saveEntry);
        phoneBookEntryForm.setResetHandler(this::resetEntry);
    }

    public void saveEntry(Person entry) {
        repo.save(entry);
        listEntities();
        closeWindow();
    }

    public void resetEntry(Person entry) {
        listEntities();
        closeWindow();
    }

    protected void closeWindow() {
        getWindows().stream().forEach(w -> removeWindow(w));
    }

}
