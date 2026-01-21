package client.scenes;

import com.google.inject.Inject;
import java.util.Optional;
import java.util.function.Function;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 * Custom ListCell class for instructions that handles dragging and dropping instructions.
 */
public class InstructionListCell extends ListCell<String> {

    private final ContextMenu contextMenu;
    private final Runnable save;
    private final LanguageController languageController;
    private final MenuItem moveUp;   // Move these out of the constructor
    private final MenuItem moveDown; // so they are class-level fields
    private final MenuItem edit;

    /**
     * Constructs a new InstructionListCell and initializes the drag-and-drop event handlers.
     * Also creates menu for manually moving instructions with right-clicking.
     */
    @Inject
    public InstructionListCell(Runnable saveFunc, Function<String,
            Optional<String>> editInstruction, LanguageController languageController) {
        this.languageController = languageController;
        setOnDragDetected(this::handleDragDetected);
        setOnDragOver(this::handleDragOver);
        setOnDragEntered(this::handleDragEntered);
        setOnDragExited(this::handleDragExited);
        setOnDragDropped(this::handleDragDropped);

        this.save = saveFunc;

        contextMenu = new ContextMenu();
        moveUp = new MenuItem();
        moveUp.setOnAction(event -> moveInstruction(-1));
        moveDown = new MenuItem();
        moveDown.setOnAction(event -> moveInstruction(1));

        edit = new MenuItem();
        edit.setOnAction(event -> this.handleEdit(event, editInstruction));


        contextMenu.getItems().addAll(moveUp, moveDown, edit);
    }

    /**
     * Handles instruction editing.
     * If the updated instruction is not null, updates instruction text,
     * however if that instruction has been removed by another client adds the new one instead.
     * Otherwise, does nothing.
     *
     * @param event           event that triggered this method data.
     * @param editInstruction function to call to get the new content of instruction.
     */
    private void handleEdit(ActionEvent event, Function<String, Optional<String>> editInstruction) {
        int index = getIndex();
        ObservableList<String> items = getListView().getItems();

        String item = items.get(index);
        editInstruction.apply(item)
              .ifPresentOrElse(s -> {
                  int newIndex = items.indexOf(item);

                  if (newIndex == -1) {
                      items.add(s);
                      return;
                  }
                  items.remove(newIndex);
                  items.add(newIndex, s);
                  getListView().getSelectionModel().select(newIndex);
                  save.run();
              }, () -> {
              });
    }


    /**
     * Updates the item number and adds a number prefix to the instruction text.
     *
     * @param item  The instruction string.
     * @param empty Whether the cell is empty.
     */
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            moveUp.setText(languageController.get("menu.moveUp"));
            moveDown.setText(languageController.get("menu.moveDown"));
            edit.setText(languageController.get("menu.edit"));

            setText((getIndex() + 1) + ". " + item);
            setContextMenu(contextMenu);
        }
    }

    /**
     * Moves the item at the current index by the given direction.
     *
     * @param direction -1 for up, 1 for down.
     */
    private void moveInstruction(int direction) {
        int index = getIndex();
        ObservableList<String> items = getListView().getItems();
        int newIndex = index + direction;

        if (newIndex >= 0 && newIndex < items.size()) {
            String item = items.remove(index);
            items.add(newIndex, item);
            getListView().getSelectionModel().select(newIndex);
            save.run();
        }
    }

    /**
     * Handles the DragDetected event.
     * It will initiate the drag-and-drop operation if the cell is not empty.
     *
     * @param event The MouseEvent triggering the drag.
     */
    private void handleDragDetected(MouseEvent event) {
        if (getItem() == null) {
            return;
        }
        Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString(getItem());
        dragboard.setContent(content);
        event.consume();
    }

    /**
     * Handles the DragOver event.
     * It will accept the transfer mode if the source is not this cell and data exists.
     *
     * @param event The DragEvent.
     */
    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != this && event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
    }

    /**
     * Handles the DragEntered event.
     * It will change the opacity to indicate a valid drop target.
     *
     * @param event The DragEvent.
     */
    private void handleDragEntered(DragEvent event) {
        if (event.getGestureSource() != this && event.getDragboard().hasString()) {
            setOpacity(0.5);
        }
    }

    /**
     * Handles the DragExited event.
     * It will reset opacity when the mouse leaves the cell.
     *
     * @param event The DragEvent.
     */
    private void handleDragExited(DragEvent event) {
        if (event.getGestureSource() != this && event.getDragboard().hasString()) {
            setOpacity(1);
        }
    }

    /**
     * Handles the DragDropped event.
     * It will swap the dragged item with the item at the current position in the list.
     *
     * @param event The DragEvent.
     */
    private void handleDragDropped(DragEvent event) {
        if (getItem() == null) {
            return;
        }

        Dragboard dragboard = event.getDragboard();
        boolean success = false;

        if (dragboard.hasString()) {
            ObservableList<String> items = getListView().getItems();
            int draggedIdx = getListView().getSelectionModel().getSelectedIndex();
            int thisIdx = getIndex();

            if (draggedIdx != thisIdx) {
                String itemToMove = items.remove(draggedIdx);
                items.add(thisIdx, itemToMove);
                getListView().getSelectionModel().select(thisIdx);
                success = true;
                save.run();
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }
}