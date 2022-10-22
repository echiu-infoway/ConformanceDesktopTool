package ca.echiu;

import ca.echiu.controller.MainWindowController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class ConformanceDesktopTool extends Application {
    private ConfigurableApplicationContext applicationContext;
    private static Stage stage;
    private static final String CONFORMANCE_DESKTOP_TOOL = "Conformance Desktop Tool";

    @Override
    public void start(Stage stage) {

        ConformanceDesktopTool.stage = stage;
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(MainWindowController.class);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(CONFORMANCE_DESKTOP_TOOL);
        stage.setMaximized(true);
        stage.show();


    }

    @Override
    public void init() {

        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.applicationContext = new SpringApplicationBuilder().sources(SpringBootApplication.class).run(args);

    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();

    }


}
