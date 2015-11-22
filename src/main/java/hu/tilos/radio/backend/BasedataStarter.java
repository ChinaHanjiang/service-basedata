package hu.tilos.radio.backend;

import akka.actor.ActorSystem;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.mongodb.DB;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.author.AuthorService;
import hu.tilos.radio.backend.author.AuthorToSave;
import hu.tilos.radio.backend.author.GetAuthorCommand;
import hu.tilos.radio.backend.author.ListAuthorCommand;
import hu.tilos.radio.backend.bus.MessageBus;
import hu.tilos.radio.backend.show.MailToShow;
import hu.tilos.radio.backend.show.ShowService;
import hu.tilos.radio.backend.show.ShowToSave;
import hu.tilos.radio.backend.spark.GuiceConfigurationListener;
import hu.tilos.radio.backend.spark.JsonTransformer;
import hu.tilos.radio.backend.spark.SparkDefaults;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import scala.util.Try;

import javax.inject.Inject;
import javax.validation.Validator;

import static spark.Spark.*;

public class BasedataStarter {

    private static final Logger LOG = LoggerFactory.getLogger(BasedataStarter.class);

    private Gson gson;

    static Injector injector;

    private FiniteDuration timeout;

    @Inject
    AuthorService authorService;

    @Inject
    ShowService showService;


    @Inject
    @Configuration(name = "port.basedata")
    private int portBasedata;

    MessageBus bus;

    public static void main(String[] args) {
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(DB.class).toProvider(MongoProducer.class);
                bind(DozerBeanMapper.class).toProvider(DozerFactory.class).asEagerSingleton();
                bind(Validator.class).toProvider(ValidatorProducer.class);
                bindListener(Matchers.any(), new GuiceConfigurationListener());
                bind(Integer.class).toInstance(1);

            }
        });

        injector.getInstance(BasedataStarter.class).run();

    }

    private void run() {
        ActorSystem system = ActorSystem.create("TilosBus");
        bus = new MessageBus(system, injector);

        timeout = Duration.create(5, "seconds");

        LOG.info("Starting new deployment");

        SparkDefaults spark = new SparkDefaults(portBasedata, injector);

        JsonTransformer jsonResponse = spark.getJsonTransformer();

        get("/api/v1/author", (req, res) -> authorService.list(), jsonResponse);


        get("/api/v1/authorx", (req, res) -> {
            Object result = Await.result(bus.tell(new ListAuthorCommand()), timeout);
            if (result instanceof Try) {
                return ((Try) result).get();
            }
            return result;
        }, jsonResponse);

        get("/api/v1/author/:alias", (req, res) -> authorService.get(req.params("alias"), null), jsonResponse);


        get("/api/v1/authorx/:alias", (req, res) -> {
            Object result = Await.result(bus.tell(new GetAuthorCommand(req.params("alias"))), timeout);
            if (result instanceof Try) {
                return ((Try) result).get();
            }
            return result;
        }, jsonResponse);

        post("/api/v1/author", spark.authorized(Role.ADMIN, (req, res, session) -> authorService.create(gson.fromJson(req.body(), AuthorToSave.class))), jsonResponse);

        put("/api/v1/author/:alias",
                spark.authorized("/author/{alias}", (req, res, session) ->
                        authorService.update(req.params("alias"), gson.fromJson(req.body(), AuthorToSave.class))), jsonResponse);

        get("/api/v1/show", (req, res) ->
                showService.list(req.queryParams("status")), jsonResponse);
        get("/api/v1/show/:alias", (req, res) ->
                showService.get(req.params("alias")), jsonResponse);

        post("/api/v1/show",
                spark.authorized(Role.ADMIN, (req, res, session) ->
                        showService.create(gson.fromJson(req.body(), ShowToSave.class))), jsonResponse);
        put("/api/v1/show/:alias",
                spark.authorized("/show/{alias}", (req, res, session) ->
                        showService.update(req.params("alias"), gson.fromJson(req.body(), ShowToSave.class))), jsonResponse);

        post("/api/v1/show/:alias/contact", (req, res) ->
                showService.contact(req.params("alias"), gson.fromJson(req.body(), MailToShow.class)), jsonResponse);


    }


}
