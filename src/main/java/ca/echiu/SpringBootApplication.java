package ca.echiu;

import javafx.application.Application;
import org.springframework.context.annotation.ImportResource;

@org.springframework.boot.autoconfigure.SpringBootApplication
@ImportResource({"classpath*:application-context.xml"})
public class SpringBootApplication {

	public static void main(String[] args) {
		Application.launch(ConformanceDesktopTool.class, args);
	}

}
