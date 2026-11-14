package it.lab;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
public class LabController {
    private static final Logger LOG = LoggerFactory.getLogger(LabController.class);
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    private static final List<String> TRACE_HEADERS = List.of("traceparent","tracestate","b3","x-b3-traceid","x-b3-spanid","x-b3-parentspanid","x-b3-sampled","x-b3-flags");
    @Value("${app.role}") private String role;
    @Value("${app.message}") private String message;
    @Value("${app.demo-key}") private String demoKey;
    @Value("${app.backend-url}") private String backendUrl;
    @Value("${app.storage-enabled}") private boolean storageEnabled;
    @Value("${app.data-dir}") private String dataDir;

    @GetMapping("/api/info")
    public Map<String,Object> info() {
        return Map.of("role",role,"version",ReleaseMarker.VERSION,"message",message,"secretConfigured",!demoKey.isBlank());
    }
    private String requestId(HttpServletRequest request) {
        String id=request.getHeader("x-request-id");
        return id==null || id.isBlank() ? UUID.randomUUID().toString() : id;
    }
    @GetMapping("/api/products")
    public Map<String,Object> products(HttpServletRequest request) {
        String id=requestId(request);
        LOG.info("products requestId={} version={}",id,ReleaseMarker.VERSION);
        return Map.of("version",ReleaseMarker.VERSION,"requestId",id,"products",List.of(
            Map.of("id","P001","name","Corso Java","stock",5,"status",CatalogRules.status(5)),
            Map.of("id","P002","name","Corso OpenShift","stock",0,"status",CatalogRules.status(0))));
    }
    @GetMapping("/api/catalog")
    public ResponseEntity<String> catalog(HttpServletRequest request) {
        String id=requestId(request);
        try {
            HttpRequest.Builder b=HttpRequest.newBuilder(URI.create(backendUrl+"/api/products"))
                .timeout(Duration.ofSeconds(5)).header("x-request-id",id).GET();
            for(String name:TRACE_HEADERS) { String value=request.getHeader(name); if(value!=null) b.header(name,value); }
            var response=client.send(b.build(),HttpResponse.BodyHandlers.ofString());
            LOG.info("catalog requestId={} upstreamStatus={}",id,response.statusCode());
            return ResponseEntity.status(response.statusCode()).header("Content-Type",response.headers().firstValue("content-type").orElse("text/plain"))
                .header("x-request-id",id).body(response.body());
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Chiamata interrotta");
        } catch(Exception e) {
            LOG.warn("catalog requestId={} error={}",id,e.getClass().getSimpleName());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Backend non raggiungibile");
        }
    }
    private Path counterPath() {
        if(!storageEnabled) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Storage demo disabilitato");
        return Path.of(dataDir,"counter.txt");
    }
    @GetMapping("/api/counter")
    public synchronized Map<String,Long> readCounter() throws java.io.IOException {
        Path p=counterPath();
        return Map.of("counter",Files.exists(p)?Long.parseLong(Files.readString(p).trim()):0L);
    }
    @PostMapping("/api/counter")
    public synchronized Map<String,Long> incrementCounter() throws java.io.IOException {
        Path p=counterPath();Files.createDirectories(p.getParent());
        long value=Files.exists(p)?Long.parseLong(Files.readString(p).trim())+1:1;
        Path tmp=Files.createTempFile(p.getParent(),"counter-",".tmp");
        Files.writeString(tmp,Long.toString(value));
        Files.move(tmp,p,StandardCopyOption.REPLACE_EXISTING);
        return Map.of("counter",value);
    }
}
