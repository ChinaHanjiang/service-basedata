package hu.tilos.radio.backend.mongoconverters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class ScriptExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptExecutor.class);

    public void logStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                LOG.info(line);
            }
        }
    }

    public void executeScript(String script, String workdir, String type) {
        Path scriptPath = Paths.get("/tmp/" + type + ".sh");
        try (BufferedWriter writer = Files.newBufferedWriter(scriptPath)) {
            writer.write(script);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        scriptPath.toFile().setExecutable(true);
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", scriptPath.toString());
        processBuilder.directory(new File(workdir));
        try {

            Process process = processBuilder.start();
            logStream(process.getInputStream());
            logStream(process.getErrorStream());
            int i = process.waitFor();
            if (i > 0) {
                LOG.error("generation script stopped with code " + i);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
