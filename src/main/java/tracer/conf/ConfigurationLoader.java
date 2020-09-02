package tracer.conf;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ConfigurationLoader {
    private final String FOLDER = "conf/";
    private List<String> resources = new LinkedList<>();
    private Properties properties = new Properties();
    private static class SingletonHelper {
        private static final ConfigurationLoader INSTANCE = new ConfigurationLoader();
    }
    public static ConfigurationLoader getInstance() {
        return SingletonHelper.INSTANCE;
    }
    private ConfigurationLoader() {
        initialize();
        loadConfiguration();
    }
    private void loadConfiguration() {
        for (String resource : resources) {
            loadConfiguration(resource);
        }
    }
    private void loadConfiguration(String resource) {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader bf = null;
        try {
            File file = new File(resource);
            if (!file.exists()) {
                return;
            }
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            bf = new BufferedReader(isr);
            properties.load(bf);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bf != null) {
                    bf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void initialize() {
        addResource("default-config.cfg");
    }
    private void addResource(String s) {
        String resource = FOLDER + s;
        if (!this.resources.contains(resource)) {
            this.resources.add(resource);
        }
    }
    public void addExternalResources(String... s) {
        for (String str : s) {
            String resource = FOLDER + str;
            if (!this.resources.contains(resource)) {
                this.resources.add(resource);
                this.loadConfiguration(resource);
            }
        }
    }
    public Object get(String key, Object o) {
        return properties.getOrDefault(key, o);
    }
    public int getAsInteger(String key, int defaultNumb) {
        String trimmer = this.getTrimmed(key);
        if (trimmer != null) {
            return Integer.parseInt(properties.getProperty(key));
        } else {
            return defaultNumb;
        }
    }
    public String getAsString(String key, String defaultStr) {
        String value = this.getTrimmed(key);
        return value == null ? defaultStr : value;
    }
    public long getAsLong(String key, long defaultNumb) {
        String trimmer = this.getTrimmed(key);
        if (trimmer != null) {
            return Long.parseLong(properties.getProperty(key));
        } else {
            return defaultNumb;
        }
    }
    public double getAsDouble(String key, double defaultNumb) {
        String trimmer = this.getTrimmed(key);
        if (trimmer != null) {
            return Double.parseDouble(properties.getProperty(key));
        } else {
            return defaultNumb;
        }
    }
    public float getAsFloat(String key, float defaultNumb) {
        String trimmer = this.getTrimmed(key);
        return trimmer != null ? Float.parseFloat(properties.getProperty(key)) : defaultNumb;
    }
    public boolean getAsBoolean(String key, boolean defaultValue) {
        String valueString = this.getTrimmed(key);
        if (null != valueString && !valueString.isEmpty()) {
            valueString = valueString.toLowerCase();
            if ("true".equals(valueString)) {
                return true;
            } else {
                return !"false".equals(valueString) && defaultValue;
            }
        } else {
            return defaultValue;
        }
    }
    public String getTrimmed(String name) {
        String value = this.properties.getProperty(name);
        return null == value ? null : value.trim();
    }
}
