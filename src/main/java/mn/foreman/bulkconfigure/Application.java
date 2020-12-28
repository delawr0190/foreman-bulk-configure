package mn.foreman.bulkconfigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** The primary application entry point. */
@SpringBootApplication
public class Application {

    /**
     * Main.
     *
     * @param args The command line args.
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
