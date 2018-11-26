package vsim.gui.components;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import java.io.IOException;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXDecorator;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;


/**
 * This class represents a text input dialog.
 */
public final class InputDialog {

  /** Enter key combination */
  private static final KeyCodeCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);

  /** Dialog stage */
  private Stage stage;
  /** Dialog enter button */
  @FXML private JFXButton enter;
  /** Dialog text field */
  @FXML private JFXTextField text;

  /**
   * Creates an input dialog.
   */
  public InputDialog() {
    try {
      this.stage = new Stage();
      this.stage.setTitle("Enter your input...");
      this.stage.initModality(Modality.APPLICATION_MODAL);
      this.stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/img/favicon.png")));
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/input.fxml"));
      loader.setController(this);
      Parent root = loader.load();
      JFXDecorator decorator = new JFXDecorator(stage, root, false, false, false);
      this.stage.setResizable(false);
      this.stage.setScene(new Scene(decorator, 300, 140));
      this.enter.setOnAction(e -> this.stage.close());
      this.text.setOnKeyPressed(e -> {
        if (InputDialog.ENTER.match(e)) {
          this.stage.close();
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Shows input dialog and returns user input text.
   *
   * @return user input text
   */
  public String showAndWait() {
    this.stage.showAndWait();
    return text.getText();
  }

}
