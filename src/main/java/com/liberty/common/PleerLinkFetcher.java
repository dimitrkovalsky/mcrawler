package com.liberty.common;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by dkovalskyi on 04.07.2017.
 */
@Slf4j
public class PleerLinkFetcher {

    public static final String STREAM_URL = "http://pleer.net/site_api/files/get_url";

    public Optional<String> fetchLink(String id) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(STREAM_URL);
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36 OPR/45.0.2552.812");

            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("action", "play"));
            urlParameters.add(new BasicNameValuePair("id", id));

            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            HttpResponse response = client.execute(post);
            log.info("Response Code : " + response.getStatusLine().getStatusCode());
            String json = RequestHelper.readResult(response.getEntity().getContent());
            Optional<PleerResponse> optional = JsonHelper.toEntitySilently(json, PleerResponse.class);
            return optional.flatMap(x -> Optional.of(x.track_link));
        } catch (Exception e) {
            log.error("Can not fetch link : " + e.getMessage());
            return Optional.empty();
        }
    }

    @Data
    private static class PleerResponse {
        private Boolean success;
        private String residue_type;
        private Integer residue;
        private String residue_human;
        private Integer battery_charge;
        private String track_link;

    }
}
