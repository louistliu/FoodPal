package client.scenes;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 * Custom ListCell class for instructions that handles dragging and dropping instructions.
 */
public class InstructionListCell extends ListCell<String> {

    /**
     * Constructs a new InstructionListCell and initializes the drag-and-drop event handlers.
     */
    public InstructionListCell() {
        setOnDragDetected(this::handleDragDetected);
        setOnDragOver(this::handleDragOver);
        setOnDragEntered(this::handleDragEntered);
        setOnDragExited(this::handleDragExited);
        setOnDragDropped(this::handleDragDropped);
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
            setText((getIndex() + 1) + ". " + item);
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
        event.consume();
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
     * It will resets opacity when the mouse leaves the cell.
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

            if (draggedIdx >= 0 && thisIdx >= 0 && draggedIdx != thisIdx) {
                String itemToMove = items.remove(draggedIdx);
                items.add(thisIdx, itemToMove);
                getListView().getSelectionModel().select(thisIdx);
                success = true;
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }
}