package dk.nversion.javascriptrunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavaScriptRunner {
    private final ScriptEngineManager manager;
    private final ScriptEngine engine;
    private final Invocable invocable;
    private final JsonProvider jsonProvider;
    private final ObjectMapper mapper;
    
    public interface JsonProvider {
        public Object fromJson(String json);
        public String toJson(Object object);
    }
    
    public JavaScriptRunner() throws ScriptException {
        manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");
        invocable = (Invocable) engine;
        mapper = new ObjectMapper();
        
        // Create utility methods to do JSON conversion
        engine.eval("var jsonProvider = new Object();\n"
            + "jsonProvider.fromJson = function(json) { return JSON.parse(json); };\n"
            + "jsonProvider.toJson = function(obj) { return JSON.stringify(obj); };\n");
        
        // load JSON provider class
        jsonProvider = invocable.getInterface(engine.get("jsonProvider"), JsonProvider.class);
    }
    
    public void eval(String script) throws ScriptException {
        engine.eval(script);
    }
    
    public <T> T get(String var, Class<T> type) throws IOException {
        Object jsobj = engine.get(var);
        // Convert to JSON in JavaScript engine
        String json = jsonProvider.toJson(jsobj);
        // Map to type with Jackson
        return mapper.readValue(json, type);        
    }
    
    public <T> T invoke(String name, Class<T> type, Object... args) throws IOException, ScriptException, NoSuchMethodException {
        Object[] jsargs = new Object[args.length];
        for(int i = 0; i < args.length; i++) {
            // Convert to JSON with Jackson
            String json = mapper.writeValueAsString(args[i]);
            // Convert to Object in JavaScript engine
            jsargs[i] = jsonProvider.fromJson(json);
        }
        Object jsobj = invocable.invokeFunction(name, jsargs);
        String json = jsonProvider.toJson(jsobj);
        return mapper.readValue(json, type);        
    }
}
