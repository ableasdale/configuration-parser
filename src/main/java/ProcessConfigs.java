import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Iterator;

public class ProcessConfigs {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        Configurations configs = new Configurations();

        processConfigs("server", configs);
        processConfigs("zookeeper", configs);
        processConfigs("kafka-rest", configs);
        processConfigs("schema-registry", configs);
        processConfigs("control-center", configs);
        processConfigs("connect-avro-distributed", configs);
    }

    private static void processConfigs(String filename, Configurations configs) {
        LOG.info(String.format("Processing: %s properties\n======", filename));
        StringBuilder sb = new StringBuilder();
        sb.append("Property Name\tProperty\tOriginal Property\tMatch?\n");
        try {
            Configuration config = configs.properties(new File(String.format("src/main/resources/%s.properties", filename)));
            Configuration configOrig = configs.properties(new File(String.format("src/main/resources/611/%s.properties", filename)));
            Iterator<String> keys = config.getKeys();

            while (keys.hasNext()) {
                String s = keys.next();
                String s2 = config.getString(s);
                String s3 = configOrig.getString(s);
                sb.append(s).append("\t").append(s2).append("\t").append(s3).append("\t").append(s2.equals(s3) ? "match" : "no-match").append("\n");
                if (!s2.equals(s3)) {
                    LOG.info(String.format("%s - %s - %s - %s", s, s2, s3, "no-match"));
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(String.format("src/main/resources/%s.csv", filename)));
            writer.write(sb.toString());
            writer.close();
        } catch (ConfigurationException | IOException e) {
            LOG.error(String.format("Error encountered whilst processing %s: ", filename), e);
        }
    }
}
