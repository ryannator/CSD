package csd.tariff.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

    @Component
    public class StartupRunner implements CommandLineRunner {

        @Override
        public void run(String... args) throws Exception {
            // This will be executed when the application starts
            System.out.println("Application has started...");
        }
    }

    // private static final Logger log = LoggerFactory.getLogger(SwDesignApplication.class);

    // public static void main(String[] args) {
		
	// 	ApplicationContext ctx = SpringApplication.run(SwDesignApplication.class, args);

    //     // print out the book's info to the terminal
	// 	BookController controller = ctx.getBean(BookController.class);
    //     List<Book> books = controller.getBooks();
    //     for(Book book : books){
    //         log.info("Book: " + book.getTitle());
    //     }

    	// TODO: Update based on Tariff project requirements
    //     // Use BookClient which makes use of Spring's RestTemplate to consume the web service
    //     BookClient client = ctx.getBean(BookClient.class);

    //     // Our web service endpoint
    //     String URI = "http://localhost:8080/books";

    //     // Perform a GET request
    //     Book book = client.getBook(URI, 1L);
    //     log.info("[RestTemplate] GET book: " + book.getTitle());

    //     // Perform a POST request to add a new book
    //     Book newBook = new Book();
    //     newBook.setTitle("Gone With The Wind");
    //     Book returnedBook = client.addBook(URI, newBook);
    //     log.info("[RestTemplate] POST book: " + returnedBook.getTitle());
    // }
}
