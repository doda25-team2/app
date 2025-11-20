package frontend.ctrl;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ApiDocsProxyController {

    private final RestTemplateBuilder restTemplateBuilder;
    private final String modelServiceUrl;

    public ApiDocsProxyController(RestTemplateBuilder restTemplateBuilder,
            @Value("${model.service.url}") String modelServiceUrl) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.modelServiceUrl = modelServiceUrl;
    }

    @GetMapping({ "/apidocs", "/apidocs/", "/apidocs/**", "/flasgger_static/**", "/swaggerui/**", "/apispec_*", "/apispec*", "/swagger.json" })
    public ResponseEntity<byte[]> proxyApiDocs(HttpServletRequest request) {
        try {
            var path = request.getRequestURI();
            var query = request.getQueryString();
            var target = modelServiceUrl;
            if (target.endsWith("/")) {
                target = target.substring(0, target.length() - 1);
            }
            var url = target + path;
            if (query != null && !query.isEmpty()) {
                url += "?" + query;
            }

            var rest = restTemplateBuilder.build();
            var resp = rest.exchange(new URI(url), HttpMethod.GET, null, byte[].class);

            HttpHeaders headers = new HttpHeaders();
            var ct = resp.getHeaders().getContentType();
            if (ct != null) {
                headers.setContentType(ct);
            } else {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            }
            return new ResponseEntity<>(resp.getBody(), headers, resp.getStatusCode());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error proxying /apidocs: " + e.getMessage()).getBytes());
        }
    }
}
