package hu.tilos.radio.backend;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import hu.tilos.radio.backend.mongoconverters.FromLocalDate;
import hu.tilos.radio.backend.mongoconverters.FromLocalDateTime;
import hu.tilos.radio.backend.mongoconverters.ToLocalDate;
import hu.tilos.radio.backend.mongoconverters.ToLocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;

@Configuration
@EnableMongoRepositories()
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Value("${mongo.host}")
    private String mongoHost;

    @Value("${mongo.db}")
    private String mongoDb;

    @Override
    protected String getDatabaseName() {
        return mongoDb;
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(mongoHost);
    }

    @Override
    public CustomConversions customConversions() {
        return new CustomConversions(Arrays.asList(new FromLocalDateTime(), new ToLocalDateTime(), new FromLocalDate(), new ToLocalDate()));
    }
}